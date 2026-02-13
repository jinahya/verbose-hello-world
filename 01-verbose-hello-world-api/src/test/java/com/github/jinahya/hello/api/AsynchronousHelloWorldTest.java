package com.github.jinahya.hello.api;

import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.Mockito;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

/**
 * An abstract class for testing a specific subclass of {@link AsynchronousHelloWorldTest}.
 *
 * @param <T> subclass type parameter.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
abstract class AsynchronousHelloWorldTest<T extends AsynchronousHelloWorld> {

    private static final System.Logger logger = System.getLogger(
            MethodHandles.lookup().lookupClass().getName()
    );

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance for testing the specified type.
     *
     * @param type the type to test
     */
    AsynchronousHelloWorldTest(final Class<T> type) {
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
    protected final AsynchronousHelloWorld service() {
        T result = _instance;
        if (result == null) {
            result = _instance = Mockito.spy(newTypeInstance());
        }
        return result;
    }

    // ---------------------------------------------------------------------------------------------
    final Class<T> type;

    private volatile T _instance;
}
