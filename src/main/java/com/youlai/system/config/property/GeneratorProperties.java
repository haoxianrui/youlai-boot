package com.youlai.system.config.property;

import cn.hutool.core.map.MapUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

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

    private Map<String, TemplateConfig> templateConfigs = MapUtil.newHashMap(true);

    /**
     * 模板配置
     */
    @Data
    public static  class TemplateConfig{

        private String templatePath;

        private String packageName;

    }


}
