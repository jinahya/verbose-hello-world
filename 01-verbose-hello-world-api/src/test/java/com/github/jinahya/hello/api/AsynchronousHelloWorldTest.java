package com.github.jinahya.hello.api;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.mockito.Spy;

/**
 * An abstract class for testing {@link AsynchronousHelloWorldTest} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AsynchronousHelloWorldTest
        extends HelloWorldTest {

    @BeforeEach
    void initAsynchronousService() {
        asynchronousService = Mockito.spy(new DefaultAsynchronousHelloWorld(synchronousService));
    }

    // -------------------------------------------------------------------------- synchronousService

    // ------------------------------------------------------------------------- asynchronousService

    // ---------------------------------------------------------------------------------------------
    @Spy
    @Accessors(fluent = true)
    @Getter(AccessLevel.PROTECTED)
    private HelloWorld synchronousService;


    @Accessors(fluent = true)
    @Getter(AccessLevel.PROTECTED)
    private AsynchronousHelloWorld asynchronousService;
}
