package com.youlai.boot.core.security.model;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.youlai.boot.system.model.dto.UserAuthInfo;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Spring Security 用户对象
 *
 * @author haoxr
 * @since 3.0.0
 */
@Data
@NoArgsConstructor
public class SysUserDetails implements UserDetails {

    @Getter
    private Long userId;

    private String username;

    private String password;

    private Boolean enabled;

    private Collection<SimpleGrantedAuthority> authorities;

    private Set<String> perms;

    private Long deptId;

    private Integer dataScope;

    public SysUserDetails(UserAuthInfo user) {
        this.userId = user.getUserId();
        Set<String> roles = user.getRoles();
        Set<SimpleGrantedAuthority> authorities;
        if (CollectionUtil.isNotEmpty(roles)) {
            authorities = roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role)) // 标识角色
                    .collect(Collectors.toSet());
        } else {
            authorities = Collections.emptySet();
        }
        this.authorities = authorities;
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.enabled = ObjectUtil.equal(user.getStatus(), 1);
        this.perms = user.getPerms();
        this.deptId = user.getDeptId();
        this.dataScope = user.getDataScope();
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
