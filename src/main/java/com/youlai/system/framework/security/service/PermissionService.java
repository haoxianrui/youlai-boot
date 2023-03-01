package com.youlai.system.framework.security.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.youlai.system.framework.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.PatternMatchUtils;

import java.util.Set;

/**
 * 权限校验
 *
 * @author haoxr
 * @date 2022/2/22
 */
@Service("pms")
@RequiredArgsConstructor
public class PermissionService {

    private final RedisTemplate redisTemplate;

    /**
     * 判断当前登录用户是否拥有操作权限
     *
     * @param perm 权限标识(eg: sys:user:add)
     * @return
     */
    public boolean hasPermission(String perm) {

        if (StrUtil.isBlank(perm)) {
            return false;
        }
        // 超级管理员放行
        if (SecurityUtils.isRoot()) {
            return true;
        }

        Long userId = SecurityUtils.getUserId();

        Set<String> perms = (Set<String>) redisTemplate.opsForValue().get("USER_PERMS:" + userId); // 权限数据用户登录成功节点存入redis，详见 JwtTokenManager#createToken()

        if (CollectionUtil.isEmpty(perms)) {
            return false;
        }
        boolean hasPermission = perms.stream()
                .anyMatch(item -> PatternMatchUtils.simpleMatch(perm, item)); // *号匹配任意字符
        return hasPermission;
    }


}
