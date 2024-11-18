package com.github.jinahya.hello;

import java.util.Objects;

abstract class HelloWorldServiceProvider_<T extends HelloWorld>
        implements HelloWorldServiceProvider {

    HelloWorldServiceProvider_(final Class<T> implClass) {
        super();
        this.implClass = Objects.requireNonNull(implClass, "implClass is null");
    }

    @Override
    public HelloWorld getService() {
        try {
            final var constructor = implClass.getDeclaredConstructor();
            if (!constructor.canAccess(null)) {
                constructor.setAccessible(true);
            }
            return constructor.newInstance();
        } catch (final ReflectiveOperationException roe) {
            throw new RuntimeException("failed to instantiate " + implClass, roe);
        }
    }

    private final Class<T> implClass;
}
