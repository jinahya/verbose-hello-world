package com.github.jinahya.hello.miscellaneous.rfc862;

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
import java.net.ServerSocket;
import java.util.concurrent.TimeUnit;

// https://www.rfc-editor.org/rfc/rfc862
@Slf4j
public class Rfc862Tcp1Server {

    static final InetAddress HOST = InetAddress.getLoopbackAddress();

    static final int PORT = 7 + 51000;

    public static void main(String... args) throws IOException {
        try (var server = new ServerSocket()) {
            server.bind(new InetSocketAddress(HOST, PORT));
            log.info("[S] bound to {}", server.getLocalSocketAddress());
            server.setSoTimeout((int) TimeUnit.SECONDS.toMillis(8L));
            try (var client = server.accept()) {
                log.debug("[S] accepted from {}, through {}", client.getRemoteSocketAddress(),
                          client.getLocalSocketAddress());
                var bytes = 0L;
                for (; true; bytes++) {
                    var b = client.getInputStream().read();
                    if (b == -1) {
                        client.shutdownInput();
                        break;
                    }
                    client.getOutputStream().write(b);
                    client.getOutputStream().flush();
                }
                log.debug("[S] {} byte(s) read/written", bytes);
                log.debug("[S] closing client...");
            }
            log.debug("[S] closing server...");
        }
    }

    private Rfc862Tcp1Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
