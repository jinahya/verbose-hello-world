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

import com.github.jinahya.hello.util.HelloWorldNioUtils;
import com.github.jinahya.hello.util.HelloWorldSecurityUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.EOFException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Rfc862Tcp3Client {

    public static void main(String... args) throws Exception {
        try (var client = AsynchronousSocketChannel.open()) {
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc862Constants.ADDR, 0));
                log.debug("bound to {}", client.getLocalAddress());
            }
            client.connect(_Rfc862Constants.ADDRESS).get(8L, TimeUnit.SECONDS);
            log.debug("connected to {}, through {}", client.getRemoteAddress(),
                      client.getLocalAddress());
            var digest = _Rfc862Utils.newDigest();
            var bytes = ThreadLocalRandom.current().nextInt(1048576);
            _Rfc862Utils.logClientBytesSending(bytes);
            var buffer = _Rfc862Utils.newBuffer();
            log.debug("buffer.capacity: {}", buffer.capacity());
            buffer.position(buffer.limit());
            while (bytes > 0) {
                if (!buffer.hasRemaining()) {
                    buffer.clear(); // position -> zero, limit -> capacity
                    ThreadLocalRandom.current().nextBytes(buffer.array());
                    buffer.limit(Math.min(buffer.remaining(), bytes));
                }
                var w = client.write(buffer).get();
                HelloWorldSecurityUtils.updatePreceding(digest, buffer, w);
                bytes -= w;
                var r = HelloWorldNioUtils.flipApplyAndRestore(buffer, client::read).get();
                if (r == -1) {
                    throw new EOFException("unexpected eof");
                }
            }
            assert bytes == 0;
            _Rfc862Utils.logDigest(digest);
            client.shutdownOutput();
            for (buffer.clear(); client.read(buffer).get() != -1; ) {
                if (!buffer.hasRemaining()) {
                    buffer.clear();
                }
            }
        }
    }

    private Rfc862Tcp3Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
