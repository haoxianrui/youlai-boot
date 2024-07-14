package com.youlai.system.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 代码生成配置属性
 *
 * @author Ray
 * @since 2.11.0
 */
@Component
@ConfigurationProperties(prefix = "generator")
@Data
public class GeneratorProperties {
}
