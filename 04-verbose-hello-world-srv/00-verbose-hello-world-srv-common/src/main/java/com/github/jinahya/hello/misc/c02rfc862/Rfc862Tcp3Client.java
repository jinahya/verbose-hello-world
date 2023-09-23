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
import com.github.jinahya.hello.misc._Rfc86_Utils;
import lombok.extern.slf4j.Slf4j;

import java.io.EOFException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc862Tcp3Client {

    public static void main(final String... args) throws Exception {
        try (var client = AsynchronousSocketChannel.open()) {
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc86_Constants.HOST, 0));
                log.info("(optionally) bound to {}", client.getLocalAddress());
            }
            client.connect(_Rfc862Constants.ADDR).get(_Rfc86_Constants.CONNECT_TIMEOUT,
                                                      _Rfc86_Constants.CONNECT_TIMEOUT_UNIT);
            _Rfc86_Utils.logConnected(client);
            try (var attachment = new Rfc862Tcp3ClientAttachment(client)) {
                for (int w, r; attachment.getBytes() > 0; ) {
                    w = attachment.write();
                    assert w > 0; // why?
                    r = attachment.read();
                    if (r == -1) {
                        throw new EOFException("unexpected eof");
                    }
                    assert r > 0; // why?
                }
                assert attachment.getBytes() == 0;
                attachment.logDigest();
                client.shutdownOutput();
                for (int r; (r = attachment.read()) != -1; ) {
                    assert r >= 0;
                }
            }
        }
    }

    private Rfc862Tcp3Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
