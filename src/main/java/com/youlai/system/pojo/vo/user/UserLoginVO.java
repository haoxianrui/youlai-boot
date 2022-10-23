package com.youlai.system.pojo.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * 用户登录视图对象
 *
 * @author haoxr
 * @date 2022/1/14
 */
@ApiModel("当前登录用户视图对象")
@Data
public class UserLoginVO {

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("用户昵称")
    private String nickname;

    @ApiModelProperty("头像地址")
    private String avatar;

    @ApiModelProperty("用户角色编码集合")
    private Set<String> roles;

    @ApiModelProperty("用户权限标识集合")
    private Set<String> perms;

}
