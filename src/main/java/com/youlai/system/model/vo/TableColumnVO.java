package com.youlai.system.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "数据表字段VO")
@Data
public class TableColumnVO {

        @Schema(description = "字段名称", example = "id")
        private String columnName;

        @Schema(description = "字段类型", example = "bigint")
        private String dataType;

        @Schema(description = "字段描述", example = "主键")
        private String columnComment;

        @Schema(description = "字段长度", example = "20")
        private Integer characterMaximumLength;

        @Schema(description = "是否主键(1-是 0-否)", example = "1")
        private Integer isPrimaryKey;

        @Schema(description = "是否可为空(1-是 0-否)", example = "1")
        private String isNullable;

        @Schema(description = "字符集", example = "utf8mb4")
        private String characterSetName;

        @Schema(description = "字符集排序规则", example = "utf8mb4_general_ci")
        private String collationName;

}
