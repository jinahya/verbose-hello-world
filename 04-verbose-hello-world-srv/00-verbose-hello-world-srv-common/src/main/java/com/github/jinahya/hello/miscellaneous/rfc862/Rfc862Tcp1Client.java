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
import java.util.HexFormat;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
class Rfc862Tcp1Client {

    public static void main(String... args) throws IOException {
        try (var client = new Socket()) {
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc862Constants.ADDR, 0));
                log.debug("[C] bound to {}", client.getLocalSocketAddress());
            }
            client.connect(_Rfc862Constants.ENDPOINT, (int) TimeUnit.SECONDS.toMillis(8L));
            log.debug("[C] connected to {}, through {}", client.getRemoteSocketAddress(),
                      client.getLocalSocketAddress());
            client.setSoTimeout((int) TimeUnit.SECONDS.toMillis(8L));
            var digest = _Rfc862Utils.newMessageDigest();
            var bytes = ThreadLocalRandom.current().nextInt(1048576);
            log.debug("[C] sending {} byte(s)", bytes);
            var buffer = _Rfc862Utils.newByteArray();
            for (int read; bytes > 0; ) {
                ThreadLocalRandom.current().nextBytes(buffer);
                var length = Math.min(buffer.length, bytes);
                client.getOutputStream().write(buffer, 0, length);
                log.trace("[C] - written: {}", buffer.length);
                bytes -= length;
                client.getOutputStream().flush();
                read = client.getInputStream().read(buffer);
                log.trace("[C] - read: {}", read);
                assert read != -1;
                digest.update(buffer, 0, read);
            }
            client.shutdownOutput();
            for (int read; ; ) {
                read = client.getInputStream().read(buffer);
                log.trace("[C] - read: {}", read);
                if (read == -1) {
                    break;
                }
                digest.update(buffer, 0, read);
            }
            log.debug("[C] digest: {}", HexFormat.of().formatHex(digest.digest()));
        }
    }

    private Rfc862Tcp1Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
