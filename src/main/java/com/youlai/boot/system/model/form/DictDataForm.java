package com.youlai.boot.system.model.form;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import java.util.List;

/**
 * 字典数据表单对象
 *
 * @author Ray Hao
 * @since 2.9.0
 */
@Schema(description = "字典数据表单")
@Data
public class DictDataForm {

    @Schema(description = "字典ID")
    private Long id;

    @Schema(description = "字典编码")
    private String dictCode;

    @Schema(description = "字典值")
    private String value;

    @Schema(description = "字典标签")
    private String label;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "状态（1-启用，0-禁用）")
    private Integer status;

    @Schema(description = "字典类型")
    private String tagType;

}
