package com.github.jinahya.hello.api;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;

/**
 * An abstract class for testing {@link AsynchronousHelloWorld} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public abstract class AsynchronousHelloWorldTest {

    protected AsynchronousHelloWorldTest() {
        super();
        this.synchronousService = Mockito.mock(HelloWorld.class, Mockito.CALLS_REAL_METHODS);
        this.asynchronousService = Mockito.spy(
                new DefaultAsynchronousHelloWorld(synchronousService, Runnable::run)
        );
    }

    // -------------------------------------------------------------------------- synchronousService

    // ------------------------------------------------------------------------- asynchronousService

    // ---------------------------------------------------------------------------------------------
    @Accessors(fluent = true)
    @Getter(AccessLevel.PROTECTED)
    private final HelloWorld synchronousService;

    @Accessors(fluent = true)
    @Getter(AccessLevel.PROTECTED)
    private final AsynchronousHelloWorld asynchronousService;
}
