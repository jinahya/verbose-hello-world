package com.github.jinahya.hello.misc.c01rfc863.real;

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

import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
class Rfc863TcpBlockingClient {

    public static void main(final String... args) throws Exception {
        final var carrier = Stopwatch.startStopwatch();
        final var service = Executors.newFixedThreadPool(_Rfc863Constants.CLIENT_THREADS);
        for (int i = 0; i < _Rfc863Constants.CLIENT_COUNT; i++) {
            service.submit(() -> {
                try (var client = new Socket()) {
                    client.connect(_Rfc863Constants.ADDR);
                    final var array = new byte[_Rfc863Constants.CLIENT_BUFLEN];
                    var bytes = _Rfc863Constants.CLIENT_BYTES;
                    while (bytes > 0) {
                        ThreadLocalRandom.current().nextBytes(array);
                        final int length = Math.min(array.length, bytes);
                        client.getOutputStream().write(
                                array, // <b>
                                0,     // <off>
                                length // <len>
                        );
                        bytes -= length;
                    }
                }
                return null;
            });
        }
        service.shutdown();
        log.debug("awaiting service to be terminated...");
        if (!service.awaitTermination(10L, TimeUnit.MINUTES)) {
            log.error("service hasn't been terminated for a while");
        }
        final var duration = Stopwatch.stopStopwatch(carrier);
        log.debug("duration: {}", duration);
    }

    private Rfc863TcpBlockingClient() {
        throw new AssertionError("instantiation is not allowed");
    }
}
