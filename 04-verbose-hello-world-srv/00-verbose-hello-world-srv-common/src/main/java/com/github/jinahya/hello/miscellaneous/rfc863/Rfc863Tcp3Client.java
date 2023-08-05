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

import com.github.jinahya.hello.util.HelloWorldSecurityUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.HexFormat;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static com.github.jinahya.hello.miscellaneous.rfc863._Rfc863Constants.ADDR;

@Slf4j
class Rfc863Tcp3Client {

    public static void main(String... args) throws Exception {
        try (var client = AsynchronousSocketChannel.open()) {
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(ADDR, 0));
                log.debug("[C] bound to {}", client.getLocalAddress());
            }
            client.connect(_Rfc863Constants.ENDPOINT).get(8L, TimeUnit.SECONDS);
            log.debug("[C] connected to {}, through {}", client.getRemoteAddress(),
                      client.getLocalAddress());
            var bytes = ThreadLocalRandom.current().nextInt(1048576);
            log.debug("[C] byte(s) to send: {}", bytes);
            var buffer = _Rfc863Utils.newByteBuffer();
            buffer.position(buffer.limit());
            var digest = _Rfc863Utils.newMessageDigest();
            while (bytes > 0) {
                if (!buffer.hasRemaining()) {
                    buffer.clear().limit(Math.min(buffer.capacity(), bytes));
                    ThreadLocalRandom.current().nextBytes(buffer.array());
                }
                var written = client.write(buffer).get(8L, TimeUnit.SECONDS);
                log.trace("[C] - written: {}", written);
                bytes -= written;
                HelloWorldSecurityUtils.updatePreceding(digest, buffer, written);
            }
            log.debug("[S] digest: {}", HexFormat.of().formatHex(digest.digest()));
            client.shutdownOutput();
        }
    }

    private Rfc863Tcp3Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
