package com.youlai.system.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.youlai.system.converter.UserConverter;
import com.youlai.system.pojo.entity.SysUser;
import com.youlai.system.pojo.form.UserForm;
import com.youlai.system.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class SysUserServiceImplTest {

    @Autowired
    private SysUserService userService;
    @Autowired
    private UserConverter userConverter;

    @Test
    void updateUser() {
        Long userId = 3L;
        SysUser entity = userService.getById(userId);
        UserForm userForm = userConverter.entity2Form(entity);
        String nickname = "测试小用户_" + RandomUtil.randomString(RandomUtil.BASE_CHAR, 1);
        userForm.setNickname(nickname);

        userService.updateUser(userId, userForm);
    }
}