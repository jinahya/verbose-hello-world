package com.github.jinahya.hello;

import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

class HelloWorldDiSpringTest extends HelloWorldDiTest {

    private static final Logger logger = getLogger(lookup().lookupClass());

    @BeforeEach
    private void inject() {
        final GenericApplicationContext context
                = new AnnotationConfigApplicationContext(HelloWorldDiSpringConfiguration.class);
        final AutowireCapableBeanFactory factory = context.getAutowireCapableBeanFactory();
        factory.autowireBean(this);
    }
}