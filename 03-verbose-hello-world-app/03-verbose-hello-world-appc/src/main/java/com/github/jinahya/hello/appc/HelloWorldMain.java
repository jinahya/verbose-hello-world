package com.github.jinahya.hello.appc;

/*-
 * #%L
 * verbose-hello-world-appc
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

import java.util.*;
import java.util.concurrent.*;

/**
 * A program whose {@link #main()} method obtains an {@link AsynchronousHelloWorld} through
 * <a href="https://spring.io/projects/spring-framework">Spring</a> dependency injection,
 * asynchronously produces the {@code hello, world} string on {@link ForkJoinPool#commonPool()}, and
 * prints it to {@link System#out}. The returned future is joined so the program does not exit
 * before the chain completes.
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
     * stage (converted via {@link CompletionStage#toCompletableFuture()}) so the asynchronous print
     * chain completes before this method returns.
     */
    static void main() {
        try (var context = new AnnotationConfigApplicationContext(HelloWorldConfiguration.class)) {
            context.getBean(HelloWorldMain.class)
                    .print()
                    .toCompletableFuture()
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
    HelloWorldMain(final AsynchronousHelloWorld<HelloWorld> service) {
        super();
        this.service = Objects.requireNonNull(service, "service is null");
    }

    /**
     * Asynchronously produces the {@code hello, world} {@link String} via the injected
     * {@link AsynchronousHelloWorld} — by
     * {@linkplain AsynchronousHelloWorld#applyAsync(java.util.function.Function) dispatching}
     * {@link HelloWorldUtils#string(HelloWorld)} on the service's executor — and prints it to
     * {@link System#out}.
     *
     * @return a {@link CompletionStage} that completes after the line has been printed; the caller
     * must wait on it (typically via
     * {@linkplain CompletionStage#toCompletableFuture() toCompletableFuture()} {@code .join()}) if
     * the JVM might exit before {@link ForkJoinPool#commonPool()} runs the scheduled task.
     */
    CompletionStage<Void> print() {
        return service.applyAsync(HelloWorldUtils::string).thenAccept(IO::println);
    }

    /**
     * The {@link AsynchronousHelloWorld} service to print with; injected by Spring through the
     * constructor.
     */
    private final AsynchronousHelloWorld<HelloWorld> service;
}
