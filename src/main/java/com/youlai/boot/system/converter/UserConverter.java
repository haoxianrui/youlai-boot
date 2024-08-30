package com.youlai.boot.system.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.system.model.entity.SysUser;
import com.youlai.boot.system.model.vo.UserInfoVO;
import com.youlai.boot.system.model.vo.UserPageVO;
import com.youlai.boot.system.model.vo.UserProfileVO;
import com.youlai.boot.system.model.bo.UserBO;
import com.youlai.boot.system.model.form.UserForm;
import com.youlai.boot.system.model.dto.UserImportDTO;
import com.youlai.boot.system.model.form.UserProfileForm;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * 用户对象转换器
 *
 * @author haoxr
 * @since 2022/6/8
 */
@Mapper(componentModel = "spring")
public interface UserConverter {

    @Mappings({
            @Mapping(target = "genderLabel", expression = "java(com.youlai.boot.common.base.IBaseEnum.getLabelByValue(bo.getGender(), com.youlai.boot.common.enums.GenderEnum.class))")
    })
    UserPageVO toPageVo(UserBO bo);

    Page<UserPageVO> toPageVo(Page<UserBO> bo);

    UserForm toForm(SysUser entity);

    @InheritInverseConfiguration(name = "toForm")
    SysUser toEntity(UserForm entity);

    @Mappings({
            @Mapping(target = "userId", source = "id")
    })
    UserInfoVO toUserInfoVo(SysUser entity);

    SysUser toEntity(UserImportDTO vo);


    UserProfileVO toProfileVO(UserBO bo);

    SysUser toEntity(UserProfileForm formData);
}
