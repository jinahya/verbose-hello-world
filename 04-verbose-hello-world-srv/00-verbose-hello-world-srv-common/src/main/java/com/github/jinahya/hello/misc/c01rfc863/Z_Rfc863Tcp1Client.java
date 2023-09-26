package com.github.jinahya.hello.misc.c01rfc863;

/*-
 * #%L
 * verbose-hello-world-srv-common
 * %%
 * Copyright (C) 2018 - 2023 Jinahya, Inc.
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

import com.github.jinahya.hello.util.Stopwatch;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Slf4j
class Z_Rfc863Tcp1Client {

    public static void main(final String... args) throws Exception {
        final Object carrier = Stopwatch.startStopwatch();
        final var futures = new ArrayList<Future<Integer>>();
        final var service = Executors.newFixedThreadPool(32);
        for (int i = 0; i < 32; i++) {
            for (final Class<?> clientClass : Z__Rfc863Constants.CLIENT_CLASSES) {
                futures.add(
                        service.submit(() -> {
                            try {
                                return Z__Rfc863Utils.fork(clientClass).waitFor();
                            } catch (final IOException ioe) {
                                throw new UncheckedIOException(ioe);
                            }
                        })
                );
            }
        }
        service.shutdown();
        if (!service.awaitTermination(10L, TimeUnit.MINUTES)) {
            log.error("service hasn't been terminated for a while");
        }
        int total = 0;
        int error = 0;
        for (final var future : futures) {
            total++;
            if (future.get() != 0) {
                error++;
            }
        }
        log.debug("total: {}", total);
        log.debug("error: {}", error);
        log.debug("elapsed: {}", Stopwatch.stopStopwatch(carrier));
    }

    private Z_Rfc863Tcp1Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
