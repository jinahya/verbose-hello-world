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

import java.net.Socket;
import java.net.StandardSocketOptions;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Slf4j
class CalcTcp1Client extends _CalcTcp {

    public static void main(final String... args) throws Exception {
        final var factory = Thread.ofVirtual().name("client-", 0L).factory();
        try (var executor = Executors.newFixedThreadPool(50, factory)) {
            final var futures = new ArrayList<Future<Void>>();
            for (int i = 0; i < 8192; i++) {
                futures.add(executor.submit(() -> {
                    try (var client = new Socket()) {
                        client.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.TRUE);
                        // --------------------------------------------------------------------- connect
//                        client.connect(ADDR, (int) TimeUnit.SECONDS.toMillis(1L));
                        client.connect(ADDR);
                        client.setSoTimeout((int) TimeUnit.SECONDS.toMillis(1L));
                        final var message = __CalcMessage2.newRandomInstance();
                        message.write(client.getOutputStream());
                        client.getOutputStream().flush();
                        message.read(client.getInputStream());
                        log.debug("{}({}, {}) = {}",
                                  message.operator().name(),
                                  String.format("%+2d", message.operand1()),
                                  String.format("%+2d", message.operand2()),
                                  String.format("%+2d", message.result())
                        );
                    }
                    return null;
                }));
            }
            for (final var future : futures) {
                try {
                    future.get();
                } catch (final ExecutionException ee) {
                    log.error("failed to execute", ee);
                }
            }
            executor.shutdown();
            if (!executor.awaitTermination(8L, TimeUnit.SECONDS)) {
                log.error("executor not terminated!");
            }
        }
    }
}
