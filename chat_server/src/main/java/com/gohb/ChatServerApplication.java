package com.gohb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ChatServerApplication  extends SpringBootServletInitializer {

    @Bean
    public SpringUtil getSpringUtil(){
        return new SpringUtil();
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(ChatServerApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(ChatServerApplication.class, args);
    }

}
