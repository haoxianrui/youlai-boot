package com.youlai.system.converter;

import com.youlai.system.model.entity.GenConfig;
import com.youlai.system.model.entity.GenFieldConfig;
import com.youlai.system.model.form.GenConfigForm;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * 代码生成配置转换器
 *
 * @author Ray
 * @since 2.10.0
 */
@Mapper(componentModel = "spring")
public interface GenConfigConverter {

    @Mapping(source = "genConfig.tableName", target = "tableName")
    @Mapping(source = "genConfig.comment", target = "comment")
    @Mapping(source = "genConfig.moduleName", target = "moduleName")
    @Mapping(source = "genConfig.packageName", target = "packageName")
    @Mapping(source = "genConfig.entityName", target = "entityName")
    @Mapping(source = "genConfig.author", target = "author")
    @Mapping(source = "fieldConfigs", target = "fieldConfigs")
    GenConfigForm toGenConfigForm(GenConfig genConfig, List<GenFieldConfig> fieldConfigs);

    List<GenConfigForm.FieldConfig> toFieldConfigList(List<GenFieldConfig> fieldConfigs);

    @Mapping(source = "configId", target = "configId")
    @Mapping(source = "columnName", target = "columnName")
    @Mapping(source = "columnType", target = "columnType")
    @Mapping(source = "fieldName", target = "fieldName")
    @Mapping(source = "fieldType", target = "fieldType")
    @Mapping(source = "comment", target = "comment")
    @Mapping(source = "formType", target = "formType")
    @Mapping(source = "queryType", target = "queryType")
    @Mapping(source = "isShowInList", target = "isShowInList")
    @Mapping(source = "isShowInForm", target = "isShowInForm")
    @Mapping(source = "isShowInQuery", target = "isShowInQuery")
    @Mapping(source = "isRequired", target = "isRequired")
    GenConfigForm.FieldConfig toFieldConfig(GenFieldConfig genFieldConfig);

}