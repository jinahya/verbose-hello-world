package com.github.jinahya.hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Named;
import java.lang.invoke.MethodHandles;

import static com.github.jinahya.hello.HelloWorldImplInjectTest.DEMO;
import static com.github.jinahya.hello.HelloWorldImplInjectTest.IMPL;

/**
 * A configuration for providing {@link HelloWorld} beans.
 */
@Configuration
class HelloWorldImplInjectSpringConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Bean
    @Named(DEMO)
    HelloWorld namedDemo() {
        return new HelloWorldDemo();
    }

    @Bean
    @Named(IMPL)
    HelloWorld namedImpl() {
        return new HelloWorldImpl();
    }

    @Bean
    @QualifiedDemo
    HelloWorld qualifiedDemo() {
        return new HelloWorldDemo();
    }

    @Bean
    @QualifiedImpl
    HelloWorld qualifiedImpl() {
        return new HelloWorldImpl();
    }
}
