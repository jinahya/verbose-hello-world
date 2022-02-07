package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-srv1
 * %%
 * Copyright (C) 2018 - 2022 Jinahya, Inc.
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

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;
import static java.net.InetAddress.getLoopbackAddress;
import static java.util.concurrent.TimeUnit.SECONDS;

// 2022-02-07 Windows 와 MacOS 에서 다른 행태를 보인다!!
@Slf4j
class SimpleDatagramServer {

    public static void main(final String[] args) throws IOException {
        final var socket = new DatagramSocket(null);
        socket.bind(new InetSocketAddress(getLoopbackAddress(), 0));
        log.debug("bound to {}", socket.getLocalSocketAddress());
        new Thread(() -> {
            try {
                sleep(SECONDS.toMillis(4L));
                socket.close();
            } catch (final InterruptedException ie) {
                log.error("interrupted", ie);
                currentThread().interrupt();
            }
        }).start();
        while (!socket.isClosed()) {
            final var packet = new DatagramPacket(new byte[0], 0);
            try {
                socket.receive(packet);
                log.debug("received from {}", packet.getSocketAddress());
            } catch (final IOException ioe) {
                if (socket.isClosed()) {
                    break;
                }
                log.error("failed to receive", ioe);
            }
        }
    }
}
