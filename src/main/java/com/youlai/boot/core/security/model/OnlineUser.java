package com.youlai.boot.core.security.model;

import lombok.Data;

import java.util.Set;

/**
 * @author wangtao
 * @since 2025/2/27 10:31
 */
@Data
public class OnlineUser{
    private Long id;
    private Long deptId;
    private String username;
    private Integer dataScope;
    private Set<String> authorities;
}
