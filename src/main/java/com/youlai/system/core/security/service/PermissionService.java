package com.youlai.system.core.security.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.youlai.system.common.constant.CacheConstants;
import com.youlai.system.common.util.SecurityUtils;
import com.youlai.system.model.bo.RolePermsBO;
import com.youlai.system.service.SysRoleMenuService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;

import java.util.List;
import java.util.Set;

/**
 * SpringSecurity 权限校验
 *
 * @author haoxr
 * @since 2022/2/22
 */
@Component("ss")
@RequiredArgsConstructor
@Slf4j
public class PermissionService {

    private final RedisTemplate redisTemplate;

    private final SysRoleMenuService roleMenuService;

    /**
     * 判断当前登录用户是否拥有操作权限
     *
     * @param requiredPerm 权限标识(eg: sys:user:add)
     * @return
     */
    public boolean hasPerm(String requiredPerm) {

        if (StrUtil.isBlank(requiredPerm)) {
            return false;
        }
        // 超级管理员放行
        if (SecurityUtils.isRoot()) {
            return true;
        }

        Set<String> roleCodes = SecurityUtils.getRoles();
        if (CollectionUtil.isEmpty(roleCodes)) {
            return false;
        }
        boolean hasPermission = false;
        for (String roleCode : roleCodes) {
            Set<String> rolePerms = (Set<String>) redisTemplate.opsForHash().get(CacheConstants.ROLE_PERMS_PREFIX, roleCode);

            if (CollectionUtil.isEmpty(rolePerms)) {
                // 无权限 ，判断下一个角色是否有权限
                continue;
            }
            // 匹配权限，支持通配符
            hasPermission = rolePerms.stream()
                    .anyMatch(rolePerm ->
                            //rolePerm=sys:user:*  requiredPerm=sys:user:add 返回true
                            PatternMatchUtils.simpleMatch(rolePerm, requiredPerm)
                    );

            if (hasPermission) {
                // 匹配到权限，退出循环
                break;
            }
        }
        if (!hasPermission) {
            log.error("用户无操作权限");
        }
        return hasPermission;
    }

    /**
     * 初始化权限缓存
     */
    @PostConstruct
    public void initPermissionCache() {
        refreshPermissionCache();
    }

    /**
     * 刷新权限缓存
     */
    public void refreshPermissionCache() {
        // 清理权限缓存
        redisTemplate.opsForHash().delete(CacheConstants.ROLE_PERMS_PREFIX, "*");

        List<RolePermsBO> list = roleMenuService.getRolePermsList(null);
        if (CollectionUtil.isNotEmpty(list)) {
            list.forEach(item -> {
                String roleCode = item.getRoleCode();
                Set<String> perms = item.getPerms();
                redisTemplate.opsForHash().put(CacheConstants.ROLE_PERMS_PREFIX, roleCode, perms);
            });
        }
    }

    /**
     * 刷新权限缓存
     */
    public void refreshPermissionCache(String roleCode) {
        // 清理权限缓存
        redisTemplate.opsForHash().delete(CacheConstants.ROLE_PERMS_PREFIX, roleCode);

        List<RolePermsBO> list = roleMenuService.getRolePermsList(roleCode);
        if (CollectionUtil.isNotEmpty(list)) {
            RolePermsBO rolePerms = list.get(0);
            if (rolePerms == null) {
                return;
            }

            Set<String> perms = rolePerms.getPerms();
            redisTemplate.opsForHash().put(CacheConstants.ROLE_PERMS_PREFIX, roleCode, perms);
        }
    }

}
