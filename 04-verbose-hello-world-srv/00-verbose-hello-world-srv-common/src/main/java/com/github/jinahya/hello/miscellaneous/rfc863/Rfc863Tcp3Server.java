package com.github.jinahya.hello.miscellaneous.rfc863;

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

import com.github.jinahya.hello.HelloWorldServerUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.concurrent.TimeUnit;

@Slf4j
class Rfc863Tcp3Server {

    static final InetAddress HOST = Rfc863Tcp2Server.HOST;

    static final int PORT = Rfc863Tcp2Server.PORT;

    static final int CAPACITY = 4096;

    static final String ALGORITHM = "SHA-1";

    public static void main(String... args) throws Exception {
        try (var server = AsynchronousServerSocketChannel.open()) {
            server.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.TRUE);
            server.setOption(StandardSocketOptions.SO_REUSEPORT, Boolean.TRUE);
            server.bind(new InetSocketAddress(HOST, PORT));
            log.debug("[S] bound to {}", server.getLocalAddress());
            try (var client = server.accept().get(8L, TimeUnit.SECONDS)) {
                log.debug("[S] accepted from {}, through {}", client.getRemoteAddress(),
                          client.getLocalAddress());
                var digest = MessageDigest.getInstance(ALGORITHM);
                int bytes = 0;
                var buffer = ByteBuffer.allocate(CAPACITY);
                while (true) {
                    if (!buffer.hasRemaining()) {
                        buffer.clear();
                    }
                    var read = client.read(buffer).get();
                    log.trace("[S] - read: {}", read);
                    if (read == -1) {
                        break;
                    }
                    bytes += read;
                    HelloWorldServerUtils.updatePreceding(digest, buffer, read);
                }
                log.debug("[S] byte(s) received (and discarded): {}", bytes);
                log.debug("[S] digest: {}", HexFormat.of().formatHex(digest.digest()));
            }
        }
    }

    private Rfc863Tcp3Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
