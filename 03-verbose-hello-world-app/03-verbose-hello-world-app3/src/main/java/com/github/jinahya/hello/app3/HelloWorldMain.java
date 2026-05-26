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

import com.github.jinahya.hello.api.*;
import org.springframework.context.annotation.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * A program whose {@link #main()} method obtains an {@link AsynchronousHelloWorld} through
 * <a href="https://spring.io/projects/spring-framework">Spring</a> dependency injection,
 * asynchronously appends {@code hello, world} to a temp file on {@link ForkJoinPool#commonPool()},
 * reads the file back, and prints it to {@link System#out}. The returned future is joined so the
 * program does not exit before the chain completes.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see AnnotationConfigApplicationContext
 */
@SuppressWarnings({
        "java:S106" // Standard outputs should not be used directly to log anything
})
class HelloWorldMain {

    /**
     * Bootstraps a Spring {@link AnnotationConfigApplicationContext} from
     * {@link HelloWorldConfiguration}, looks up the container-managed {@link HelloWorldMain} bean,
     * invokes {@link #print()} on it, and {@linkplain CompletableFuture#join() joins} the returned
     * future so the asynchronous append + read + print chain completes before this method returns.
     */
    static void main() {
        try (var context = new AnnotationConfigApplicationContext(HelloWorldConfiguration.class)) {
            context.getBean(HelloWorldMain.class)
                    .print()
                    .join();
        }
    }

    /**
     * Creates a new instance with the given {@link AsynchronousHelloWorld} service. Invoked by the
     * Spring container through the
     * {@link HelloWorldConfiguration#helloWorldMain(AsynchronousHelloWorld) helloWorldMain} factory
     * method.
     *
     * @param service the {@link AsynchronousHelloWorld} service to print with; must not be
     *                {@code null}.
     */
    HelloWorldMain(final AsynchronousHelloWorld service) {
        super();
        this.service = Objects.requireNonNull(service, "service is null");
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
                .thenApply(p -> {
                    try {
                        IO.println(Files.readString(p, StandardCharsets.US_ASCII));
                    } catch (final IOException ioe) {
                        throw new UncheckedIOException(ioe);
                    }
                    return p;
                })
                .thenAccept(p -> {
                    try {
                        Files.deleteIfExists(p);
                    } catch (final IOException ioe) {
                        throw new UncheckedIOException(ioe);
                    }
                });
    }

    /**
     * The {@link AsynchronousHelloWorld} service to print with; injected by Spring through the
     * constructor.
     */
    private final AsynchronousHelloWorld service;
}
