package com.youlai.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youlai.system.model.form.MenuForm;
import com.youlai.system.common.model.Option;
import com.youlai.system.model.entity.SysMenu;
import com.youlai.system.model.query.MenuQuery;
import com.youlai.system.model.vo.MenuVO;
import com.youlai.system.model.vo.RouteVO;

import java.util.List;
import java.util.Set;

/**
 * 菜单业务接口
 * 
 * @author haoxr
 * @since 2020/11/06
 */
public interface SysMenuService extends IService<SysMenu> {

    /**
     * 获取菜单表格列表
     */
    List<MenuVO> listMenus(MenuQuery queryParams);


    /**
     * 获取菜单下拉列表
     */
    List<Option> listMenuOptions();

    /**
     * 新增菜单
     *
     * @param menuForm  菜单表单对象
     */
    boolean saveMenu(MenuForm menuForm);

    /**
     * 获取路由列表
     */
    List<RouteVO> listRoutes( Set<String> roles);

    /**
     * 修改菜单显示状态
     * 
     * @param menuId 菜单ID
     * @param visible 是否显示(1-显示 0-隐藏)
     */
    boolean updateMenuVisible(Long menuId, Integer visible);

    /**
     * 获取菜单表单数据
     *
     * @param id 菜单ID
     */
    MenuForm getMenuForm(Long id);

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     */
    boolean deleteMenu(Long id);

    /**
     * 为代码生成添加菜单
     *
     * @param parentMenuId 父菜单ID
     * @param entityName   实体名
     */
    void addMenuForCodeGeneration(Long parentMenuId,String businessName, String entityName);
}
