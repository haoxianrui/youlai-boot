package com.youlai.system;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * SpringBoot 配置加载顺序单元测试类
 *
 * @author: haoxr
 * @date: 2023/02/23
 */
@SpringBootTest(classes = ConfigLoadOrderTests.class)
@Slf4j
public class ConfigLoadOrderTests {


    @Value("${config.name}")
    private String configName;

    /**
     *  测试配置加载顺序
     */
    @Test
    public void testConfigLoadOrder() {
        log.info("配置加载顺序 config.name:{}", configName);

    }

}
