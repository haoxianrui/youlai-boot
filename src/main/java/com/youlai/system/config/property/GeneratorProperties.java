package com.youlai.system.config.property;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.map.MapUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
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


    /**
     * 默认配置
     */
    private DefaultConfig defaultConfig ;

    /**
     * 模板配置
     */
    private Map<String, TemplateConfig> templateConfigs = MapUtil.newHashMap(true);

    /**
     * 后端应用名
     */

    private String backendAppName;

    /**
     * 前端应用名
     */
    private String frontendAppName;

    /**
     * 排除数据表
     */
    private List<String> excludeTables;

    /**
     * 模板配置
     */
    @Data
    public static class TemplateConfig {

        private String templatePath;

        private String packageName;

        /**
         * 文件扩展名，如 .java
         */
        private String extension = FileNameUtil.EXT_JAVA;

    }

    /**
     * 默认配置
     */
    @Data
    public static class DefaultConfig {

        private String author;


    }


}
