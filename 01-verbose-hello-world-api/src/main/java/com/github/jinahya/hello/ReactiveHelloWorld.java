package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-api
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.Flow;

/**
 * An interface for generating <a href="#hello-world-bytes">hello-world-bytes</a> to various
 * targets.
 * <p>
 * All methods defined in this interface are thead-safe.
 *
 * <h2 id="hello-world-bytes">hello-world-bytes</h2>
 * A sequence of {@value #BYTES} bytes, representing the "{@code hello, world}" string encoded in
 * {@link java.nio.charset.StandardCharsets#US_ASCII US_ASCII} character set, which consists of
 * {@code 0x68("h")} followed by {@code 0x65("e")}, {@code 0x6C("l")}, {@code 0x6C("l")},
 * {@code 0x6F("o")}, {@code 0x2C(",")}, {@code 0x20(" ")}, {@code 0x77("w")}, {@code 0x6F("o")},
 * {@code 0x72("r")}, {@code 0x6C("l")}, and {@code 0x64("d")}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@FunctionalInterface
public interface ReactiveHelloWorld extends HelloWorld {

    /**
     * Returns a logger for this interface.
     *
     * @return a logger for this interface.
     */
    private Logger log() {
        return LoggerFactory.getLogger(getClass());
    }

    /**
     * Returns a logger for this interface.
     *
     * @return a logger for this interface.
     */
    private System.Logger logger() {
        return System.getLogger(getClass().getName());
    }

    default void subscribe(Flow.Subscriber<String> subscriber) {
        Objects.requireNonNull(subscriber, "subscriber is null");
        var subscription = new Flow.Subscription() {
            @Override
            public synchronized void request(long n) {
                if (canceled) {
                    log().warn("already canceled");
                    return;
                }
                if (n <= 0L) {
                    subscriber.onError(
                            new IllegalArgumentException("n(" + n + ") is not positive"));
                }
                if (thread != null) {
                    log().debug("joining the read...");
                    try {
                        thread.join();
                        log().debug("joined to the thread");
                    } catch (InterruptedException ie) {
                        log().error("interrupted while joining the thread", ie);
                        Thread.currentThread().interrupt();
                    }
                }
                thread = new Thread(() -> {
                    var array = new byte[BYTES];
                    array = set(array);
                    var string = new String(array, StandardCharsets.US_ASCII);
                    for (int i = 0; i < n && !canceled; i++) {
                        subscriber.onNext(string);
                    }
                    if (!canceled) {
                        subscriber.onComplete();
                    }
                });
                thread.start();
            }

            @Override
            public synchronized void cancel() {
                canceled = true;
            }

            private volatile boolean canceled = false;

            private Thread thread = null;
        };
        subscriber.onSubscribe(subscription);
    }
}
