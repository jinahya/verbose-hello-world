package com.github.jinahya.hello.misc.c01rfc863;

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

import java.net.ServerSocket;

@Slf4j
class Rfc863Tcp0Server {

    public static void main(String... args) throws Exception {
        try (var server = new ServerSocket()) {
            server.bind(_Rfc863Constants.ADDR, 1);
            log.info("bound to {}", server.getLocalSocketAddress());
            server.setSoTimeout((int) _Rfc863Constants.ACCEPT_TIMEOUT_IN_MILLIS);
            try (var client = server.accept()) {
                log.info("accepted from {}, through {}", client.getRemoteSocketAddress(),
                         client.getLocalSocketAddress());
                client.setSoTimeout((int) _Rfc863Constants.READ_TIMEOUT_IN_MILLIS);
                var digest = _Rfc863Utils.newDigest();
                var bytes = 0L;
                for (int b; (b = client.getInputStream().read()) != -1; bytes++) {
                    digest.update((byte) b);
                }
                _Rfc863Utils.logServerBytes(bytes);
                _Rfc863Utils.logDigest(digest);
            }
        }
    }

    private Rfc863Tcp0Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
