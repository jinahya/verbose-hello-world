package com.github.jinahya.hello.miscellaneous.rfc862;

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
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Base64;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Rfc862Tcp3Client {

    public static void main(String... args) throws Exception {
        try (var client = AsynchronousSocketChannel.open()) {
            client.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.TRUE);
            client.setOption(StandardSocketOptions.SO_REUSEPORT, Boolean.TRUE);
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc862Constants.ADDR, 0));
                log.debug("[C] bound to {}", client.getLocalAddress());
            }
            client.connect(_Rfc862Constants.ENDPOINT).get(8L, TimeUnit.SECONDS);
            log.debug("[C] connected to {}, through {}", client.getRemoteAddress(),
                      client.getLocalAddress());
            var digest = _Rfc862Utils.newMessageDigest();
            var bytes = ThreadLocalRandom.current().nextInt(1048576);
            log.debug("[C] sending {} byte(s)", bytes);
            var buffer = _Rfc862Utils.newByteBuffer();
            buffer.position(buffer.limit());
            while (bytes > 0) {
                if (!buffer.hasRemaining()) {
                    buffer.clear(); // position -> zero, limit -> capacity
                    ThreadLocalRandom.current().nextBytes(buffer.array());
                    buffer.limit(Math.min(buffer.limit(), bytes));
                }
                var written = client.write(buffer).get(8L, TimeUnit.SECONDS);
                log.trace("[C] - written: {}", written);
                bytes -= written;
                var limit = buffer.limit();
                var position = buffer.position();
                buffer.flip(); // limit -> position, position -> zero
                var read = client.read(buffer).get();
                log.trace("[C] - read: {}", read);
                assert read != -1 : "unexpected eof with remaining bytes to send";
                HelloWorldSecurityUtils.updatePreceding(digest, buffer, read);
                buffer.limit(limit).position(position);
            }
            assert bytes == 0;
            client.shutdownOutput();
            buffer.clear(); // position -> zero, limit -> capacity
            for (int read; true; ) {
                read = client.read(buffer).get(8L, TimeUnit.SECONDS);
                log.trace("[C] - read: {}", read);
                if (read == -1) {
                    client.shutdownInput();
                    break;
                }
                HelloWorldSecurityUtils.updatePreceding(digest, buffer, read);
                if (!buffer.hasRemaining()) {
                    buffer.clear();
                }
            }
            log.debug("[C] digest: {}", Base64.getEncoder().encodeToString(digest.digest()));
        }
    }

    private Rfc862Tcp3Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
