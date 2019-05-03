package com.github.jinahya.hello;

import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.jsr330.Salta;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

class HelloWorldImplInjectSaltaTest extends HelloWorldImplInjectTest {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @BeforeEach
    private void inject() {
        final Injector injector = Salta.createInjector(new HelloWorldImplInjectSaltaModule());
        injector.injectMembers(this);
    }
}
