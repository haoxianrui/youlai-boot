package com.youlai.system.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Schema(description = "数据表分页VO")
@Data
public class TablePageVO {

    @Schema(description = "数据表名称", example = "sys_user")
    private String tableName;

    @Schema(description = "数据表注释",example = "用户表")
    private String tableComment;

    @Schema(description = "数据表排序规则",example = "用户表")
    private String tableCollation;

    @Schema(description = "存储引擎",example = "InnoDB")
    private String engine;

    @Schema(description = "字符集",example = "utf8mb4_general_ci")
    private String charset;

    @Schema(description = "创建时间",example = "2023-08-08 08:08:08")
    private String createTime;

}
