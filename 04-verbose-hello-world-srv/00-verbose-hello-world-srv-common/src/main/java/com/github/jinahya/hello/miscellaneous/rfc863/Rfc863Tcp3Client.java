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

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
class Rfc863Tcp3Client {

    private static final InetAddress HOST = Rfc863Tcp3Server.HOST;

    private static final int PORT = Rfc863Tcp3Server.PORT;

    public static void main(String... args)
            throws IOException, InterruptedException, ExecutionException, TimeoutException {
        try (var client = AsynchronousSocketChannel.open()) {
            var bind = true;
            if (bind) {
                client.bind(new InetSocketAddress(HOST, 0));
                log.debug("[C] bound to {}", client.getLocalAddress());
            }
            client.connect(new InetSocketAddress(HOST, PORT)).get(8L, TimeUnit.SECONDS);
            log.debug("[C] connected to {}, through {}", client.getRemoteAddress(),
                      client.getLocalAddress());
            var buffer = ByteBuffer.allocate(8);
            while (buffer.hasRemaining()) {
                var written = client.write(buffer).get(8L, TimeUnit.SECONDS);
                log.debug("[C] written: {}", written);
            }
            client.shutdownOutput();
            log.debug("[C] closing...");
        }
    }

    private Rfc863Tcp3Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
