package com.github.jinahya.hello;

import dagger.MembersInjector;
import org.junit.jupiter.api.BeforeEach;

class HelloWorldDiDaggerTest extends HelloWorldDiTest {

    @BeforeEach
    private void inject() {
        final MembersInjector<HelloWorldDiDaggerTest> injector = DaggerHelloWorldDiDaggerComponent.create();
        injector.injectMembers(this);
    }
}
