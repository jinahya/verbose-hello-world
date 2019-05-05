package com.github.jinahya.hello;

import dagger.MembersInjector;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

class HelloWorldDiDaggerTest extends HelloWorldDiTest {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @BeforeEach
    private void inject() {
        final MembersInjector<HelloWorldDiDaggerTest> injector = DaggerHelloWorldDiDaggerComponent.create();
        injector.injectMembers(this);
    }
}
