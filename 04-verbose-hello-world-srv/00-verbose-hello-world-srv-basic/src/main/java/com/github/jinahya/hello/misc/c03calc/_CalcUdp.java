package com.github.jinahya.hello.misc.c03calc;

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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramSocket;
import java.nio.channels.DatagramChannel;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({
        "java:S101" // class _Calc...
})
abstract class _CalcUdp extends _Calc {

    private static final String LOG_FORMAT_BOUND = "bound to {}";

    private static final String LOG_FORMAT_CONNECTED = "connected to {}";

    private static final String LOG_FORMAT_SENDING = "sending {} byte(s) to {}";

    private static final String LOG_FORMAT_SENT = "{} byte(s) sent to {}";

    private static final String LOG_FORMAT_RECEIVED = "{} byte(s) received from {}";

    public static <T extends DatagramSocket> T logBound(final T socket) {
        Objects.requireNonNull(socket, "socket is null");
        log.info(LOG_FORMAT_BOUND, socket.getLocalSocketAddress());
        return socket;
    }

    @SuppressWarnings({"unchecked"})
    public static <T extends DatagramChannel> T logBound(final T channel)
            throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        if (ThreadLocalRandom.current().nextBoolean()) {
            return (T) logBound(channel.socket()).getChannel();
        }
        log.info(LOG_FORMAT_BOUND, channel.getLocalAddress());
        return channel;
    }
}
