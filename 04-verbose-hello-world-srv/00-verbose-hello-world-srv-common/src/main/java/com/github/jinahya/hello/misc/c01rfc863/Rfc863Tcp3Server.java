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

import com.github.jinahya.hello.util.HelloWorldNetUtils;
import lombok.extern.slf4j.Slf4j;

import java.nio.channels.AsynchronousServerSocketChannel;

@Slf4j
class Rfc863Tcp3Server {

    // @formatter:off
    static class Attachment extends Rfc863Tcp2Server.Attachment {
    }
    // @formatter:on

    public static void main(String... args) throws Exception {
        try (var server = AsynchronousServerSocketChannel.open()) {
            HelloWorldNetUtils.printSocketOptions(AsynchronousServerSocketChannel.class, server);
            server.bind(_Rfc863Constants.ADDR);
            log.info("bound to {}", server.getLocalAddress());
            try (var client = server.accept().get(_Rfc863Constants.ACCEPT_TIMEOUT_DURATION,
                                                  _Rfc863Constants.ACCEPT_TIMEOUT_UNIT)) {
                log.info("accepted from {}, through {}", client.getRemoteAddress(),
                         client.getLocalAddress());
                var attachment = new Attachment();
                for (int r; ; ) {
                    if (!attachment.buffer.hasRemaining()) {
                        attachment.buffer.clear();
                    }
                    assert attachment.buffer.hasRemaining();
                    r = client.read(attachment.buffer).get(_Rfc863Constants.READ_TIMEOUT_DURATION,
                                                           _Rfc863Constants.READ_TIMEOUT_UNIT);
                    if (r == -1) {
                        break;
                    }
                    assert r > 0;
                    attachment.bytes += r;
                    attachment.digest.update(
                            attachment.slice
                                    .position(attachment.buffer.position() - r)
                                    .limit(attachment.buffer.position())
                    );
                }
                _Rfc863Utils.logServerBytes(attachment.bytes);
                _Rfc863Utils.logDigest(attachment.digest);
            }
        }
    }

    private Rfc863Tcp3Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
