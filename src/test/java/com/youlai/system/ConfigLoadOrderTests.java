package com.youlai.system;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

/**
 * SpringBoot 配置加载顺序单元测试类
 *
 * @author haoxr
 * @since 2023/02/23
 */
@SpringBootTest
@Slf4j
public class ConfigLoadOrderTests {

    /**
     * 测试配置加载顺序
     */
    @Test
    public void testConfigLoadOrder(ApplicationContext context) {
        Environment environment = context.getEnvironment();
        String property = environment.getProperty("config.name");
        log.info(property);
    }

}
