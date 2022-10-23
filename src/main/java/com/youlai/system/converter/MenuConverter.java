package com.youlai.system.converter;

import com.youlai.system.pojo.entity.SysMenu;
import com.youlai.system.pojo.po.RoutePO;
import com.youlai.system.pojo.vo.menu.MenuVO;
import com.youlai.system.pojo.vo.menu.RouteVO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * 菜单对象转换器
 *
 * @author haoxr
 * @date 2022/7/29
 */
@Mapper(componentModel = "spring")
public interface MenuConverter {

    MenuVO entity2VO(SysMenu entity);


}