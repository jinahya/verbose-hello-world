package com.github.jinahya.hello.api;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.mockito.Mockito;

/**
 * An abstract base for tests that demonstrate a reactive library's <em>own</em> publisher-creation
 * idioms against {@link HelloWorld} / {@link AsynchronousHelloWorld} — i.e., the library defines
 * its own publisher and fetches the
 * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> directly from the service
 * (no intermediate {@code org.reactivestreams.Publisher}).
 * <p>
 * Subclasses are expected to be named {@code ReactiveHelloWorld_<Library>_Test}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
abstract class ReactiveHelloWorld__Test {

    ReactiveHelloWorld__Test() {
        super();
        this.synchronousService = Mockito.mock(HelloWorld.class, Mockito.CALLS_REAL_METHODS);
        this.asynchronousService = Mockito.spy(
                new DefaultAsynchronousHelloWorld(synchronousService, Runnable::run)
        );
    }

    // ---------------------------------------------------------------------------- java.lang.Object
    @Override
    public String toString() {
        return ReactiveHelloWorldTestUtils.toSimplifiedString(super.toString());
    }

    // -------------------------------------------------------------------------- synchronousService

    // ------------------------------------------------------------------------- asynchronousService

    // ---------------------------------------------------------------------------------------------
    @Accessors(fluent = true)
    @Getter(AccessLevel.PACKAGE)
    private final HelloWorld synchronousService;

    @Accessors(fluent = true)
    @Getter(AccessLevel.PACKAGE)
    private final AsynchronousHelloWorld asynchronousService;
}
