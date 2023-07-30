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

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.concurrent.TimeUnit;

// https://datatracker.ietf.org/doc/html/rfc863
@Slf4j
public class Rfc863Tcp1Server {

    static final InetAddress HOST = InetAddress.getLoopbackAddress();

    static final int PORT = 9 + 51000;

    static final int LENGTH = 1024;

    static final String ALGORITHM = "SHA-1";

    public static void main(String... args) throws Exception {
        try (var server = new ServerSocket()) {
            server.setReuseAddress(true);
            server.bind(new InetSocketAddress(HOST, PORT));
            log.debug("[S] bound to {}", server.getLocalSocketAddress());
            server.setSoTimeout((int) TimeUnit.SECONDS.toMillis(8L));
            try (var client = server.accept()) {
                log.debug("[S] accepted from {}, through {}",
                          client.getRemoteSocketAddress(), client.getLocalSocketAddress());
                var digest = MessageDigest.getInstance(ALGORITHM);
                var bytes = 0L;
                for (var buffer = new byte[LENGTH]; true; ) {
                    var read = client.getInputStream().read(buffer);
                    log.trace("[C] - read: {}", read);
                    if (read == -1) {
                        break;
                    }
                    bytes += read;
                    digest.update(buffer, 0, read);
                }
                log.debug("[S] {} byte(s) received (and discarded)", bytes);
                log.debug("[S] digest: {}", HexFormat.of().formatHex(digest.digest()));
            }
        }
    }

    private Rfc863Tcp1Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
