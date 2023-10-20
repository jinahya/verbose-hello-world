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

import com.github.jinahya.hello.misc._UdpUtils;
import com.github.jinahya.hello.util.HelloWorldServerUtils;
import com.github.jinahya.hello.util.JavaLangUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramSocket;

@Slf4j
class CalcUdp1Server {

    public static void main(final String... args) throws IOException {
        try (var server = new DatagramSocket(null)) {
            // -------------------------------------------------------------------------------- bind
            server.bind(_CalcConstants.ADDR);
            _UdpUtils.logBound(server);
            // ------------------------------------------------------- close-server-when-reads-quit!
            JavaLangUtils.readLinesAndCloseWhenTests(
                    HelloWorldServerUtils::isQuit,
                    server
            );
            // ------------------------------------------------------------------------ receive/send
            while (!server.isClosed()) {
                try {
                    _CalcMessage.newInstanceForServers()
                            .receiveRequest(server)
                            .apply()
                            .sendResult(server);
                } catch (final IOException ioe) {
                    if (!server.isClosed()) {
                        log.error("failed to receive/send", ioe);
                    }
                }
            }
        }
    }

    private CalcUdp1Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
