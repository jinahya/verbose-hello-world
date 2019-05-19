package com.github.jinahya.hello;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.Binder;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

class HelloWorldDiHk2Test extends HelloWorldDiTest {

    private static final Logger logger = getLogger(lookup().lookupClass());

    @BeforeEach
    private void inject() {
        final Binder binder = new HelloWorldDiHk2Binder();
        final ServiceLocator locator = ServiceLocatorUtilities.bind(binder);
        locator.inject(this);
    }
}
