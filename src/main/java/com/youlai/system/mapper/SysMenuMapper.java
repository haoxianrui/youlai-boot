package com.youlai.system.mapper;

/**
 * 菜单持久接口层
 *
 * @author haoxr
 * @date 2022/1/24
 */

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlai.system.pojo.entity.SysMenu;
import com.youlai.system.pojo.bo.RouteBO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Set;

@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    List<RouteBO> listRoutes();

    /**
     * 获取角色权限集合
     *
     * @param roles
     * @return
     */
    Set<String> listRolePerms(Set<String> roles);
}
