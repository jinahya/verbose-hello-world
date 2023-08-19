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
import com.github.jinahya.hello.util.HelloWorldSecurityUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static com.github.jinahya.hello.miscellaneous.c01rfc863._Rfc863Constants.ADDR;

@Slf4j
class Rfc863Tcp3Client {

    public static void main(String... args) throws Exception {
        try (var client = AsynchronousSocketChannel.open()) {
            HelloWorldNetUtils.printSocketOptions(client);
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(ADDR, 0));
                log.debug("bound to {}", client.getLocalAddress());
            }
            client.connect(_Rfc863Constants.ADDRESS).get(1L, TimeUnit.SECONDS);
            log.debug("connected to {}, through {}", client.getRemoteAddress(),
                      client.getLocalAddress());
            var bytes = ThreadLocalRandom.current().nextInt(1048576);
            _Rfc863Utils.logClientBytes(bytes);
            var buffer = _Rfc863Utils.newBuffer();
            buffer.position(buffer.limit());
            var digest = _Rfc863Utils.newDigest();
            while (bytes > 0) {
                if (!buffer.hasRemaining()) {
                    buffer.clear().limit(Math.min(buffer.capacity(), bytes));
                    ThreadLocalRandom.current().nextBytes(buffer.array());
                }
                var w = client.write(buffer).get();
                bytes -= w;
                HelloWorldSecurityUtils.updatePreceding(digest, buffer, w);
            }
            _Rfc863Utils.logDigest(digest);
        }
    }

    private Rfc863Tcp3Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
