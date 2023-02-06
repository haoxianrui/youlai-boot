package com.youlai.system.pojo.vo.menu;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description ="资源(菜单+权限)视图对象")
@Data
public class ResourceVO {

    @Schema(description="选项的值")
    private Long value;

    @Schema(description="选项的标签")
    private String label;

    @Schema(description="子菜单")
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private List<ResourceVO> children;



}
