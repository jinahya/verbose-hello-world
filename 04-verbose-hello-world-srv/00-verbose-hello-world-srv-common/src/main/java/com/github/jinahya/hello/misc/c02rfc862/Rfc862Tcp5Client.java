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
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc862Tcp5Client {

    public static void main(final String... args) throws Exception {
        final var group = AsynchronousChannelGroup.withThreadPool(Executors.newCachedThreadPool());
        try (var client = AsynchronousSocketChannel.open(group)) {
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc86_Constants.HOST, 0));
                log.info("(optionally) bound to {}", client.getLocalAddress());
            }
            new Rfc862Tcp5ClientAttachment(group, client).connect();
            final var terminated = group.awaitTermination(
                    _Rfc86_Constants.CLIENT_TIMEOUT, _Rfc86_Constants.CLIENT_TIMEOUT_UNIT
            );
            assert terminated : "channel group hasn't been terminated!";
        }
    }

    private Rfc862Tcp5Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
