package com.github.jinahya.hello.miscellaneous.c02rfc862;

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

import java.io.EOFException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
class Rfc862Tcp1Client {

    public static void main(String... args) throws Exception {
        try (var client = new Socket()) {
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc862Constants.ADDR, 0));
                log.debug("bound to {}", client.getLocalSocketAddress());
            }
            client.connect(_Rfc862Constants.ADDRESS, (int) TimeUnit.SECONDS.toMillis(8L));
            log.debug("connected to {}, through {}", client.getRemoteSocketAddress(),
                      client.getLocalSocketAddress());
            client.setSoTimeout((int) TimeUnit.SECONDS.toMillis(8L));
            var digest = _Rfc862Utils.newDigest();
            var bytes = ThreadLocalRandom.current().nextInt(1048576);
            _Rfc862Utils.logClientBytesSending(bytes);
            var array = _Rfc862Utils.newArray();
            log.debug("array.length: {}", array.length);
            for (int r; bytes > 0; ) {
                ThreadLocalRandom.current().nextBytes(array);
                var l = Math.min(array.length, bytes);
                client.getOutputStream().write(array, 0, l);
                client.getOutputStream().flush();
                bytes -= l;
                digest.update(array, 0, l);
                r = client.getInputStream().read(array);
                if (r == -1) {
                    throw new EOFException("unexpected eof");
                }
                assert r > 0;
            }
            _Rfc862Utils.logDigest(digest);
            client.shutdownOutput();
            for (int r; (r = client.getInputStream().read(array)) != -1; ) {
                assert r > 0;
            }
        }
    }

    private Rfc862Tcp1Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
