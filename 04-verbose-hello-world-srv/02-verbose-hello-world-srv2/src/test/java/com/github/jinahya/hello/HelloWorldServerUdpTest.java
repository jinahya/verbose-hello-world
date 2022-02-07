package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-04-srv2
 * %%
 * Copyright (C) 2018 - 2021 Jinahya, Inc.
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
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import static com.github.jinahya.hello.HelloWorldClientUdp.clients;
import static com.github.jinahya.hello.HelloWorldServerUdp.LOCAL_PORT;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
class HelloWorldServerUdpTest {

    @Test
    void test() throws IOException, InterruptedException {
        final var addr = InetAddress.getLoopbackAddress();
        final var socketAddress = new InetSocketAddress(addr, 0);
        final var server = new HelloWorldServerUdp(socketAddress);
        try {
            server.open();
            final var port = LOCAL_PORT.get();
            final var endpoint = new InetSocketAddress(addr, port);
            clients(4, endpoint, s -> {
                assertNotNull(s);
                log.debug("[C] received: {}", s);
            });
        } finally {
            server.close();
        }
    }
}
