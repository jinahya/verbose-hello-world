package com.github.jinahya.hello.miscellaneous.m1_rfc862;

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
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class Rfc862TcpClient1 {

    private static final boolean BIND = false;

    public static void connectWriteAndRead(SocketAddress endpoint) throws IOException {
        try (var client = new Socket()) {
            if (BIND) {
                client.bind(new InetSocketAddress(InetAddress.getLocalHost(), 0));
                log.debug("[C] client bound to {}", client.getLocalSocketAddress());
            }
            client.connect(endpoint);
            log.debug("[C] connected to {}, through {}", client.getRemoteSocketAddress(),
                      client.getLocalSocketAddress());
            var bytes = ThreadLocalRandom.current().nextInt(1, 9);
            for (int j = 0; j < bytes; j++) {
                var written = ThreadLocalRandom.current().nextInt(256);
                client.getOutputStream().write(written);
                client.getOutputStream().flush();
                var read = client.getInputStream().read();
                assert read != -1;
                assert read == written;
            }
            log.debug("[C] {} byte(s) written/read", bytes);
        }
    }

    public static void main(String... args) throws IOException {
        var host = InetAddress.getLoopbackAddress();
        var endpoint = new InetSocketAddress(host, Rfc862TcpServer1.PORT);
        connectWriteAndRead(endpoint);
    }

    private Rfc862TcpClient1() {
        throw new AssertionError("instantiation is not allowed");
    }
}
