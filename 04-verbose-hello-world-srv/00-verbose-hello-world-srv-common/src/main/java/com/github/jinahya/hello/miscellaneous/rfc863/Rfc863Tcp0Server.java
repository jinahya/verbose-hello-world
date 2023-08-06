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

import java.net.ServerSocket;
import java.util.HexFormat;
import java.util.concurrent.TimeUnit;

// https://datatracker.ietf.org/doc/html/rfc863
@Slf4j
public class Rfc863Tcp0Server {

    public static void main(String... args) throws Exception {
        try (var server = new ServerSocket()) {
            server.setReuseAddress(true);
            server.bind(_Rfc863Constants.ENDPOINT);
            log.debug("[S] bound to {}", server.getLocalSocketAddress());
            server.setSoTimeout((int) TimeUnit.SECONDS.toMillis(8L));
            try (var client = server.accept()) {
                log.debug("[S] accepted from {}, through {}",
                          client.getRemoteSocketAddress(), client.getLocalSocketAddress());
                var digest = _Rfc863Utils.newMessageDigest();
                var bytes = 0L;
                while (true) {
                    var read = client.getInputStream().read();
                    log.trace("[C] - read: {}", read);
                    if (read == -1) {
                        client.shutdownInput();
                        break;
                    }
                    bytes++;
                    digest.update((byte) read);
                }
                log.debug("[S] byte(s) received (and discarded): {}", bytes);
                log.debug("[S] digest: {}", HexFormat.of().formatHex(digest.digest()));
            }
        }
    }

    private Rfc863Tcp0Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
