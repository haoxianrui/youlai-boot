package com.youlai.system.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "代码生成配置表单")
@Data
public class GeneratorConfigForm {

    @Schema(description = "表名")
    private String tableName;

    @Schema(description = "实体名")
    private String entityName;

    @Schema(description = "包名")
    private String packageName;

    @Schema(description = "模块名")
    private String moduleName;

    @Schema(description = "作者")
    private String author;

    @Schema(description = "字段配置")
    private List<FieldConfig> fieldConfigs;

    @Schema(description = "字段配置")
    @Data
    public static class FieldConfig {

        @Schema(description = "字段名称")
        private String name;

        @Schema(description = "字段类型")
        private String type;

        @Schema(description = "字段描述")
        private String description;

        @Schema(description = "是否在列表显示")
        private Boolean showInList;

        @Schema(description = "是否在表单显示")
        private Boolean showInForm;

        @Schema(description = "是否在查询条件显示")
        private Boolean showInQuery;

        @Schema(description = "表单类型")
        private String formType;

        @Schema(description = "查询方式")
        private String queryMethod;

    }
}
