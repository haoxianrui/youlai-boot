package com.youlai.system.pojo.vo.perm;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 权限视图对象
 *
 * @author haoxr
 * @date 2021/10/30 10:54
 */
@Schema(description ="权限视图对象")
@Data
public class PermPageVO {

    @Schema(description="权限ID")
    private Long id;

    @Schema(description="权限名称")
    private String name;

    @Schema(description="URL权限标识-服务名称")
    private String serviceName;

    @Schema(description="URL权限标识-请求标识")
    private String requestMethod;

    @Schema(description="URL权限标识-请求方式")
    private String requestPath;

    @Schema(description="按钮权限标识")
    private String btnPerm;

}
