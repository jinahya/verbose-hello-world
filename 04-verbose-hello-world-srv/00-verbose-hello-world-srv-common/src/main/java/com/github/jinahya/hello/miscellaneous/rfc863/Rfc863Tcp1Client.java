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
import java.net.Socket;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Rfc863Tcp1Client {

    private static final InetAddress HOST = Rfc863Tcp1Server.HOST;

    private static final int PORT = Rfc863Tcp1Server.PORT;

    static final int LENGTH = Rfc863Tcp1Server.LENGTH << 1;

    private static final String ALGORITHM = Rfc863Tcp1Server.ALGORITHM;

    public static void main(String... args) throws Exception {
        try (var client = new Socket()) {
            client.setReuseAddress(true);
            var bind = true;
            if (bind) {
                client.bind(new InetSocketAddress(HOST, 0));
                log.debug("[C] bound to {}", client.getLocalSocketAddress());
            }
            client.connect(new InetSocketAddress(HOST, PORT), (int) TimeUnit.SECONDS.toMillis(8L));
            log.debug("[C] connected to {}, through {}", client.getRemoteSocketAddress(),
                      client.getLocalSocketAddress());
            var bytes = ThreadLocalRandom.current().nextInt(1048576);
            log.debug("[C] sending {} byte(s)...", bytes);
            var digest = MessageDigest.getInstance(ALGORITHM);
            for (var buffer = new byte[LENGTH]; bytes > 0; ) {
                ThreadLocalRandom.current().nextBytes(buffer);
                var length = Math.min(buffer.length, bytes);
                client.getOutputStream().write(buffer, 0, length);
                log.trace("[C] - written: {}", length);
                bytes -= length;
                digest.update(buffer, 0, length);
            }
            client.getOutputStream().flush();
            client.shutdownOutput();
            log.debug("[S] digest: {}", HexFormat.of().formatHex(digest.digest()));
        }
    }

    private Rfc863Tcp1Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
