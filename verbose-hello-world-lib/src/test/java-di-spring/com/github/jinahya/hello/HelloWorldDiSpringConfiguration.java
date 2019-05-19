package com.github.jinahya.hello;

import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Named;

import static com.github.jinahya.hello.HelloWorldDiTest.DEMO;
import static com.github.jinahya.hello.HelloWorldDiTest.IMPL;
import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * A configuration for providing {@link HelloWorld} beans.
 */
@Configuration
class HelloWorldDiSpringConfiguration {

    private static final Logger logger = getLogger(lookup().lookupClass());

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
