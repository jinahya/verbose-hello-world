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

import com.github.jinahya.hello.util._ExcludeFromCoverage_PrivateConstructor_Obviously;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
class CalcUdp1Client extends CalcUdp {

    public static void main(final String... args) throws IOException {
        try (var executor = newExecutorForClient("udp-1-client-")) {
            final var sequence = new AtomicInteger();
            for (int i = 0; i < REQUEST_COUNT; i++) {
                executor.submit(() -> {
                    try (var client = new DatagramSocket(null)) {
                        // ---------------------------------------------------------- bind(optional)
                        if (ThreadLocalRandom.current().nextBoolean()) {
                            try {
                                client.bind(new InetSocketAddress(HOST, 0));
                            } catch (final IOException ioe) {
                                log.error("failed to bind", ioe);
                            }
                        }
                        // ------------------------------------------------------- connect(optional)
                        if (ThreadLocalRandom.current().nextBoolean()) {
                            try {
                                client.connect(ADDR);
                            } catch (final IOException ioe) {
                                log.error("failed to connect", ioe);
                            }
                        }
                        // -------------------------------------------------------------------- send
                        final var message = new CalcMessage.OfArray()
                                .randomize()
                                .sequence(sequence);
                        if (client.isConnected()) {
                            message.sendToServer(client);
                        } else {
                            message.sendToServer(client, ADDR);
                        }
                        // ------------------------------------------------------------- receive/log
                        client.setSoTimeout((int) SO_TIMEOUT_MILLIS);
                        message.receiveFromServer(client).log();
                        // ------------------------------------------------- disconnect-if-connected
                        if (client.isConnected()) {
                            client.disconnect();
                        }
                    } catch (final Exception e) {
                        log.error("failed to request", e);
                    }
                });
            }
        }
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private CalcUdp1Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
