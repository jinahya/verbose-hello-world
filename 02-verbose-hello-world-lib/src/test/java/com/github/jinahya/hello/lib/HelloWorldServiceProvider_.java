package com.github.jinahya.hello.lib;

import com.github.jinahya.hello.api.HelloWorld;

import java.util.Objects;

/**
 * An abstract class for implementing {@link HelloWorldServiceProvider} interface.
 *
 * @param <T> service type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
abstract class HelloWorldServiceProvider_<T extends HelloWorld>
        implements HelloWorldServiceProvider {

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance with specified service class.
     *
     * @param serviceClass the service class.
     */
    HelloWorldServiceProvider_(final Class<T> serviceClass) {
        super();
        this.serviceClass = Objects.requireNonNull(serviceClass, "serviceClass is null");
    }

    // ------------------------------------------------------------------- HelloWorldServiceProvider
    @Override
    public HelloWorld getService() {
        try {
            final var constructor = serviceClass.getDeclaredConstructor();
            if (!constructor.canAccess(null)) {
                constructor.setAccessible(true);
            }
            return constructor.newInstance();
        } catch (final ReflectiveOperationException roe) {
            throw new RuntimeException("failed to instantiate " + serviceClass, roe);
        }
    }

    // ----------------------------------------------------------------------------- INSTANCE_FIELDS
    private final Class<T> serviceClass;
}
