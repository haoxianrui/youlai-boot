package com.youlai.system.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 用户表详情视图对象
 *
 * @author haoxr
 * @date 2022/8/25
 */
@Schema 
@Data
public class UserDetailVO {

    @Schema(description="用户ID")
    private Long id;

    @Schema(description="用户名")
    private String username;

    @Schema(description="昵称")
    private String nickname;

    @Schema(description="")
    private String mobile;

    @Schema(description="性别")
    private Integer gender;

    @Schema(description="用户头像")
    private String avatar;

    @Schema(description="邮箱")
    private String email;

    @Schema(description="用户状态(1:正常;0:禁用)")
    private Integer status;

    @Schema(description="部门ID")
    private Long deptId;

    @Schema(description="角色ID集合")
    private List<Long> roleIds;

}
