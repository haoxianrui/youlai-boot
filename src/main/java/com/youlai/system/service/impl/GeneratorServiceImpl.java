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
import com.youlai.system.enums.FormTypeEnum;
import com.youlai.system.enums.JavaTypeEnum;
import com.youlai.system.enums.QueryTypeEnum;
import com.youlai.system.exception.BusinessException;
import com.youlai.system.mapper.DatabaseMapper;
import com.youlai.system.model.bo.ColumnMetaData;
import com.youlai.system.model.bo.TableMetaData;
import com.youlai.system.model.entity.GenConfig;
import com.youlai.system.model.entity.GenFieldConfig;
import com.youlai.system.model.form.GenConfigForm;
import com.youlai.system.model.query.TablePageQuery;
import com.youlai.system.model.vo.GeneratorPreviewVO;
import com.youlai.system.model.vo.TablePageVO;
import com.youlai.system.service.GeneratorService;
import com.youlai.system.service.GenConfigService;
import com.youlai.system.service.GenFieldConfigService;
import com.youlai.system.service.SysMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据库服务实现类
 *
 * @author Ray
 * @since 2.10.0
 */
@Service
@RequiredArgsConstructor
public class GeneratorServiceImpl implements GeneratorService {

    private final DatabaseMapper databaseMapper;
    private final GeneratorProperties generatorProperties;
    private final GenConfigService genConfigService;
    private final GenFieldConfigService genFieldConfigService;
    private final GenConfigConverter genConfigConverter;
    private final SysMenuService menuService;

    /**
     * 数据表分页列表
     *
     * @param queryParams 查询参数
     * @return 分页结果
     */
    public Page<TablePageVO> getTablePage(TablePageQuery queryParams) {
        Page<TablePageVO> page = new Page<>(queryParams.getPageNum(), queryParams.getPageSize());
        // 设置排除的表
        List<String> excludeTables = generatorProperties.getExcludeTables();
        queryParams.setExcludeTables(excludeTables);

        return databaseMapper.getTablePage(page, queryParams);
    }

    /**
     * 获取代码生成配置
     *
     * @param tableName 表名 eg: sys_user
     * @return 代码生成配置
     */
    @Override
    public GenConfigForm getGenConfigFormData(String tableName) {
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

            genConfig.setAuthor(generatorProperties.getDefaultConfig().getAuthor());

        }

        // 根据表的列 + 已经存在的字段生成配置 得到 组合后的字段生成配置
        List<GenFieldConfig> genFieldConfigs = new ArrayList<>();

        // 获取表的列
        List<ColumnMetaData> tableColumns = databaseMapper.getTableColumns(tableName);
        if (CollectionUtil.isNotEmpty(tableColumns)) {
            // 查询字段生成配置
            List<GenFieldConfig> fieldConfigList = genFieldConfigService.list(
                    new LambdaQueryWrapper<GenFieldConfig>()
                            .eq(GenFieldConfig::getConfigId, genConfig.getId())
                            .orderByAsc(GenFieldConfig::getFieldSort)
            );
            Integer maxSort = fieldConfigList.stream().map(GenFieldConfig::getFieldSort).max(Integer::compareTo).orElseGet(() -> 0);
            for (ColumnMetaData tableColumn : tableColumns) {
                // 根据列名获取字段生成配置
                String columnName = tableColumn.getColumnName();
                GenFieldConfig genFieldConfig = fieldConfigList.stream()
                        .filter(item -> StrUtil.equals(item.getColumnName(), columnName))
                        .findFirst()
                        .orElseGet(() -> createDefaultFieldConfig(tableColumn));
                if (genFieldConfig.getFieldSort() == null) {
                    genFieldConfig.setFieldSort(++maxSort);
                }
                // 根据列类型设置字段类型
                String fieldType = genFieldConfig.getFieldType();
                if (StrUtil.isBlank(fieldType)) {
                    String javaType = JavaTypeEnum.getJavaTypeByDbType(genFieldConfig.getColumnType());
                    genFieldConfig.setFieldType(javaType);
                }
                genFieldConfigs.add(genFieldConfig);
            }
        }
        //对genFieldConfigs按照fieldSort排序
        genFieldConfigs = genFieldConfigs.stream().sorted(Comparator.comparing(GenFieldConfig::getFieldSort)).collect(Collectors.toList());
        return genConfigConverter.toGenConfigForm(genConfig, genFieldConfigs);
    }


    /**
     * 创建默认字段配置
     *
     * @param columnMetaData 表字段元数据
     * @return
     */
    private GenFieldConfig createDefaultFieldConfig(ColumnMetaData columnMetaData) {
        GenFieldConfig fieldConfig = new GenFieldConfig();
        fieldConfig.setColumnName(columnMetaData.getColumnName());
        fieldConfig.setColumnType(columnMetaData.getDataType());
        fieldConfig.setFieldComment(columnMetaData.getColumnComment());
        fieldConfig.setFieldName(StrUtil.toCamelCase(columnMetaData.getColumnName()));
        fieldConfig.setIsRequired("YES".equals(columnMetaData.getIsNullable()) ? 1 : 0);
        fieldConfig.setFormType(FormTypeEnum.INPUT);
        fieldConfig.setQueryType(QueryTypeEnum.EQ);
        fieldConfig.setMaxLength(columnMetaData.getCharacterMaximumLength());
        return fieldConfig;
    }

    /**
     * 保存代码生成配置
     *
     * @param formData 代码生成配置表单
     */
    @Override
    public void saveGenConfig(GenConfigForm formData) {
        GenConfig genConfig = genConfigConverter.toGenConfig(formData);
        genConfigService.saveOrUpdate(genConfig);

        // 如果选择上级菜单
        Long parentMenuId = formData.getParentMenuId();
        if (parentMenuId != null) {
            menuService.addMenuForCodeGeneration(parentMenuId,genConfig.getBusinessName(),genConfig.getEntityName());
        }

        List<GenFieldConfig> genFieldConfigs = genConfigConverter.toGenFieldConfig(formData.getFieldConfigs());

        if (CollectionUtil.isEmpty(genFieldConfigs)) {
            throw new BusinessException("字段配置不能为空");
        }
        genFieldConfigs.forEach(genFieldConfig -> {
            genFieldConfig.setConfigId(genConfig.getId());
        });
        genFieldConfigService.saveOrUpdateBatch(genFieldConfigs);
    }

    /**
     * 删除代码生成配置
     *
     * @param tableName 表名
     * @return
     */
    @Override
    public void deleteGenConfig(String tableName) {
        GenConfig genConfig = genConfigService.getOne(new LambdaQueryWrapper<GenConfig>()
                .eq(GenConfig::getTableName, tableName));

        boolean result = genConfigService.remove(new LambdaQueryWrapper<GenConfig>()
                .eq(GenConfig::getTableName, tableName)
        );
        if (result) {
            genFieldConfigService.remove(new LambdaQueryWrapper<GenFieldConfig>()
                    .eq(GenFieldConfig::getConfigId, genConfig.getId())
            );
        }
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
                .orderByAsc(GenFieldConfig::getFieldSort)

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
            String filePath = getFilePath(templateName, packageName, subPackageName, entityName);
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
        if ("Entity".equals(templateName)) {
            return entityName + extension;
        }
        if ("MapperXml".equals(templateName)) {
            return entityName + "Mapper" + extension;
        }
        if ("API".equals(templateName)) {
            return StrUtil.toSymbolCase(entityName, '-') + extension;
        }

        if ("VIEW".equals(templateName)) {
            return "index.vue";
        }

        return entityName + templateName + extension;
    }

    private String getFilePath(String templateName, String packageName, String subPackageName, String entityName) {
        String path;
        if ("MapperXml".equals(templateName)) {
            path = (generatorProperties.getBackendAppName()
                    + File.separator
                    + "src" + File.separator + "main" + File.separator + "resources"
                    + File.separator + subPackageName
            );
        } else if ("API".equals(templateName)) {
            path = (generatorProperties.getFrontendAppName()
                    + File.separator
                    + "src" + File.separator + subPackageName
            );
        } else if ("VIEW".equals(templateName)) {
            path = (generatorProperties.getFrontendAppName()
                    + File.separator
                    + "src" + File.separator + subPackageName
                    + File.separator
                    + StrUtil.toSymbolCase(entityName, '-')
            );
        } else {
            path = (generatorProperties.getBackendAppName()
                    + File.separator
                    + "src" + File.separator + "main" + File.separator + "java"
                    + File.separator + packageName + File.separator + subPackageName
            );
        }

        // subPackageName = model.entity => model/entity
        path = path.replace(".", File.separator);

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
        bindMap.put("businessName", genConfig.getBusinessName());
        bindMap.put("fieldConfigs", fieldConfigs);

        boolean hasLocalDateTime = false;
        boolean hasBigDecimal = false;
        boolean hasRequiredField = false;

        for (GenFieldConfig fieldConfig : fieldConfigs) {
            if ("LocalDateTime".equals(fieldConfig.getFieldType())) {
                hasLocalDateTime = true;
            }
            if ("BigDecimal".equals(fieldConfig.getFieldType())) {
                hasBigDecimal = true;
            }
            if (ObjectUtil.equals(fieldConfig.getIsRequired(), 1)) {
                hasRequiredField = true;
            }
            fieldConfig.setTsType(JavaTypeEnum.getTsTypeByJavaType(fieldConfig.getFieldType()));
        }

        bindMap.put("hasLocalDateTime", hasLocalDateTime);
        bindMap.put("hasBigDecimal", hasBigDecimal);
        bindMap.put("hasRequiredField", hasRequiredField);

        TemplateEngine templateEngine = TemplateUtil.createEngine(new TemplateConfig("templates", TemplateConfig.ResourceMode.CLASSPATH));
        Template template = templateEngine.getTemplate(templateConfig.getTemplatePath());
        String content = template.render(bindMap);

        return content;
    }


}
