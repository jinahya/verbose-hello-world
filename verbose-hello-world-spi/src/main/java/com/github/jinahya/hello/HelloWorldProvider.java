package com.github.jinahya.hello;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.ServiceLoader.load;

/**
 * An interface for hello world provider.
 */
public interface HelloWorldProvider {

    /**
     * Returns a stream of hello world providers.
     *
     * @return a stream of hello world providers
     */
    static Stream<HelloWorldProvider> getProviderStream() {
        return StreamSupport.stream(load(HelloWorldProvider.class).spliterator(), false);
    }

    static <R> R applyProviderStream(
            final Function<? super Stream<? extends HelloWorldProvider>, ? extends R> function) {
        return function.apply(getProviderStream());
    }

    static <U, R> R applyProviderStream(
            final Supplier<? extends U> supplier,
            final BiFunction<? super Stream<? extends HelloWorldProvider>, ? super U, ? extends R> function) {
        return applyProviderStream(s -> function.apply(s, supplier.get()));
    }

    static void acceptProviderStream(final Consumer<? super Stream<? extends HelloWorldProvider>> consumer) {
        applyProviderStream(s -> {
            consumer.accept(s);
            return null;
        });
    }

    static <U> void acceptProviderStream(
            final Supplier<? extends U> supplier,
            final BiConsumer<? super Stream<? extends HelloWorldProvider>, ? super U> consumer) {
        acceptProviderStream(s -> consumer.accept(s, supplier.get()));
    }

    /**
     * Find any available hello world provider and returns the value of {@link #getInstance()}.
     *
     * @return an available instance of {@link HelloWorld}.
     */
    static HelloWorld findAnyAvailableInstance() {
        return applyProviderStream(
                s -> s.filter(HelloWorldProvider::isAvailable)
                        .findAny()
                        .orElseThrow(() -> new RuntimeException("no available services"))
                        .getInstance());
    }

    /**
     * Returns a boolean flag for availability of this provider. The {@code default} {@code isAvailable} method of
     * {@code HelloWorldProvider} class returns {@code true}.
     *
     * @return {@code true} if available, {@code false} otherwise.
     */
    default boolean isAvailable() {
        return true;
    }

    /**
     * Provides an instance of {@link HelloWorld}.
     *
     * @return an instance of {@link HelloWorld}.
     */
    HelloWorld getInstance();
}
