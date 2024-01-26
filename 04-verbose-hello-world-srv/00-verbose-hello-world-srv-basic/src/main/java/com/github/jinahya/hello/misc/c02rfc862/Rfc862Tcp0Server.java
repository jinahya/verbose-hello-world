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

import lombok.extern.slf4j.Slf4j;

import java.net.ServerSocket;

@Slf4j
@SuppressWarnings({
        "java:S127" // assign loop counter within body
})
class Rfc862Tcp0Server extends _Rfc862Tcp {

    public static void main(final String... args) throws Exception {
        try (var server = new ServerSocket()) {
            // -------------------------------------------------------------------------------- bind
            server.bind(ADDR, 1);
            logBound(server);
            // ------------------------------------------------------------------------------ accept
            try (var client = logAccepted(server.accept())) {
                // ------------------------------------------------------------------------- prepare
                final var digest = newDigest();
                var bytes = 0L;
                // -------------------------------------------------------------------- read / write
                for (int b; ; ) {
                    // ------------------------------------------------------------------------ read
                    b = client.getInputStream().read();
                    if (b == -1) {
                        break;
                    }
                    bytes++;
                    // ----------------------------------------------------------------------- write
                    client.getOutputStream().write(b);
                    digest.update((byte) b);
                }
                log.debug("[server] flushing output");
                client.getOutputStream().flush();
                // ----------------------------------------------------------------------------- log
                logServerBytes(bytes);
                logDigest(digest);
                log.debug("[server] closing client: " + client);
            }
            log.debug("[server] end-of-try");
        }
        log.debug("[server] end-of-main");
    }

    private Rfc862Tcp0Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}