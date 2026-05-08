package com.github.jinahya.hello.api;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;

import java.util.Objects;

/**
 * An abstract class for testing {@link AsynchronousHelloWorldTest} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
abstract class AsynchronousHelloWorldTest
        extends HelloWorldTest {

    @BeforeEach
    void initAsynchronousService() {
        asynchronousService = Mockito.spy(new DefaultAsynchronousHelloWorld(service));
    }

//    @BeforeEach
//    void verifyWiring() throws Exception {
////        Assertions.assertInstanceOf(DefaultAsynchronousHelloWorld.class, asynchronousService);
//        final var f = DefaultAsynchronousHelloWorld.class.getDeclaredField("service");
//        f.setAccessible(true);
//        Assertions.assertSame(service, f.get(asynchronousService));
//    }

    // ------------------------------------------------------------------------------------- service
//    protected HelloWorld service() {
//        return service;
//    }

    // ------------------------------------------------------------------------- asynchronousService
//    protected AsynchronousHelloWorld asynchronousService() {
////        return asynchronousService;
//        return Objects.requireNonNull(asynchronousService, "asynchronousService is null");
//    }

    // ---------------------------------------------------------------------------------------------
    @Spy
    @Accessors(fluent = true)
    @Getter(AccessLevel.PROTECTED)
    private HelloWorld service;


    @Accessors(fluent = true)
    @Getter(AccessLevel.PROTECTED)
    private DefaultAsynchronousHelloWorld asynchronousService;
}
