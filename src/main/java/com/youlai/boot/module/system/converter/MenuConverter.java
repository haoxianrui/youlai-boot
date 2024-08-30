package com.youlai.boot.module.system.converter;

import com.youlai.boot.module.system.model.entity.SysMenu;
import com.youlai.boot.module.system.model.vo.MenuVO;
import com.youlai.boot.module.system.model.form.MenuForm;
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

    MenuVO toVo(SysMenu entity);

    @Mapping(target = "params", ignore = true)
    MenuForm toForm(SysMenu entity);

    @Mapping(target = "params", ignore = true)
    SysMenu toEntity(MenuForm menuForm);

}