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
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Slf4j
class Z_Rfc863Tcp1Client {

    //    private static final int CLIENTS = 8192;
    private static final int CLIENTS = 1024;

    public static void main(final String... args) throws Exception {
        final Object carrier = Stopwatch.startStopwatch();
        final var futures = new ArrayList<Future<Integer>>();
//        final var service = Executors.newFixedThreadPool(Z__Rfc863Constants.SERVER_THREADS);
        final var service = Executors.newFixedThreadPool(16);
        final var array = new byte[32768];
        for (int i = 0; i < CLIENTS; i++) {
            futures.add(
                    service.submit(() -> {
                        try (var client = new Socket()) {
                            client.setReuseAddress(true);
                            client.setSoLinger(true, 0);
                            client.connect(_Rfc863Constants.ADDR);
                            client.getOutputStream().write(array);
                            client.getOutputStream().flush();
                        } catch (final IOException ioe) {
                            log.error("failed to connect/send", ioe);
                            throw ioe;
                        }
                        return null;
                    })
            );
        }
        service.shutdown();
        if (!service.awaitTermination(10L, TimeUnit.MINUTES)) {
            log.error("service hasn't been terminated for a while");
        }
        int total = 0;
        int error = 0;
        for (final var future : futures) {
            total++;
            try {
                future.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
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
