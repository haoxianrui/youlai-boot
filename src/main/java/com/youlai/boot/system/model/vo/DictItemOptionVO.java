package com.youlai.boot.system.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 字典数据项
 *
 * @author Ray
 * @since 0.0.1
 */
@Schema(description = "字典数据项")
@Getter
@Setter
public class DictItemOptionVO {

    @Schema(description = "字典数据值")
    private String value;

    @Schema(description = "字典数据标签")
    private String label;

    @Schema(description = "标签类型")
    private String tagType;

}
