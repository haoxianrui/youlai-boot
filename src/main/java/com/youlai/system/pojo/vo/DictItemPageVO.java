package com.youlai.system.pojo.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description ="字典数据项分页对象")
@Data
public class DictItemPageVO {

    @Schema(description="数据项ID")
    private Long id;

    @Schema(description="数据项名称")
    private String name;

    @Schema(description="值")
    private String value;

    @Schema(description="类型状态：1->启用;0->禁用")
    private Integer status;

}
