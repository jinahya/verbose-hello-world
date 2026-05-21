package com.github.jinahya.hello.app3;

/*-
 * #%L
 * verbose-hello-world-app3
 * %%
 * Copyright (C) 2018 - 2019 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.github.jinahya.hello.api.AsynchronousHelloWorld;
import com.github.jinahya.hello.api.HelloWorld;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import jakarta.inject.Inject;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

/**
 * A program whose {@link #main()} method obtains an {@link AsynchronousHelloWorld} through
 * <a href="https://github.com/google/guice">Guice</a> constructor injection, asynchronously
 * appends {@code hello, world} to a temp file on {@link ForkJoinPool#commonPool()}, reads the file
 * back, and prints it to {@link System#out}. The returned future is joined so the program does not
 * exit before the chain completes.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see Guice#createInjector(com.google.inject.Module...)
 */
@SuppressWarnings({
        "java:S106" // Standard outputs should not be used directly to log anything
})
class HelloWorldMain {

    /**
     * Creates a Guice {@link com.google.inject.Injector Injector} configured with an inline
     * {@link AbstractModule} that binds {@link AsynchronousHelloWorld} to a provider which wraps
     * the first {@link ServiceLoader}-registered {@link HelloWorld} via
     * {@link AsynchronousHelloWorld#from(HelloWorld, java.util.concurrent.Executor)
     * from(helloWorld, executor)} using {@link ForkJoinPool#commonPool()} as the executor, obtains
     * a fully-injected {@link HelloWorldMain} instance from the injector via
     * {@link com.google.inject.Injector#getInstance(Class)}, invokes {@link #print()} on it, and
     * {@linkplain CompletableFuture#join() joins} the returned future so the asynchronous append +
     * read + print chain completes before this method returns.
     */
    static void main() {
        Guice.createInjector(new HelloWorldModule())
                .getInstance(HelloWorldMain.class)
                .print()
                .join();
    }

    /**
     * Creates a new instance with the given {@link AsynchronousHelloWorld} service. Invoked by
     * Guice through constructor injection when
     * {@link com.google.inject.Injector#getInstance(Class)} is called for this class.
     *
     * @param service the {@link AsynchronousHelloWorld} service to print with; must not be
     *                {@code null}.
     */
    @Inject
    private HelloWorldMain(final HelloWorld service) {
        super();
        this.service = AsynchronousHelloWorld.from(
                Objects.requireNonNull(service, "service is null"),
                ForkJoinPool.commonPool()
        );
    }

    /**
     * Creates a temp file,
     * {@linkplain AsynchronousHelloWorld#append(java.nio.file.Path, Object) asynchronously appends}
     * the {@value HelloWorld#BYTES} bytes of {@code hello, world} to it via the injected
     * {@link AsynchronousHelloWorld}, then reads the file via
     * {@link Files#readString(java.nio.file.Path, java.nio.charset.Charset)} and prints it to
     * {@link System#out}.
     *
     * @return a {@link CompletableFuture} that completes after the append finishes and the line has
     * been printed; the caller must {@linkplain CompletableFuture#join() join} (or otherwise wait
     * on) it if the JVM might exit before {@link ForkJoinPool#commonPool()} runs the scheduled
     * task.
     */
    CompletableFuture<Void> print() {
        return CompletableFuture
                .supplyAsync(() -> {
                    try {
                        final var path = Files.createTempFile(null, null);
                        path.toFile().deleteOnExit();
                        return path;
                    } catch (final IOException ioe) {
                        throw new UncheckedIOException(ioe);
                    }
                })
                .thenCompose(p -> service.append(p, p))
                .thenAccept(p -> {
                    try {
                        IO.println(Files.readString(p, StandardCharsets.US_ASCII));
                    } catch (final IOException ioe) {
                        throw new UncheckedIOException(ioe);
                    }
                });
    }

    /**
     * The {@link AsynchronousHelloWorld} service to print with; injected by Guice through the
     * constructor.
     */
    private final AsynchronousHelloWorld service;
}
