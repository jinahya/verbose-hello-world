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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Base64;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
class Rfc862Tcp0Client {

    public static void main(String... args) throws IOException {
        try (var client = new Socket()) {
            client.setReuseAddress(true);
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc862Constants.ADDR, 0));
                log.debug("[C] bound to {}", client.getLocalSocketAddress());
            }
            client.connect(_Rfc862Constants.ENDPOINT, (int) TimeUnit.SECONDS.toMillis(8L));
            log.debug("[C] connected to {}, through {}", client.getRemoteSocketAddress(),
                      client.getLocalSocketAddress());
            client.setSoTimeout((int) TimeUnit.SECONDS.toMillis(8L));
            var digest = _Rfc862Utils.newMessageDigest();
            var bytes = ThreadLocalRandom.current().nextInt(1024);
            log.debug("[C] sending {} byte(s)", bytes);
            for (int b; bytes > 0; ) {
                b = ThreadLocalRandom.current().nextInt(256);
                client.getOutputStream().write(b);
                client.getOutputStream().flush();
                bytes--;
                b = client.getInputStream().read();
                digest.update((byte) b);
            }
            client.shutdownOutput();
            for (int b; ; ) {
                b = client.getInputStream().read();
                if (b == -1) {
                    client.shutdownInput();
                    break;
                }
                digest.update((byte) b);
            }
            log.debug("[C] digest: {}", Base64.getEncoder().encodeToString(digest.digest()));
        }
    }

    private Rfc862Tcp0Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
