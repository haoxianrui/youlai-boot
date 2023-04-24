package com.youlai.system;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.youlai.system.pojo.form.RoleForm;
import com.youlai.system.pojo.form.UserForm;
import com.youlai.system.sample.DynamicDataSourceSample;
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
    private DynamicDataSourceSample dynamicDataSourceSample;


    @Test
    void testDynamicDataSourceWithTransactional() {
        String newTypeCod = RandomUtil.randomString(RandomUtil.BASE_CHAR, 6).toUpperCase();
        boolean result = dynamicDataSourceSample.updateDictTypeCode(2L, newTypeCod);
        log.info("testDynamicDataSourceWithTransactional result:{}", result);
    }
}