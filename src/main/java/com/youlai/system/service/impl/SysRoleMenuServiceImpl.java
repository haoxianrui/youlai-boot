package com.youlai.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.system.mapper.SysRoleMenuMapper;
import com.youlai.system.model.bo.RolePermsBO;
import com.youlai.system.model.entity.SysRoleMenu;
import com.youlai.system.service.SysRoleMenuService;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 角色菜单业务实现
 *
 * @author haoxr
 * @since 2.5.0
 */
@Service
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements SysRoleMenuService {

    /**
     * 获取角色拥有的菜单ID集合
     *
     * @param roleId 角色ID
     * @return 菜单ID集合
     */
    @Override
    public List<Long> listMenuIdsByRoleId(Long roleId) {
        List<Long> menuIds = this.baseMapper.listMenuIdsByRoleId(roleId);
        return menuIds;
    }

    /**
     * 获取权限角色列表
     *
     * @return 权限角色列表
     */
    @Override
    public List<RolePermsBO> getRolePermsList(String roleCode) {
        List<RolePermsBO> rolePerms= this.baseMapper.getRolePermsList(roleCode);
        return rolePerms;
    }

}
