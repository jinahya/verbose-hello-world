package com.github.jinahya.hello.misc.c03calc;

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

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
class CalcTcp1Client extends CalcTcp {

    public static void main(final String... args) throws Exception {
        try (var executor = newExecutorForClient("tcp-1-client-")) {
            final var index = new AtomicInteger();
            for (int i = 0; i < REQUEST_COUNT; i++) {
                executor.submit(() -> {
                    try (var client = new Socket()) {
                        // ----------------------------------------------------------------- connect
                        client.connect(ADDR, (int) CONNECT_TIMEOUT_MILLIS);
                        // ---------------------------------------------------------- write/read/log
                        client.setSoTimeout((int) SO_TIMEOUT_MILLIS);
                        new _Message.OfArray().randomize()
                                .writeToServer(client.getOutputStream(), true)
                                .readFromServer(client.getInputStream())
                                .log(index.getAndIncrement());
                    } catch (final IOException ioe) {
                        log.error("failed to request", ioe);
                    }
                });
            }
            // ---------------------------------------------------------------------- shutdown/await
            executor.shutdown();
            if (!executor.awaitTermination(1L, TimeUnit.MINUTES)) {
                log.error("executor not terminated");
            }
        }
    }
}
