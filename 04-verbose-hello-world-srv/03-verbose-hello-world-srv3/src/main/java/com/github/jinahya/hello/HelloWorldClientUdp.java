package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-04-srv2
 * %%
 * Copyright (C) 2018 - 2021 Jinahya, Inc.
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

import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
public class HelloWorldClientUdp {

    static void clients(final int count, final SocketAddress endpoint,
                        final Consumer<? super String> consumer) {
        final CountDownLatch latch = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
        }
        try {
            if (!latch.await(1L, TimeUnit.MINUTES)) {
                log.warn("latch is still not broken!");
            }
        } catch (final InterruptedException ie) {
            log.error("interrupted while awaiting latch", ie);
        }
    }

    private HelloWorldClientUdp() {
        throw new AssertionError("instantiation is not allowed");
    }
}
