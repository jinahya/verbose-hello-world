package com.github.jinahya.hello;

import dagger.ObjectGraph;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

class HelloWorldImplInjectDaggerTest extends HelloWorldImplInjectTest {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @BeforeEach
    private void inject() {
        final ObjectGraph objectGraph = ObjectGraph.create(new HelloWorldImplInjectDaggerModule());
        objectGraph.inject(this);
    }
}
