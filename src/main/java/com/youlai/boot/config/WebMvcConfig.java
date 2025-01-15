package com.youlai.boot.config;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.HibernateValidator;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.validation.beanvalidation.SpringConstraintValidatorFactory;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * WebMvc 自动装配配置
 *
 * @author Ray
 * @since 2020/10/16
 */
@Configuration
@Slf4j
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 配置消息转换器
     *
     * @param converters 消息转换器列表
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = jackson2HttpMessageConverter.getObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);

        // 处理 Long 和 BigInteger 类型，避免前端精度丢失问题
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(BigInteger.class, ToStringSerializer.instance);
        simpleModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(
                DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN).withZone(ZoneId.of( "GMT+8"))
        ));
        objectMapper.registerModule(simpleModule);
        objectMapper.setDateFormat(new SimpleDateFormat(DatePattern.NORM_DATETIME_PATTERN));


        jackson2HttpMessageConverter.setObjectMapper(objectMapper);
        converters.add(1, jackson2HttpMessageConverter);
    }

    /**
     * 配置校验器
     *
     * @param autowireCapableBeanFactory 用于注入 SpringConstraintValidatorFactory
     * @return Validator 实例
     */
    @Bean
    public Validator validator(final AutowireCapableBeanFactory autowireCapableBeanFactory) {
        try (ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
                .configure()
                .failFast(true) // failFast=true 时，遇到第一个校验失败则立即返回，false 表示校验所有参数
                .constraintValidatorFactory(new SpringConstraintValidatorFactory(autowireCapableBeanFactory))
                .buildValidatorFactory()) {

            // 使用 try-with-resources 确保 ValidatorFactory 被正确关闭
            return validatorFactory.getValidator();
        }
    }
}
