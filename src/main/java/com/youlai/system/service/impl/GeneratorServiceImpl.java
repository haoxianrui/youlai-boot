package com.youlai.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.system.config.property.GeneratorProperties;
import com.youlai.system.mapper.DatabaseMapper;
import com.youlai.system.mapper.GenConfigMapper;
import com.youlai.system.mapper.GenFieldConfigMapper;
import com.youlai.system.model.entity.GenConfig;
import com.youlai.system.model.entity.GenFieldConfig;
import com.youlai.system.model.form.GenConfigForm;
import com.youlai.system.model.query.TablePageQuery;
import com.youlai.system.model.vo.TableColumnVO;
import com.youlai.system.model.vo.GeneratorPreviewVO;
import com.youlai.system.model.vo.TablePageVO;
import com.youlai.system.service.GeneratorService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import cn.hutool.extra.template.TemplateConfig.ResourceMode;

import java.io.File;
import java.util.*;

/**
 * 数据库服务实现类
 *
 * @author Ray
 * @since 2.11.0
 */
@Service
@RequiredArgsConstructor
public class GeneratorServiceImpl implements GeneratorService {

    private final DatabaseMapper databaseMapper;

    private final GeneratorProperties generatorProperties;

    private final GenConfigMapper genConfigMapper;
    private final GenFieldConfigMapper genFieldConfigMapper;

    // 注入 spring.application.name
    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * 数据表分页列表
     *
     * @param queryParams 查询参数
     * @return 分页结果
     */
    public Page<TablePageVO> getTablePage(TablePageQuery queryParams) {
        Page<TablePageVO> page = new Page<>(queryParams.getPageNum(), queryParams.getPageSize());
        return databaseMapper.getTablePage(page, queryParams);
    }

    /**
     * 获取数据表字段列表
     *
     * @param tableName 表名
     * @return 字段列表
     */
    @Override
    public List<TableColumnVO> getTableColumns(String tableName) {
        return databaseMapper.getTableColumns(tableName);
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
        GenConfig genConfig = genConfigMapper.selectOne(
                new LambdaQueryWrapper<>(GenConfig.class)
                        .eq(GenConfig::getTableName, tableName)
                        .last("LIMIT 1")
        );

        // 查询字段生成配置
        List<GenFieldConfig> fieldConfigs = genFieldConfigMapper.selectList(
                new LambdaQueryWrapper<>(GenFieldConfig.class)
                        .eq(GenFieldConfig::getConfigId, genConfig.getId())
        );

        GenConfigForm genConfigForm = new GenConfigForm();


        return null;
    }

    @Override
    public boolean saveGenCodeConfig(GenConfigForm formData) {
        return false;
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

        GenConfig genConfig = genConfigMapper.selectOne(new LambdaQueryWrapper<GenConfig>()
                .eq(GenConfig::getTableName, tableName)
        );
        Assert.isTrue(genConfig != null, "未找到表生成配置");

        List<GenFieldConfig> fieldConfigs = genFieldConfigMapper.selectList(new LambdaQueryWrapper<GenFieldConfig>()
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
            bindMap.put("hasRequiredField", Boolean.TRUE.equals(fieldConfig.getIsRequired()));
        }

        TemplateEngine templateEngine = TemplateUtil.createEngine(new TemplateConfig("templates", ResourceMode.CLASSPATH));
        Template template = templateEngine.getTemplate(templateConfig.getTemplatePath());
        String content = template.render(bindMap);

        return content;
    }

}
