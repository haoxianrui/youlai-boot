package com.youlai.boot.core.security.extension.sms;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serial;
import java.util.Collection;

/**
 * 短信验证码认证 Token
 *
 * @author Ray.Hao
 * @since 2.20.0
 */
public class SmsAuthenticationToken extends AbstractAuthenticationToken {
    @Serial
    private static final long serialVersionUID = 621L;
    private final Object principal;
    private Object credentials;

    /**
     * 短信验证码认证 Token (未认证)
     *
     * @param principal 微信用户信息
     */
    public SmsAuthenticationToken(Object principal) {
        // 没有授权信息时，设置为 null
        super(null);
        this.principal = principal;
        // 默认未认证
        this.setAuthenticated(false);
    }

    /**
     * 短信验证码认证 Token (已认证)
     *
     * @param principal   用户信息
     * @param authorities 授权信息
     */
    public SmsAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        // 认证通过
        super.setAuthenticated(true);
    }


    /**
     * 认证通过
     *
     * @param principal   用户信息
     * @param authorities 授权信息
     * @return
     */
    public static SmsAuthenticationToken authenticated(Object principal, Collection<? extends GrantedAuthority> authorities) {
        return new SmsAuthenticationToken(principal, authorities);
    }

    @Override
    public Object getCredentials() {
        return this.credentials ;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }
}
