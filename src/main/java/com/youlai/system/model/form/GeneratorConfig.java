package com.youlai.system.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "代码生成配置")
@Data
public class GeneratorConfig {

        @Schema(description = "表名")
        private String tableName;

        @Schema(description = "包名")
        private String packageName;

        @Schema(description = "模块名")
        private String moduleName;

        @Schema(description = "作者")
        private String author;

        @Schema(description = "表前缀")
        private String tablePrefix;

        @Schema(description = "是否覆盖")
        private Boolean cover;


}
