package com.youlai.system.pojo.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.youlai.system.common.enums.MenuTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description ="菜单视图对象")
@Data
public class MenuVO {

    private Long id;

    private Long parentId;

    private String name;

    private String icon;

    private String routeName;

    private String routePath;

    private String component;

    private Integer sort;

    private Integer visible;

    private String redirect;

    @Schema(description="菜单类型")
    private MenuTypeEnum type;

    @Schema(description="按钮权限标识")
    private String perm;

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private List<MenuVO> children;

}
