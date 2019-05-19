package com.github.jinahya.hello;

import com.google.inject.AbstractModule;
import org.slf4j.Logger;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * A module for injecting instances of {@link HelloWorld}.
 */
class HelloWorldModule extends AbstractModule {

    private static final Logger logger = getLogger(lookup().lookupClass());

    /**
     * The fully qualified name of the {@code HelloWorldMain} class.
     */
    private static final String HELLO_WORLD_IMPL_FQCN = "com.github.jinahya.hello.HelloWorldImpl";

    /**
     * The class found with {@link #HELLO_WORLD_IMPL_FQCN}.
     */
    private static final Class<? extends HelloWorld> HELLO_WORLD_IMPL_CLASS;

    static {
        try {
            HELLO_WORLD_IMPL_CLASS = Class.forName(HELLO_WORLD_IMPL_FQCN).asSubclass(HelloWorld.class);
        } catch (final ClassNotFoundException cnfe) {
            throw new InstantiationError(cnfe.getMessage());
        }
    }

    @Override
    protected void configure() {
        // TODO: implement!
    }
}
