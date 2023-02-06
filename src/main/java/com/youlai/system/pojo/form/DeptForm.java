package com.youlai.system.pojo.form;

import com.youlai.system.common.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Schema(description = "部门表单对象")
@Data
public class DeptForm extends BaseEntity {

    @Schema(description="部门ID(新增不填)")
    private Long id;

    @Schema(description="部门名称")
    private String name;

    @Schema(description="父部门ID")
    @NotNull(message = "父部门ID不能为空")
    private Long parentId;

    @Schema(description="状态")
    private Integer status;

    @Schema(description="排序")
    private Integer sort;

}
