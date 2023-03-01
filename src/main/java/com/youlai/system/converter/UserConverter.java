package com.youlai.system.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.system.pojo.bo.UserBO;
import com.youlai.system.pojo.entity.SysUser;
import com.youlai.system.pojo.form.UserForm;
import com.youlai.system.pojo.bo.UserFormBO;
import com.youlai.system.pojo.vo.UserInfoVO;
import com.youlai.system.pojo.vo.UserPageVO;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * 用户对象转换器
 *
 * @author haoxr
 * @date 2022/6/8
 */
@Mapper(componentModel = "spring")
public interface UserConverter {

    @Mappings({
            @Mapping(target = "genderLabel", expression = "java(com.youlai.system.common.base.IBaseEnum.getLabelByValue(po.getGender(), com.youlai.system.common.enums.GenderEnum.class))")
    })
    UserPageVO po2Vo(UserBO po);

    Page<UserPageVO> po2Vo(Page<UserBO> po);

    UserForm po2Form(UserFormBO po);

    UserForm entity2Form(SysUser entity);

    @InheritInverseConfiguration(name = "entity2Form")
    SysUser form2Entity(UserForm entity);

    @Mappings({
            @Mapping(target = "userId", source = "id")
    })
    UserInfoVO entity2LoginUser(SysUser entity);


}
