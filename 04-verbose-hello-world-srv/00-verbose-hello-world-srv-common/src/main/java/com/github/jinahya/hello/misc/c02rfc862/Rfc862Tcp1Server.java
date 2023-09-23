package com.github.jinahya.hello.misc.c02rfc862;

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

import com.github.jinahya.hello.misc._Rfc86_Utils;
import lombok.extern.slf4j.Slf4j;

import java.net.ServerSocket;

// https://www.rfc-editor.org/rfc/rfc862
@Slf4j
class Rfc862Tcp1Server {

    public static void main(String... args) throws Exception {
        try (var server = new ServerSocket()) {
            server.bind(_Rfc862Constants.ADDR);
            log.info("bound to {}", server.getLocalSocketAddress());
            server.setSoTimeout((int) _Rfc862Constants.ACCEPT_TIMEOUT_IN_MILLIS);
            try (var client = server.accept()) {
                log.info("accepted from {}, through {}", client.getRemoteSocketAddress(),
                         client.getLocalSocketAddress());
                client.setSoTimeout((int) _Rfc862Constants.READ_TIMEOUT_IN_MILLIS);
                final var digest = _Rfc862Utils.newDigest();
                var bytes = 0;
                final var array = _Rfc86_Utils.newArray();
                for (int r; (r = client.getInputStream().read(array)) != -1; ) {
                    assert r > 0;
                    bytes += r;
                    client.getOutputStream().write(array, 0, r);
                    client.getOutputStream().flush();
                    digest.update(array, 0, r);
                }
                _Rfc862Utils.logServerBytes(bytes);
                _Rfc862Utils.logDigest(digest);
            }
        }
    }

    private Rfc862Tcp1Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
