package com.youlai.system.pojo.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * 用户表单对象
 *
 * @author haoxr
 * @date 2022/4/12 11:04
 */
@ApiModel
@Data
public class UserForm {

    @ApiModelProperty("用户ID")
    private Long id;

    @ApiModelProperty("用户名")
    @NotBlank(message = "用户名不能为空")
    private String username;

    @ApiModelProperty("昵称")
    @NotBlank(message = "昵称不能为空")
    private String nickname;

    @Pattern(regexp = "^1(3\\d|4[5-9]|5[0-35-9]|6[2567]|7[0-8]|8\\d|9[0-35-9])\\d{8}$", message = "{phone.valid}")
    private String mobile;

    @ApiModelProperty("性别")
    private Integer gender;

    @ApiModelProperty("用户头像")
    private String avatar;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("用户状态(1:正常;0:禁用)")
    private Integer status;

    @ApiModelProperty("部门ID")
    private Long deptId;

    @ApiModelProperty("角色ID集合")
    @NotEmpty(message = "用户角色不能为空")
    private List<Long> roleIds;

}
