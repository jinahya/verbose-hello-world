package com.github.jinahya.hello.misc.c02rfc862;

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

import com.github.jinahya.hello.misc._Rfc86_Constants;
import com.github.jinahya.hello.misc._Rfc86_Utils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc862Tcp4Client {

    public static void main(final String... args) throws Exception {
        try (var client = AsynchronousSocketChannel.open()) {
            // --------------------------------------------------------------------- BIND/optionally
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc86_Constants.HOST, 0));
                log.info("(optionally) bound to {}", client.getLocalAddress());
            }
            // ----------------------------------------------------------------------------- CONNECT
            client.connect(_Rfc862Constants.ADDR).get(_Rfc86_Constants.CONNECT_TIMEOUT,
                                                      _Rfc86_Constants.CONNECT_TIMEOUT_UNIT);
            _Rfc86_Utils.logConnected(client);
            // ------------------------------------------------------------------------ SEND/RECEIVE
            try (var attachment = new Rfc862Tcp4ClientAttachment(client)) {
                int r;
                while (attachment.write() > 0) {
                    r = attachment.read();
                    assert r >= 0;
                }
                client.shutdownOutput();
                while ((r = attachment.read()) != -1) {
                    assert r >= 0;
                }
            }
        }
    }

    private Rfc862Tcp4Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}