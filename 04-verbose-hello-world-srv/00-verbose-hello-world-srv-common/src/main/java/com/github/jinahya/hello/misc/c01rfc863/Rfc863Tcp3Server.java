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

import java.nio.channels.AsynchronousServerSocketChannel;

@Slf4j
class Rfc863Tcp3Server {

    // @formatter:on

    public static void main(String... args) throws Exception {
        try (var server = AsynchronousServerSocketChannel.open()) {
            server.bind(_Rfc863Constants.ADDR);
            log.info("bound to {}", server.getLocalAddress());
            try (var client = server.accept().get(_Rfc863Constants.ACCEPT_TIMEOUT_DURATION,
                                                  _Rfc863Constants.ACCEPT_TIMEOUT_UNIT)) {
                log.info("accepted from {}, through {}", client.getRemoteAddress(),
                         client.getLocalAddress());
                try (var attachment = new Rfc863Tcp3ServerAttachment()) {
                    for (int r; ; ) {
                        r = attachment.readFrom(client).get(_Rfc863Constants.READ_TIMEOUT_DURATION,
                                                            _Rfc863Constants.READ_TIMEOUT_UNIT);
                        if (r == -1) {
                            break;
                        }
                        assert r > 0; // why not 0?
                        attachment.updateDigest(r);
                        attachment.increaseBytes(r);
                    }
                }
            }
        }
    }

    private Rfc863Tcp3Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
