package com.youlai.system;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.youlai.system.pojo.form.RoleForm;
import com.youlai.system.pojo.form.UserForm;
import com.youlai.system.service.SysRoleService;
import com.youlai.system.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Slf4j
class DynamicDataSourceTest {

    @Autowired
    private SysUserService userService;

    @Autowired
    private SysRoleService roleService;

    private static Long userId = 3L; // 测试用户
    private static Long roleId = 3L;

    @Test
    @Transactional
    void updateUser() {
        UserForm userForm = userService.getUserFormData(userId);
        userForm.setNickname("测试用户_" + RandomUtil.randomString(RandomUtil.BASE_CHAR, 1));
        userService.updateUser(userId, userForm);

        this.updateRole();
    }


    @DS("slave")
    void updateRole() {
        RoleForm roleForm = roleService.getRoleForm(roleId);
        roleForm.setName("访问游客_" + RandomUtil.randomString(RandomUtil.BASE_CHAR, 1));
        roleService.saveRole(roleForm);
    }
}