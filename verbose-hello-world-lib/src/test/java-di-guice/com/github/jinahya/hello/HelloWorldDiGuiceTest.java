package com.github.jinahya.hello;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.BeforeEach;

/**
 * An injection test for Google Guice.
 *
 * @see <a href="https://github.com/google/guice">Guice</a>
 */
class HelloWorldDiGuiceTest extends HelloWorldDiTest {

    // -----------------------------------------------------------------------------------------------------------------
    @BeforeEach
    private void inject() {
        final Injector injector = Guice.createInjector(new HelloWorldDiGuiceModule());
        injector.injectMembers(this);
    }
}
