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

import java.util.Objects;
import java.util.concurrent.Flow;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;

/**
 * An interface for generating reactive streams of <a
 * href="HelloWorld#hello-world-bytes">hello-world-bytes</a>.
 * <p>
 * All methods defined in this interface are thead-safe.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@FunctionalInterface
public interface ReactiveHelloWorld extends AsynchronousHelloWorld {

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

    default <T> void subscribe(Flow.Subscriber<? super T> subscriber,
                               Function<? super ReactiveHelloWorld, ? extends T> generator) {
        Objects.requireNonNull(subscriber, "subscriber is null");
        Objects.requireNonNull(generator, "generator is null");
        var subscription = new ReactiveHelloWorldSubscription<T>(
                subscriber,
                () -> generator.apply(this),
                new LinkedBlockingQueue<>()
        );
        subscriber.onSubscribe(subscription);
    }
}
