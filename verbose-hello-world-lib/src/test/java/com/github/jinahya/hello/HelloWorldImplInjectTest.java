package com.github.jinahya.hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.List;

import static java.util.Arrays.stream;
import static java.util.Collections.shuffle;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertFalse;

abstract class HelloWorldImplInjectTest extends AbstractHelloWorldImplTest {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * An injection qualifier for {@link HelloWorldImpl}.
     */
    static final String IMPL = "impl";

    /**
     * An injection qualifier for {@link HelloWorldDemo}.
     */
    static final String DEMO = "demo";

    @Override
    HelloWorld helloWorld() {
        final List<Field> fields = stream(getClass().getDeclaredFields())
                .filter(f -> HelloWorld.class.equals(f.getType()))
                .filter(f -> f.isAnnotationPresent(Inject.class))
                .collect(toList());
        assertFalse(fields.isEmpty());
        final List<HelloWorld> values = fields.stream()
                .map(f -> {
                    try {
                        return (HelloWorld) f.get(this);
                    } catch (final IllegalAccessException iae) {
                        throw new RuntimeException(iae);
                    }
                })
                .collect(toList());
        assertFalse(values.isEmpty());
        shuffle(values);
        return values.get(0);
    }

    @Inject
    @Named(IMPL)
    HelloWorld namedImpl;

    @Inject
    @Named(DEMO)
    HelloWorld namedDemo;

    @Inject
    @QualifiedImpl
    HelloWorld qualifiedImpl;

    @Inject
    @QualifiedDemo
    HelloWorld qualifiedDemo;
}
