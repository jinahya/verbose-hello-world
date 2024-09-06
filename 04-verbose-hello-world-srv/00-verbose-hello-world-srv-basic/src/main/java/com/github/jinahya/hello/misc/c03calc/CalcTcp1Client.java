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

import com.github.jinahya.hello.util.JavaIoFlushableUtils;
import com.github.jinahya.hello.util.JavaUtilConcurrentCallableUtils;
import com.github.jinahya.hello.util._ExcludeFromCoverage_PrivateConstructor_Obviously;
import lombok.extern.slf4j.Slf4j;

import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
class CalcTcp1Client extends CalcTcp {

    public static void main(final String... args) {
        try (var executor = newExecutorForClient("tcp-1-client-")) {
            // --------------------------------------------------------------------- submit-requests
            final var sequence = new AtomicInteger();
            for (int i = 0; i < REQUEST_COUNT; i++) {
                executor.submit(() -> {
                    try (var client = new Socket()) {
                        // ----------------------------------------------------------------- connect
                        client.connect(ADDR, (int) CONNECT_TIMEOUT_MILLIS);
                        // ---------------------------------------------------------- write/read/log
                        new CalcMessage.OfArray()
                                .randomize()
                                .sequence(sequence.getAndIncrement())
                                .writeToServerAndAccept(client, c -> s -> {
                                    JavaIoFlushableUtils.flushUnchecked(s);
                                    JavaUtilConcurrentCallableUtils.callUnchecked(() -> {
                                        c.setSoTimeout((int) SO_TIMEOUT_MILLIS);
                                        return null;
                                    });
                                })
                                .readFromServer(client.getInputStream())
                                .log();
                    } catch (final Exception e) {
                        log.error("failed to request", e);
                    }
                });
            }
        }
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private CalcTcp1Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
