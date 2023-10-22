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

import com.github.jinahya.hello.misc.c00rfc86_._Rfc86_Constants;
import com.github.jinahya.hello.misc.c00rfc86_._Rfc86_Utils;
import com.github.jinahya.hello.util.LoggingUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ThreadLocalRandom;

import static com.github.jinahya.hello.misc.c01rfc863._Rfc863Constants.HOST;

@Slf4j
class Rfc863Tcp4Client {

    public static void main(final String... args) throws Exception {
        if (args.length > 0) {
            LoggingUtils.setLevelForAllLoggers(args[0]);
        }
        try (var client = AsynchronousSocketChannel.open()) {
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(HOST, 0));
                log.info("(optionally) bound to {}", client.getLocalAddress());
            }
            client.connect(_Rfc863Constants.ADDR).get(_Rfc86_Constants.CONNECT_TIMEOUT,
                                                      _Rfc86_Constants.CONNECT_TIMEOUT_UNIT);
            _Rfc86_Utils.logConnected(client);
            try (var attachment = new Rfc863Tcp4ClientAttachment(client)) {
                while (attachment.write() > 0) {
                    // does nothing
                }
            }
        }
    }

    private Rfc863Tcp4Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
