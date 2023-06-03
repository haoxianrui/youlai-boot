package com.youlai.system.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.youlai.system.handler.mybatisplus.MyDataPermissionHandler;
import com.youlai.system.handler.mybatisplus.MyMetaObjectHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * mybatis-plus 配置类
 *
 * @author haoxr
 * @since 2022/7/2
 */
@Configuration
@EnableTransactionManagement
public class MybatisPlusConfig {


    @Value("${system.config.data-permission-enabled}")
    private Boolean dataPermissionEnabled;

    /**
     * 分页插件和数据权限插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        //数据权限
        if (dataPermissionEnabled) {
            interceptor.addInnerInterceptor(new DataPermissionInterceptor(new MyDataPermissionHandler()));
        }
        //分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));

        return interceptor;
    }

    /**
     * 自动填充数据库创建人、创建时间、更新人、更新时间
     */
    @Bean
    public GlobalConfig globalConfig() {
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setMetaObjectHandler(new MyMetaObjectHandler());
        return globalConfig;
    }

}
