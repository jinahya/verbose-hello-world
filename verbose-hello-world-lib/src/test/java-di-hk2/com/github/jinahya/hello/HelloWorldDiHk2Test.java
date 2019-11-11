package com.github.jinahya.hello;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.Binder;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.jupiter.api.BeforeEach;

/**
 * An extended {@link HelloWorldDiTest} which uses {@link HelloWorldDiHk2Binder} as a binder.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class HelloWorldDiHk2Test extends HelloWorldDiTest {

    // -----------------------------------------------------------------------------------------------------------------
    @BeforeEach
    private void inject() {
        final Binder binder = new HelloWorldDiHk2Binder();
        final ServiceLocator locator = ServiceLocatorUtilities.bind(binder);
        locator.inject(this);
    }
}
