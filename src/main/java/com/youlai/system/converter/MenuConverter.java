package com.youlai.system.converter;

import com.youlai.system.model.entity.SysMenu;
import com.youlai.system.model.form.MenuForm;
import com.youlai.system.model.vo.MenuVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 菜单对象转换器
 *
 * @author Ray Hao
 * @since 2024/5/26
 */
@Mapper(componentModel = "spring")
public interface MenuConverter {

    MenuVO entity2Vo(SysMenu entity);

    @Mapping(target = "params", ignore = true)
    MenuForm convertToForm(SysMenu entity);

    @Mapping(target = "params", ignore = true)
    SysMenu convertToEntity(MenuForm menuForm);

}