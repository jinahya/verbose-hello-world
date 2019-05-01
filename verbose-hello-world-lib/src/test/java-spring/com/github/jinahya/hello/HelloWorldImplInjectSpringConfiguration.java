package com.github.jinahya.hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.invoke.MethodHandles;

@Configuration
class HelloWorldImplInjectSpringConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Bean
    HelloWorld helloWorld() {
        return new HelloWorldImpl();
    }
}
