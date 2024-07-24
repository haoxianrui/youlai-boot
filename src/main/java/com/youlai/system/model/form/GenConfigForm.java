package com.youlai.system.model.form;

import com.youlai.system.enums.FormTypeEnum;
import com.youlai.system.enums.QueryTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "代码生成配置表单")
@Data
public class GenConfigForm {

    @Schema(description = "表名")
    private String tableName;

    @Schema(description = "类描述")
    private String comment;

    @Schema(description = "模块名")
    private String moduleName;

    @Schema(description = "包名")
    private String packageName;

    @Schema(description = "实体名")
    private String entityName;

    @Schema(description = "作者")
    private String author;

    @Schema(description = "字段配置列表")
    private List<FieldConfig> fieldConfigs;

    @Schema(description = "字段配置")
    @Data
    public static class FieldConfig {
        @Schema(description = "ID")
        private Long configId;

        @Schema(description = "列名")
        private String columnName;

        @Schema(description = "列类型")
        private String columnType;

        @Schema(description = "字段名")
        private String fieldName;

        @Schema(description = "字段类型")
        private String fieldType;

        @Schema(description = "字段描述")
        private String comment;

        @Schema(description = "是否在列表显示")
        private Integer isShowInList;

        @Schema(description = "是否在表单显示")
        private Integer isShowInForm;

        @Schema(description = "是否在查询条件显示")
        private Integer isShowInQuery;

        @Schema(description = "是否必填")
        private Integer isRequired;

        @Schema(description = "表单类型")
        private FormTypeEnum formType;

        @Schema(description = "查询类型")
        private QueryTypeEnum queryType;

    }
}
