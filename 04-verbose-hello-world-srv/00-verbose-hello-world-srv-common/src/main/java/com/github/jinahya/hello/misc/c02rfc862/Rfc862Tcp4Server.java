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

import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.Executors;

@Slf4j
class Rfc862Tcp4Server {

    public static void main(final String... args) throws Exception {
        final var group = AsynchronousChannelGroup.withThreadPool(Executors.newCachedThreadPool());
        try (var server = AsynchronousServerSocketChannel.open(group)) {
            server.bind(_Rfc862Constants.ADDR, 0);
            log.info("bound to {}", server.getLocalAddress());
            new Rfc862Tcp4ServerAttachment(group, server).accept();
            final var terminated = group.awaitTermination(
                    _Rfc86_Constants.SERVER_TIMEOUT, _Rfc86_Constants.SERVER_TIMEOUT_UNIT
            );
            assert terminated : "channel group has not been terminated!";
        }
    }

    private Rfc862Tcp4Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
