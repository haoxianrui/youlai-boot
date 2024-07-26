package com.youlai.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.system.SystemApplication;
import com.youlai.system.config.property.GeneratorProperties;
import com.youlai.system.converter.GenConfigConverter;
import com.youlai.system.exception.BusinessException;
import com.youlai.system.mapper.DatabaseMapper;
import com.youlai.system.model.bo.ColumnMetaData;
import com.youlai.system.model.bo.TableMetaData;
import com.youlai.system.model.entity.GenConfig;
import com.youlai.system.model.entity.GenFieldConfig;
import com.youlai.system.model.form.GenConfigForm;
import com.youlai.system.model.query.TablePageQuery;
import com.youlai.system.model.vo.GeneratorPreviewVO;
import com.youlai.system.service.GeneratorService;
import com.youlai.system.service.GenConfigService;
import com.youlai.system.service.GenFieldConfigService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

/**
 * 数据库服务实现类
 *
 * @author Ray
 * @since 2.10.0
 */
@Service
@RequiredArgsConstructor
public class GeneratorServiceImpl implements GeneratorService {

    @Value("${spring.application.name}")
    private String applicationName;

    private final DatabaseMapper databaseMapper;
    private final GeneratorProperties generatorProperties;
    private final GenConfigService genConfigService;
    private final GenFieldConfigService genFieldConfigService;

    private final GenConfigConverter genConfigConverter;

    /**
     * 数据表分页列表
     *
     * @param queryParams 查询参数
     * @return 分页结果
     */
    public Page<TableMetaData> getTablePage(TablePageQuery queryParams) {
        Page<TableMetaData> page = new Page<>(queryParams.getPageNum(), queryParams.getPageSize());
        return databaseMapper.getTablePage(page, queryParams);
    }

    /**
     * 获取代码生成配置
     *
     * @param tableName 表名 eg: sys_user
     * @return 代码生成配置
     */
    @Override
    public GenConfigForm getGenConfig(String tableName) {
        // 查询表生成配置
        GenConfig genConfig = genConfigService.getOne(
                new LambdaQueryWrapper<>(GenConfig.class)
                        .eq(GenConfig::getTableName, tableName)
                        .last("LIMIT 1")
        );
        // 如果没有代码生成配置，则根据表的元数据生成默认配置
        if (genConfig == null) {
            TableMetaData tableMetadata = databaseMapper.getTableMetadata(tableName);
            Assert.isTrue(tableMetadata != null, "未找到表元数据");


            genConfig = new GenConfig();
            genConfig.setTableName(tableName);

            String tableComment = tableMetadata.getTableComment();
            if (StrUtil.isNotBlank(tableComment)) {
                genConfig.setBusinessName(tableComment.replace("表", ""));
            }
            // 实体类名 = 表名去掉前缀后转驼峰，前缀默认为下划线分割的第一个元素
            String entityName = StrUtil.toCamelCase(StrUtil.removePrefix(tableName, tableName.split("_")[0]));
            genConfig.setEntityName(entityName);

            String packageName = SystemApplication.class.getPackageName();
            genConfig.setPackageName(packageName);

        }


        List<GenFieldConfig> genFieldConfigs = null;

        // 获取表的列信息
        List<ColumnMetaData> tableColumns = databaseMapper.getTableColumns(tableName);

        if (CollectionUtil.isNotEmpty(tableColumns)) {

            // 查询字段生成配置
            List<GenFieldConfig> configList = genFieldConfigService.list(
                    new LambdaQueryWrapper<>(GenFieldConfig.class)
                            .eq(GenFieldConfig::getConfigId, genConfig.getId())
            );
            genFieldConfigs = new ArrayList<>();
            for (ColumnMetaData tableColumn : tableColumns) {
                GenFieldConfig fieldConfig = new GenFieldConfig();
                fieldConfig.setFieldName(tableColumn.getColumnName());
                fieldConfig.setFieldType(tableColumn.getDataType());
                fieldConfig.setComment(tableColumn.getColumnComment());


                // 如果没有字段生成配置，则根据表的元数据生成默认配置
                if (CollectionUtil.isNotEmpty(configList)) {
                    for (GenFieldConfig config : configList) {
                        if (StrUtil.equals(config.getFieldName(), fieldConfig.getFieldName())) {
                            fieldConfig = config;
                            break;
                        }
                    }
                }

                genFieldConfigs.add(fieldConfig);
            }
        }

        GenConfigForm configFormData = genConfigConverter.toGenConfigForm(genConfig, genFieldConfigs);
        return configFormData;
    }

    @Override
    public void saveGenConfig(GenConfigForm formData) {
        GenConfig genConfig = genConfigConverter.toGenConfigEntity(formData);
        genConfigService.saveOrUpdate(genConfig);

        List<GenFieldConfig> genFieldConfigs = genConfigConverter.toGenFieldConfigEntity(formData.getFieldConfigs());

        if (CollectionUtil.isEmpty(genFieldConfigs)) {
            throw new BusinessException("字段配置不能为空");
        }
        genFieldConfigs.forEach(genFieldConfig -> {
            genFieldConfig.setConfigId(genConfig.getId());
        });
        genFieldConfigService.saveOrUpdateBatch(genFieldConfigs);
    }


    /**
     * 获取预览生成代码
     *
     * @param tableName 表名
     * @return 预览数据
     */
    @Override
    public List<GeneratorPreviewVO> getTablePreviewData(String tableName) {

        List<GeneratorPreviewVO> list = new ArrayList<>();

        GenConfig genConfig = genConfigService.getOne(new LambdaQueryWrapper<GenConfig>()
                .eq(GenConfig::getTableName, tableName)
        );
        Assert.isTrue(genConfig != null, "未找到表生成配置");

        List<GenFieldConfig> fieldConfigs = genFieldConfigService.list(new LambdaQueryWrapper<GenFieldConfig>()
                .eq(GenFieldConfig::getConfigId, genConfig.getId())
        );
        Assert.isTrue(CollectionUtil.isNotEmpty(fieldConfigs), "未找到字段生成配置");

        // 遍历模板配置
        Map<String, GeneratorProperties.TemplateConfig> templateConfigs = generatorProperties.getTemplateConfigs();
        for (Map.Entry<String, GeneratorProperties.TemplateConfig> templateConfigEntry : templateConfigs.entrySet()) {
            GeneratorPreviewVO previewVO = new GeneratorPreviewVO();

            GeneratorProperties.TemplateConfig templateConfig = templateConfigEntry.getValue();

            /* 1. 生成文件名 UserController */
            // User Role Menu Dept
            String entityName = genConfig.getEntityName();
            // Controller Service Mapper Entity
            String templateName = templateConfigEntry.getKey();
            // .java .ts .vue
            String extension = templateConfig.getExtension();

            // 文件名 UserController.java
            String fileName = getFileName(entityName, templateName, extension);
            previewVO.setFileName(fileName);


            /* 2. 生成文件路径 */
            // com.youlai.system
            String packageName = genConfig.getPackageName();
            // controller
            String subPackageName = templateConfig.getPackageName();
            // 文件路径 com.youlai.system.controller
            String filePath = getFilePath(templateName, packageName, subPackageName);
            previewVO.setPath(filePath);

            /* 3. 生成文件内容 */

            // 生成文件内容
            String content = getCodeContent(templateConfig, genConfig, fieldConfigs);
            previewVO.setContent(content);


            list.add(previewVO);
        }
        return list;
    }


    private String getFileName(String entityName, String templateName, String extension) {
        if (templateName.equals("Entity")) {
            return entityName + extension;
        }
        if (templateName.equals("MapperXml")) {
            return entityName + "Mapper" + extension;
        }
        if (templateName.equals("API") || templateName.equals("VIEW")) {
            return StrUtil.toSymbolCase(entityName, '-') + extension;
        }
        return entityName + templateName + extension;
    }

    private String getFilePath(String templateName, String packageName, String subPackageName) {
        String path;
        if (templateName.equals("MapperXml")) {
            path = (applicationName
                    + File.separator
                    + "src" + File.separator + "main" + File.separator + "resources"
                    + File.separator + subPackageName
            ).replace(".", File.separator);
        } else if (templateName.equals("API") || templateName.equals("VIEW")) {
            path = ("vue3-element-admin"
                    + File.separator
                    + "src" + File.separator + subPackageName
            ).replace(".", File.separator);
        } else {
            path = (applicationName
                    + File.separator
                    + "src" + File.separator + "main" + File.separator + "java"
                    + File.separator + packageName + File.separator + subPackageName
            ).replace(".", File.separator);
        }
        return path;
    }

    /**
     * 生成代码内容
     *
     * @param templateConfig 模板配置
     * @param genConfig      生成配置
     * @param fieldConfigs   字段配置
     * @return 代码内容
     */
    private String getCodeContent(GeneratorProperties.TemplateConfig templateConfig, GenConfig genConfig, List<GenFieldConfig> fieldConfigs) {

        Map<String, Object> bindMap = new HashMap<>();

        String entityName = genConfig.getEntityName();

        bindMap.put("package", genConfig.getPackageName());
        bindMap.put("subPackage", templateConfig.getPackageName());
        bindMap.put("date", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm"));
        bindMap.put("entityName", entityName);
        bindMap.put("tableName", genConfig.getTableName());
        bindMap.put("author", genConfig.getAuthor());
        bindMap.put("lowerFirstEntityName", StrUtil.lowerFirst(entityName));
        bindMap.put("tableComment", StrUtil.replace(genConfig.getComment(), "表", Strings.EMPTY));
        bindMap.put("fieldConfigs", fieldConfigs);

        for (GenFieldConfig fieldConfig : fieldConfigs) {
            bindMap.put("hasLocalDateTime", "LocalDateTime".equals(fieldConfig.getFieldType()));
            bindMap.put("hasBigDecimal", "BigDecimal".equals(fieldConfig.getFieldType()));
            bindMap.put("hasRequiredField", ObjectUtil.equals(fieldConfig.getIsRequired(), 1));
        }

        TemplateEngine templateEngine = TemplateUtil.createEngine(new TemplateConfig("templates", TemplateConfig.ResourceMode.CLASSPATH));
        Template template = templateEngine.getTemplate(templateConfig.getTemplatePath());
        String content = template.render(bindMap);

        return content;
    }


}
