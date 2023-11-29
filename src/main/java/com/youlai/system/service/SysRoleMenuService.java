package com.youlai.system.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.youlai.system.model.bo.RolePermsBO;
import com.youlai.system.model.entity.SysRoleMenu;

import java.util.List;
import java.util.Set;

/**
 * 角色菜单业务接口
 *
 * @author haoxr
 * @since 2.5.0
 */
public interface SysRoleMenuService extends IService<SysRoleMenu> {

    /**
     * 获取角色拥有的菜单ID集合
     *
     * @param roleId 角色ID
     * @return 菜单ID集合
     */
    List<Long> listMenuIdsByRoleId(Long roleId);


    /**
     * 获取角色和权限的列表
     *
     * @return 角色权限的列表
     */
    List<RolePermsBO> getRolePermsList(String roleCode);
}
