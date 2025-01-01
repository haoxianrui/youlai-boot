package com.youlai.boot.core.security.extension;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serial;
import java.util.Collection;

/**
 * 微信认证 Token
 *
 * @author Ray.Hao
 * @since 2024/12/2
 */
public class WechatAuthenticationToken extends AbstractAuthenticationToken {
    @Serial
    private static final long serialVersionUID = 621L;
    private final Object principal;

    /**
     * 微信认证 Token (未认证)
     *
     * @param principal 微信用户信息
     */
    public WechatAuthenticationToken(Object principal) {
        // 没有授权信息时，设置为 null
        super(null);
        this.principal = principal;
        // 默认未认证
        this.setAuthenticated(false);
    }


    /**
     * 微信认证 Token (已认证)
     *
     * @param principal   微信用户信息
     * @param authorities 授权信息
     */
    public WechatAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        // 认证通过
        super.setAuthenticated(true);
    }


    /**
     * 认证通过
     *
     * @param principal   微信用户信息
     * @param authorities 授权信息
     * @return
     */
    public static WechatAuthenticationToken authenticated(Object principal, Collection<? extends GrantedAuthority> authorities) {
        return new WechatAuthenticationToken(principal, authorities);
    }

    @Override
    public Object getCredentials() {
        // 微信认证不需要密码
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }
}