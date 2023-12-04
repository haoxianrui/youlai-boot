package com.youlai.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlai.system.model.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色访问层
 *
 * @author haoxr
 * @since 2022/1/15
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    /**
     * 统计角色下绑定的用户数量
     *
     * @param roleId 角色ID
     */
    int countUsersForRole(Long roleId);
}
