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

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

// https://www.rfc-editor.org/rfc/rfc862
@Slf4j
class Rfc862Tcp1Server {

    public static void main(String... args) throws IOException {
        try (var server = new ServerSocket()) {
            server.setReuseAddress(true);
            server.bind(_Rfc862Constants.ENDPOINT);
            log.debug("[S] bound to {}", server.getLocalSocketAddress());
            server.setSoTimeout((int) TimeUnit.SECONDS.toMillis(8L));
            try (var client = server.accept()) {
                log.debug("[S] accepted from {}, through {}", client.getRemoteSocketAddress(),
                          client.getLocalSocketAddress());
                client.setSoTimeout((int) TimeUnit.SECONDS.toMillis(8L));
                var digest = _Rfc862Utils.newMessageDigest();
                var bytes = 0L;
                var buffer = _Rfc862Utils.newByteArray();
                while (true) {
                    var read = client.getInputStream().read(buffer);
                    log.trace("[S] - read: {}", read);
                    if (read == -1) {
                        client.shutdownInput();
                        break;
                    }
                    assert read > 0;
                    bytes += read;
                    digest.update(buffer, 0, read);
                    client.getOutputStream().write(buffer, 0, read);
                    client.getOutputStream().flush();
                }
                client.shutdownOutput();
                log.debug("[S] byte(s) received and echoed: {}", bytes);
                log.debug("[S] digest: {}", Base64.getEncoder().encodeToString(digest.digest()));
            }
        }
    }

    private Rfc862Tcp1Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
