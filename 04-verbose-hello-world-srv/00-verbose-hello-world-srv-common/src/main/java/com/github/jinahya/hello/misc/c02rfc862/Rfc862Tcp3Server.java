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

import com.github.jinahya.hello.misc._Rfc86_Constants;
import lombok.extern.slf4j.Slf4j;

import java.nio.channels.AsynchronousServerSocketChannel;

@Slf4j
class Rfc862Tcp3Server {

    public static void main(final String... args) throws Exception {
        try (var server = AsynchronousServerSocketChannel.open()) {
            server.bind(_Rfc862Constants.ADDR);
            log.info("bound to {}", server.getLocalAddress());
            try (var client = server.accept().get(_Rfc86_Constants.ACCEPT_TIMEOUT_DURATION,
                                                  _Rfc86_Constants.ACCEPT_TIMEOUT_UNIT)) {
                log.info("accepted from {}, through {}", client.getRemoteAddress(),
                         client.getLocalAddress());
                try (var attachment = new Rfc862Tcp3ServerAttachment(client)) {
                    int r, w;
                    while (true) {
                        r = client.read(attachment.buffer)
                                .get(_Rfc862Constants.READ_TIMEOUT_DURATION,
                                     _Rfc862Constants.READ_TIMEOUT_UNIT);
                        if (r == -1) {
                            break;
                        }
                        assert r >= 0;
                        attachment.bytes += r;
                        attachment.digest.update(
                                attachment.slice
                                        .position(attachment.buffer.position() - r)
                                        .limit(attachment.buffer.position())
                        );
                        attachment.buffer.flip();
                        w = client.write(attachment.buffer).get();
                        assert w >= 0;
                        attachment.buffer.compact();
                    }
                    client.shutdownInput();
                    for (attachment.buffer.flip(); attachment.buffer.hasRemaining(); ) {
                        w = client.write(attachment.buffer).get();
                        assert w >= 0;
                    }
                    _Rfc862Utils.logServerBytes(attachment.bytes);
                    _Rfc862Utils.logDigest(attachment.digest);
                }
            }
        }
    }

    private Rfc862Tcp3Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
