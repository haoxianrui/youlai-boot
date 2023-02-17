package com.youlai.system.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

/**
 * 接口文档配置
 *
 * @author haoxr
 * @date 2023/2/17
 */
@Configuration
public class SwaggerConfig {


    /**
     * 接口分组-系统接口
     *
     * @param operationCustomizer
     * @return
     */
    @Bean
    public GroupedOpenApi systemApi(OperationCustomizer operationCustomizer) {
        return GroupedOpenApi.builder()
                .group("系统接口")
                .packagesToScan("com.youlai.system.controller")
                .pathsToMatch( "/api/**")
                .addOperationCustomizer(operationCustomizer)
                .build();
    }

    /**
     * 鉴权
     *
     * @return
     */
    @Bean
    public OperationCustomizer operationCustomizer() {
        return (operation, handlerMethod) -> operation.addParametersItem(
                new Parameter()
                        .in(SecurityScheme.In.HEADER.toString())
                        .name(HttpHeaders.AUTHORIZATION)
                        .required(true)
                        .description("token 校验"));
    }


    /**
     * 接口信息
     *
     * @return
     */
    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI().info(
                new Info()
                        .title("youlai-boot接口文档")
                        .version("0.0.1")
                        .description("youlai-boot接口文档")
                        .license(new License().name("Apache 2.0")
                                .url("https://www.youlai.tech")
                        )
        );
    }

}
