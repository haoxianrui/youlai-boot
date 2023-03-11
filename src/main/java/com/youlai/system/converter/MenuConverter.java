package com.youlai.system.converter;

import com.youlai.system.pojo.entity.SysMenu;
import com.youlai.system.pojo.form.MenuForm;
import com.youlai.system.pojo.vo.MenuVO;
import org.mapstruct.Mapper;

/**
 * 菜单对象转换器
 *
 * @author haoxr
 * @date 2022/7/29
 */
@Mapper(componentModel = "spring")
public interface MenuConverter {

    MenuVO entity2Vo(SysMenu entity);


    MenuForm entity2Form(SysMenu entity);

    SysMenu form2Entity(MenuForm menuForm);

}