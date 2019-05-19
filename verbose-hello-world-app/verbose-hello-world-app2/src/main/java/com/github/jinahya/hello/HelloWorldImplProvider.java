package com.github.jinahya.hello;

import java.lang.reflect.Constructor;

public class HelloWorldImplProvider implements HelloWorldProvider {

    private static final String HELLO_WORLD_IMPL_FQCN = "com.github.jinahya.hello.HelloWorldImpl";

    private static final Class<? extends HelloWorld> HELLO_WORLD_IMPL_CLASS;

    static {
        try {
            HELLO_WORLD_IMPL_CLASS = Class.forName(HELLO_WORLD_IMPL_FQCN).asSubclass(HelloWorld.class);
        } catch (final ClassNotFoundException cnfe) {
            throw new InstantiationError(cnfe.getMessage());
        }
    }

    private static final Constructor<? extends HelloWorld> HELLO_WORLD_IMPL_CONSTRUCTOR;

    static {
        try {
            HELLO_WORLD_IMPL_CONSTRUCTOR = HELLO_WORLD_IMPL_CLASS.getConstructor();
        } catch (final NoSuchMethodException nsme) {
            throw new InstantiationError(nsme.getMessage());
        }
    }

    @Override
    public HelloWorld getInstance() {
        try {
            return HELLO_WORLD_IMPL_CONSTRUCTOR.newInstance();
        } catch (final ReflectiveOperationException roe) {
            throw new RuntimeException("failed to instantiate", roe);
        }
    }
}
