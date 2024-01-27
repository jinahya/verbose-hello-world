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

import com.github.jinahya.hello.util.JavaLangUtils;
import com.github.jinahya.hello.util._ExcludeFromCoverage_PrivateConstructor_Obviously;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.StandardSocketOptions;

@Slf4j
class CalcUdp1Server extends CalcUdp {

    public static void main(final String... args) throws IOException {
        try (var server = new DatagramSocket(null);
             var executor = newExecutorForServer("udp-1-server-")) {
            server.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.TRUE);
            server.setOption(StandardSocketOptions.SO_REUSEPORT, Boolean.TRUE);
            // -------------------------------------------------------------------------------- bind
            server.bind(ADDR);
            logBound(server);
            // ------------------------------------------------------------- read-quit!/close-server
            JavaLangUtils.readLinesAndCloseWhenTests(
                    "quit!"::equalsIgnoreCase,
                    server
            );
            // ----------------------------------------------------------------- read/calculate/send
            while (!server.isClosed()) {
                try {
                    new _Message.OfArray()
                            .receiveFromClient(server)
                            .calculateResult(executor, m -> {
                                try {
                                    m.sendToClient(server);
                                } catch (final IOException ioe) {
                                    log.error("failed to send to client", ioe);
                                }
                            });
                } catch (final IOException ioe) {
                    if (!server.isClosed()) {
                        log.error("failed to receive", ioe);
                    }
                }
            }
        }
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private CalcUdp1Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
