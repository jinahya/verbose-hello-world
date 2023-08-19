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

import java.net.ServerSocket;
import java.util.concurrent.TimeUnit;

// https://www.rfc-editor.org/rfc/rfc862
@Slf4j
class Rfc862Tcp1Server {

    public static void main(String... args) throws Exception {
        try (var server = new ServerSocket()) {
            server.bind(_Rfc862Constants.ADDRESS);
            log.debug("bound to {}", server.getLocalSocketAddress());
            server.setSoTimeout((int) TimeUnit.SECONDS.toMillis(16L));
            try (var client = server.accept()) {
                log.debug("accepted from {}, through {}", client.getRemoteSocketAddress(),
                          client.getLocalSocketAddress());
                client.setSoTimeout((int) TimeUnit.SECONDS.toMillis(16L));
                var digest = _Rfc862Utils.newDigest();
                var bytes = 0L;
                var array = _Rfc862Utils.newArray();
                log.debug("array.length: {}", array.length);
                for (int r; (r = client.getInputStream().read(array)) != -1; ) {
                    assert r > 0;
                    bytes += r;
                    client.getOutputStream().write(array, 0, r);
                    client.getOutputStream().flush();
                    digest.update(array, 0, r);
                }
                client.shutdownOutput();
                _Rfc862Utils.logServerBytesSent(bytes);
                _Rfc862Utils.logDigest(digest);
            }
        }
    }

    private Rfc862Tcp1Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
