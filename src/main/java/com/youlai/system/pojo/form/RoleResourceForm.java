package com.youlai.system.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;


@Schema(description ="菜单资源表单")
@Data
public class RoleResourceForm {

    @Schema(description="菜单ID集合")
    private List<Long> menuIds;

    @Schema(description="权限ID集合")
    private List<Long> permIds;

}
