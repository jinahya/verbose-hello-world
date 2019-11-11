package com.github.jinahya.hello;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Named;

import static com.github.jinahya.hello.HelloWorldDiTest.DEMO;
import static com.github.jinahya.hello.HelloWorldDiTest.IMPL;

/**
 * A configuration for providing {@link HelloWorld} beans.
 */
@Configuration
class HelloWorldDiSpringConfiguration {

    // -----------------------------------------------------------------------------------------------------------------
    @Named(DEMO)
    @Bean
    HelloWorld namedDemo() {
        return new HelloWorldDemo();
    }

    @Named(IMPL)
    @Bean
    HelloWorld namedImpl() {
        return new HelloWorldImpl();
    }

    @QualifiedDemo
    @Bean
    HelloWorld qualifiedDemo() {
        return new HelloWorldDemo();
    }

    @QualifiedImpl
    @Bean
    HelloWorld qualifiedImpl() {
        return new HelloWorldImpl();
    }
}
