package com.github.jinahya.hello.miscellaneous.c01rfc863;

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
import java.util.concurrent.TimeUnit;

@Slf4j
class Rfc863Tcp3Server {

    public static void main(String... args) throws Exception {
        try (var server = AsynchronousServerSocketChannel.open()) {
            HelloWorldNetUtils.printSocketOptions(server);
            server.bind(_Rfc863Constants.ADDRESS);
            log.info("bound to {}", server.getLocalAddress());
            try (var client = server.accept().get(16L, TimeUnit.SECONDS)) {
                log.info("accepted from {}, through {}", client.getRemoteAddress(),
                         client.getLocalAddress());
                var digest = _Rfc863Utils.newDigest();
                var buffer = _Rfc863Utils.newBuffer();
                var slice = buffer.slice();
                var bytes = 0;
                for (int r; ; ) {
                    if (!buffer.hasRemaining()) {
                        buffer.clear();
                    }
                    if ((r = client.read(buffer).get(16L, TimeUnit.SECONDS)) == -1) {
                        break;
                    }
                    bytes += r;
                    digest.update(
                            slice.position(buffer.position() - r).limit(buffer.position())
                    );
                }
                _Rfc863Utils.logServerBytes(bytes);
                _Rfc863Utils.logDigest(digest);
            }
        }
    }

    private Rfc863Tcp3Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
