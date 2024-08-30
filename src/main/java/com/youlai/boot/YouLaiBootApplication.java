package com.youlai.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 *
 * @author Ray
 */
@SpringBootApplication
@ConfigurationPropertiesScan
@EnableScheduling
public class YouLaiBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(YouLaiBootApplication.class, args);
    }

}
