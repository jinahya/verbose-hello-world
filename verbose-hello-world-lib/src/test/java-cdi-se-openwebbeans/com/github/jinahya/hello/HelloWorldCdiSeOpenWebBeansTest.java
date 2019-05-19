package com.github.jinahya.hello;

import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.bridge.SLF4JBridgeHandler;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * A class extends {@link HelloWorldCdiSeTest} for Apache OpenWebBeans.
 *
 * @see <a href="<a href="https://openwebbeans.apache.org/">Apache OpenWebBeans</a>
 */
class HelloWorldCdiSeOpenWebBeansTest extends HelloWorldCdiSeTest {

    private static final Logger logger = getLogger(lookup().lookupClass());

    /**
     * Removes handlers from the root logger and installs SLF4J bridge handler.
     *
     * @see SLF4JBridgeHandler#removeHandlersForRootLogger()
     * @see SLF4JBridgeHandler#install()
     */
    @BeforeAll
    static void bridgetSlf4j() {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        logger.debug("handlers removed from the root logger");
        SLF4JBridgeHandler.install();
        logger.debug("bridget handler installed");
    }
}
