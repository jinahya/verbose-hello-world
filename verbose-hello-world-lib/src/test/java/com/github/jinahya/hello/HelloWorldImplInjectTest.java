package com.github.jinahya.hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.shuffle;
import static java.util.stream.Collectors.toList;

abstract class HelloWorldImplInjectTest extends AbstractHelloWorldImplTest {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    static final String QUALIFIER_IMPL = "impl";

    static final String QUALIFIER_DEMO = "demo";

    @Override
    HelloWorld helloWorld() {
        final List<HelloWorld> helloWorlds
                = Arrays.stream(getClass().getDeclaredFields())
                .filter(f -> HelloWorld.class.isAssignableFrom(f.getType()))
                .filter(f -> f.isAnnotationPresent(Inject.class))
                .map(f -> {
                    f.setAccessible(true);
                    try {
                        return (HelloWorld) f.get(this);
                    } catch (final IllegalAccessException iae) {
                        throw new RuntimeException(iae);
                    }
                })
                .collect(toList());
        shuffle(helloWorlds);
        return helloWorlds.get(0);
    }

    @Inject
    private HelloWorld helloWorld;

    @Named(QUALIFIER_IMPL)
    @Inject
    private HelloWorld namedHelloWorldImpl;

    @Named(QUALIFIER_DEMO)
    @Inject
    private HelloWorld namedHelloWorldDemo;

    @ImplQualifier
    @Inject
    private HelloWorld qualifiedHelloWorldImpl;

    @DemoQualifier
    @Inject
    private HelloWorld qualifiedHelloWorldDemo;
}
