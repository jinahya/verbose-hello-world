package com.github.jinahya.hello;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

class HelloWorldDiSpringTest extends HelloWorldDiTest {

    // -----------------------------------------------------------------------------------------------------------------
    @BeforeEach
    private void inject() {
        final GenericApplicationContext context
                = new AnnotationConfigApplicationContext(HelloWorldDiSpringConfiguration.class);
        final AutowireCapableBeanFactory factory = context.getAutowireCapableBeanFactory();
        factory.autowireBean(this);
    }
}