package com.github.jinahya.hello.app4_;

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

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldArrayPublisher;
import jakarta.enterprise.inject.se.SeContainerInitializer;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A program whose {@link #main()} method obtains a {@link HelloWorld} through
 * <a href="https://jakarta.ee/specifications/cdi/">Jakarta CDI</a> and prints
 * {@code hello, world} to {@link System#out}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see SeContainerInitializer#newInstance()
 */
@SuppressWarnings({
        "java:S106",  // Standard outputs should not be used directly to log anything
        "java:S6813"  // Field dependency injection should be avoided
})
class HelloWorldMain implements Flow.Subscriber<byte[]> {

    static {
        Logger.getLogger("org.jboss.weld").setLevel(Level.WARNING);
    }

    /**
     * Bootstraps a CDI SE container via {@link SeContainerInitializer#initialize()}, selects an
     * instance of this class (which is itself the {@link Flow.Subscriber}) from the container,
     * invokes {@link #print()} on it, and {@linkplain CompletableFuture#join() joins} the
     * returned future so the JVM does not exit before the publisher has emitted (or terminated).
     */
    static void main() {
        try (var _ = SeContainerInitializer.newInstance().initialize()) {
            CDI.current().select(HelloWorldMain.class).get()
                    .print()
                    .join();
        }
    }

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Suppresses external instantiation; the CDI container constructs instances of this class
     * reflectively through this private constructor.
     */
    @Inject
    private HelloWorldMain(final HelloWorld service) {
        super();
        this.service = Objects.requireNonNull(service, "service is null");
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Subscribes {@code this} to a {@link HelloWorldArrayPublisher} backed by the injected
     * {@link HelloWorld} and returns a {@link CompletableFuture} that the subscriber's terminal
     * callbacks complete: {@link #onNext(byte[])} completes it after the first emitted byte
     * array has been printed, while {@link #onComplete()} / {@link #onError(Throwable)} complete
     * it on upstream termination.
     *
     * @return a {@link CompletableFuture} the caller should
     * {@linkplain CompletableFuture#join() join} so the JVM does not exit before the publisher
     * emits.
     */
    CompletableFuture<Void> print() {
        new HelloWorldArrayPublisher(service).subscribe(this);
        return future;
    }

    // ----------------------------------------------------------------------- Flow.Subscriber<byte>

    @Override
    public void onSubscribe(final Flow.Subscription subscription) {
        this.subscription = subscription;
        this.subscription.request(1);
    }

    @Override
    public void onNext(final byte[] item) {
        IO.println(new String(item, StandardCharsets.US_ASCII));
        subscription.cancel();
        future.complete(null);
    }

    @Override
    public void onError(final Throwable throwable) {
        future.completeExceptionally(throwable);
    }

    @Override
    public void onComplete() {
        future.complete(null);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * The {@link HelloWorld} service, injected by CDI.
     */
    private final HelloWorld service;

    private Flow.Subscription subscription;

    private final CompletableFuture<Void> future = new CompletableFuture<>();
}
