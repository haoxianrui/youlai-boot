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

    GenConfigForm.FieldConfig toFieldConfig(GenFieldConfig genFieldConfig);



    @Mapping(source = "formData", target = "genConfig")
    @Mapping(source = "formData.fieldConfigs", target = "fieldConfigs")
    GenConfig toGenConfig(GenConfigForm formData);

    @Mapping(source = "formData.fieldConfigs", target = "fieldConfigs")
    List<GenFieldConfig> toGenFieldConfigList(List<GenConfigForm.FieldConfig> fieldConfigs);

}