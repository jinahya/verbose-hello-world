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

import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Slf4j
class Rfc862Tcp3Server {

    public static void main(String... args) throws Exception {
        try (var server = AsynchronousServerSocketChannel.open()) {
            server.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.TRUE);
            server.setOption(StandardSocketOptions.SO_REUSEPORT, Boolean.TRUE);
            server.bind(_Rfc862Constants.ENDPOINT);
            log.debug("[S] bound to {}", server.getLocalAddress());
            try (var client = server.accept().get(8L, TimeUnit.SECONDS)) {
                log.debug("[S] accepted from {}, through {}", client.getRemoteAddress(),
                          client.getLocalAddress());
                var digest = _Rfc862Utils.newMessageDigest();
                var bytes = 0;
                var buffer = _Rfc862Utils.newByteBuffer();
                while (true) {
                    var read = client.read(buffer).get(8L, TimeUnit.SECONDS);
                    log.trace("[S] - read: {}", read);
                    if (read == -1) {
                        client.shutdownInput();
                        break;
                    }
                    bytes += read;
                    HelloWorldSecurityUtils.updatePreceding(digest, buffer, read);
                    buffer.flip(); // limit -> position; position -> zero
                    var written = client.write(buffer).get(8L, TimeUnit.SECONDS);
                    log.trace("[S] - written: {}", written);
                    buffer.compact();
                }
                for (buffer.flip(); buffer.hasRemaining(); ) {
                    var written = client.write(buffer).get(8L, TimeUnit.SECONDS);
                    log.trace("[S] - written: {}", written);
                }
                client.shutdownOutput();
                log.debug("[S] byte(s) received and echoed back: {}", bytes);
                log.debug("[S] digest: {}", Base64.getEncoder().encodeToString(digest.digest()));
            }
        }
    }

    private Rfc862Tcp3Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
