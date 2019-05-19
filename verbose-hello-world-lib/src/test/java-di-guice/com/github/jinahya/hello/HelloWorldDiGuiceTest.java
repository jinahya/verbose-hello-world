package com.github.jinahya.hello;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * An injection test for Google Guice.
 *
 * @see <a href="https://github.com/google/guice">Guice</a>
 */
class HelloWorldDiGuiceTest extends HelloWorldDiTest {

    private static final Logger logger = getLogger(lookup().lookupClass());

    @BeforeEach
    private void inject() {
        final Injector injector = Guice.createInjector(new HelloWorldDiGuiceModule());
        injector.injectMembers(this);
    }
}
