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

import com.github.jinahya.hello.util.HelloWorldNetUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;

import static com.github.jinahya.hello.miscellaneous.c01rfc863._Rfc863Constants.HOST;

@Slf4j
class Rfc863Tcp0Client {

    public static void main(String... args) throws Exception {
        try (var client = new Socket()) {
            HelloWorldNetUtils.printSocketOptions(client);
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(HOST, 0));
                log.info("(optionally) bound to {}", client.getLocalSocketAddress());
            }
            client.connect(_Rfc863Constants.ADDR, (int) _Rfc863Constants.CONNECT_TIMEOUT_IN_MILLIS);
            log.info("connected to {}, through {}", client.getRemoteSocketAddress(),
                     client.getLocalSocketAddress());
            var digest = _Rfc863Utils.newDigest();
            var bytes = _Rfc863Utils.newBytes(1024);
            _Rfc863Utils.logClientBytes(bytes);
            for (int b; bytes-- > 0; ) {
                b = ThreadLocalRandom.current().nextInt(255);
                client.getOutputStream().write(b);
                digest.update((byte) b);
            }
            client.getOutputStream().flush();
            _Rfc863Utils.logDigest(digest);
        }
    }

    private Rfc863Tcp0Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
