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

import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.TimeUnit;

@Slf4j
class Rfc862Tcp3Server {

    // @formatter:off
    static class Attachment extends Rfc862Tcp2Server.Attachment {
    }
    // @formatter:on

    public static void main(String... args) throws Exception {
        var timeout = 8L;
        var unit = TimeUnit.SECONDS;
        try (var server = AsynchronousServerSocketChannel.open()) {
            server.bind(_Rfc862Constants.ADDR);
            log.info("bound to {}", server.getLocalAddress());
            try (var client = server.accept().get(timeout, unit)) {
                log.info("accepted from {}, through {}", client.getRemoteAddress(),
                         client.getLocalAddress());
                var attachment = new Attachment();
                while (true) {
                    var r = client.read(attachment.buffer).get(timeout, unit);
                    if (r == -1) {
                        client.shutdownInput();
                        break;
                    }
                    attachment.bytes += r;
                    attachment.digest.update(
                            attachment.slice
                                    .position(attachment.buffer.position() - r)
                                    .limit(attachment.buffer.position())
                    );
                    attachment.buffer.flip(); // limit -> position; position -> zero
                    var w = client.write(attachment.buffer).get(timeout, unit);
                    attachment.buffer.compact();
                }
                for (attachment.buffer.flip(); attachment.buffer.hasRemaining(); ) {
                    var w = client.write(attachment.buffer).get(timeout, unit);
                }
                _Rfc862Utils.logServerBytes(attachment.bytes);
                _Rfc862Utils.logDigest(attachment.digest);
            }
        }
    }

    private Rfc862Tcp3Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
