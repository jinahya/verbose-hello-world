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

import com.github.jinahya.hello.util.HelloWorldSecurityUtils;
import lombok.extern.slf4j.Slf4j;

import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.TimeUnit;

@Slf4j
class Rfc862Tcp3Server {

    public static void main(String... args) throws Exception {
        try (var server = AsynchronousServerSocketChannel.open()) {
            server.bind(_Rfc862Constants.ADDRESS);
            log.debug("bound to {}", server.getLocalAddress());
            try (var client = server.accept().get(8L, TimeUnit.SECONDS)) {
                log.debug("accepted from {}, through {}", client.getRemoteAddress(),
                          client.getLocalAddress());
                var digest = _Rfc862Utils.newDigest();
                var bytes = 0;
                var buffer = _Rfc862Utils.newBuffer();
                log.debug("buffer.capacity: {}", buffer.capacity());
                while (true) {
                    var r = client.read(buffer).get(8L, TimeUnit.SECONDS);
                    if (r == -1) {
                        client.shutdownInput();
                        break;
                    }
                    bytes += r;
                    HelloWorldSecurityUtils.updatePreceding(digest, buffer, r);
                    buffer.flip(); // limit -> position; position -> zero
                    var w = client.write(buffer).get(8L, TimeUnit.SECONDS);
                    assert w >= 0;
                    buffer.compact();
                }
                for (buffer.flip(); buffer.hasRemaining(); ) {
                    var w = client.write(buffer).get(8L, TimeUnit.SECONDS);
                    assert w >= 0;
                }
                client.shutdownOutput();
                _Rfc862Utils.logServerBytesSent(bytes);
                _Rfc862Utils.logDigest(digest);
            }
        }
    }

    private Rfc862Tcp3Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
