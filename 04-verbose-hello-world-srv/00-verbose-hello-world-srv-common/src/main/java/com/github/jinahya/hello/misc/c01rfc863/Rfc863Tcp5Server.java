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

import com.github.jinahya.hello.misc.c00rfc86_._Rfc86_Constants;
import lombok.extern.slf4j.Slf4j;

import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.Executors;

@Slf4j
class Rfc863Tcp5Server {

    public static void main(final String... args) throws Exception {
        final var service = Executors.newFixedThreadPool(1);
        final var group = AsynchronousChannelGroup.withCachedThreadPool(service, 0);
        try (var server = AsynchronousServerSocketChannel.open(group)) {
            server.bind(_Rfc863Constants.ADDR, 1);
            log.info("bound to {}", server.getLocalAddress());
            final var attachment = new Rfc863Tcp5ServerAttachment(group, server);
            attachment.accept();
            ;
            final var terminated = group.awaitTermination(_Rfc86_Constants.SERVER_TIMEOUT,
                                                          _Rfc86_Constants.SERVER_TIMEOUT_UNIT);
            assert terminated : "channel group hasn't been terminated";
            assert attachment.isClosed();
        }
    }

    private Rfc863Tcp5Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
