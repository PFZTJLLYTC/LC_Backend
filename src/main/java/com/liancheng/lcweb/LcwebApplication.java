package com.liancheng.lcweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

//不加exclude会强迫使用自动生成的权限系统
@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.security.
        servlet.SecurityAutoConfiguration.class})
@EnableJpaAuditing
@EnableScheduling
public class LcwebApplication {

    public static void main(String[] args) {
        SpringApplication.run(LcwebApplication.class, args);
    }

}
