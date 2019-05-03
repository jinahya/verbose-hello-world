package com.github.jinahya.hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Named;
import java.lang.invoke.MethodHandles;

import static java.util.concurrent.ThreadLocalRandom.current;

/**
 * A configuration for providing {@link HelloWorld} beans.
 */
@Configuration
class HelloWorldImplInjectSpringConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Bean
    HelloWorld any() {
        return current().nextBoolean() ? new HelloWorldImpl() : new HelloWorldDemo();
    }

    @Named(HelloWorldImplInjectTest.QUALIFIER_IMPL)
    @Bean
    HelloWorld namedImpl() {
        return new HelloWorldImpl();
    }

    @Named(HelloWorldImplInjectTest.QUALIFIER_DEMO)
    @Bean
    HelloWorld namedDemo() {
        return new HelloWorldDemo();
    }

    @ImplQualifier
    @Bean
    HelloWorld qualifiedImpl() {
        return new HelloWorldImpl();
    }

    @DemoQualifier
    @Bean
    HelloWorld qualifiedDemo() {
        return new HelloWorldDemo();
    }
}
