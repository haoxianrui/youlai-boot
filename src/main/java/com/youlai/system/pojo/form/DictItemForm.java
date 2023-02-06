package com.youlai.system.pojo.form;


import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "字典数据项")
@Data
public class DictItemForm {

    @Schema(description="数据项ID")
    private Long id;

    @Schema(description="类型编码")
    private String typeCode;

    @Schema(description="数据项名称")
    private String name;

    @Schema(description="值")
    private String value;

    @Schema(description="状态：1->启用;0->禁用")
    private Integer status;

    @Schema(description="排序")
    private Integer sort;

}
