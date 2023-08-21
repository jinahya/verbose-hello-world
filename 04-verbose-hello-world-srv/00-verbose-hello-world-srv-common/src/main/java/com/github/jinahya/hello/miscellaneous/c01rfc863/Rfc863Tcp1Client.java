package com.github.jinahya.hello.miscellaneous.c01rfc863;

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

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;

import static com.github.jinahya.hello.miscellaneous.c01rfc863._Rfc863Constants.ADDR;

@Slf4j
class Rfc863Tcp1Client {

    public static void main(String... args) throws Exception {
        try (var client = new Socket()) {
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(ADDR, 0));
                log.info("(optionally) bound to {}", client.getLocalSocketAddress());
            }
            client.connect(_Rfc863Constants.ADDRESS); // IOException
            log.info("connected to {}, through {}", client.getRemoteSocketAddress(),
                     client.getLocalSocketAddress());
            var digest = _Rfc863Utils.newDigest();
            var bytes = ThreadLocalRandom.current().nextInt(1048576);
            _Rfc863Utils.logClientBytes(bytes);
            var array = _Rfc863Utils.newArray();
            while (bytes > 0) {
                ThreadLocalRandom.current().nextBytes(array);
                var l = Math.min(array.length, bytes);
                client.getOutputStream().write(array, 0, l);
                bytes -= l;
                digest.update(array, 0, l);
            }
            client.getOutputStream().flush();
            _Rfc863Utils.logDigest(digest);
        }
    }

    private Rfc863Tcp1Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
