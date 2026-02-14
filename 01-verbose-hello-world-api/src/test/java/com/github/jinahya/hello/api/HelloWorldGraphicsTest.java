package com.github.jinahya.hello.api;

import org.junit.platform.commons.util.ReflectionUtils;

import java.util.Objects;

/**
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
abstract class HelloWorldGraphicsTest<T extends HelloWorldGraphics> {

    HelloWorldGraphicsTest(final Class<T> type) {
        super();
        this.type = Objects.requireNonNull(type, "type is null");
    }
    // ---------------------------------------------------------------------------------------- type

    /**
     * Returns a new instance of {@link #type}.
     *
     * @return a new instance of {@link #type}.
     */
    T newTypeInstance() {
        return ReflectionUtils.newInstance(type);
    }

    // ------------------------------------------------------------------------------------ instance

    /**
     * Returns an instance of {@link #type}.
     *
     * @return an instance of {@link #type}.
     */
    protected final HelloWorldGraphics service() {
        T result = _instance;
        if (result == null) {
            result = _instance = newTypeInstance();
        }
        return result;
    }

    // ---------------------------------------------------------------------------------------------
    final Class<T> type;

    private T _instance;
}
