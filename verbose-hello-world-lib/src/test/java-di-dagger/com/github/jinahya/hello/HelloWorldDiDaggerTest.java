package com.github.jinahya.hello;

import dagger.MembersInjector;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

class HelloWorldDiDaggerTest extends HelloWorldDiTest {

    private static final Logger logger = getLogger(lookup().lookupClass());

    @BeforeEach
    private void inject() {
        final MembersInjector<HelloWorldDiDaggerTest> injector = DaggerHelloWorldDiDaggerComponent.create();
        injector.injectMembers(this);
    }
}
