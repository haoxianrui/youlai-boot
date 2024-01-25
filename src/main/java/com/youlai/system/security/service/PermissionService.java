package com.youlai.system.security.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.youlai.system.common.constant.CacheConstants;
import com.youlai.system.security.util.SecurityUtils;
import com.youlai.system.model.bo.RolePermsBO;
import com.youlai.system.service.SysRoleMenuService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;

import java.util.*;

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

    private final RedisTemplate<String,Object> redisTemplate;

    private final SysRoleMenuService roleMenuService;


    /**
     * 初始化权限缓存
     */
    @PostConstruct
    public void initRolePermsCache() {
        refreshRolePermsCache();
    }

    /**
     * 刷新权限缓存
     */
    public void refreshRolePermsCache() {
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
    public void refreshRolePermsCache(String roleCode) {
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

    /**
     * 刷新权限缓存 (角色编码变更时调用)
     */
    public void refreshRolePermsCache(String oldRoleCode,String newRoleCode) {
        // 清理旧角色权限缓存
        redisTemplate.opsForHash().delete(CacheConstants.ROLE_PERMS_PREFIX, oldRoleCode);

        // 添加新角色权限缓存
        List<RolePermsBO> list = roleMenuService.getRolePermsList(newRoleCode);
        if (CollectionUtil.isNotEmpty(list)) {
            RolePermsBO rolePerms = list.get(0);
            if (rolePerms == null) {
                return;
            }

            Set<String> perms = rolePerms.getPerms();
            redisTemplate.opsForHash().put(CacheConstants.ROLE_PERMS_PREFIX, newRoleCode, perms);
        }
    }


    /**
     * 获取角色权限列表
     *
     * @param roleCodes 角色编码集合
     * @return 角色权限列表
     */
    public Set<String> getRolePermsFormCache(Set<String> roleCodes) {
        // 检查输入是否为空
        if (CollectionUtil.isEmpty(roleCodes)) {
            return Collections.emptySet();
        }

        Set<String> perms = new HashSet<>();
        // 从缓存中一次性获取所有角色的权限
        Collection<Object> roleCodesAsObjects = new ArrayList<>(roleCodes);
        List<Object> rolePermsList = redisTemplate.opsForHash().multiGet(CacheConstants.ROLE_PERMS_PREFIX, roleCodesAsObjects);

        for (Object rolePermsObj : rolePermsList) {
            if (rolePermsObj instanceof Set) {
                @SuppressWarnings("unchecked")
                Set<String> rolePerms = (Set<String>) rolePermsObj;
                perms.addAll(rolePerms);
            }
        }

        return perms;
    }


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


}
