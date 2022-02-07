package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-04-srv1
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
import java.net.InetSocketAddress;

import static com.github.jinahya.hello.HelloWorldClientTcp.clients;
import static com.github.jinahya.hello.HelloWorldServerTcp.LOCAL_PORT;
import static java.net.InetAddress.getLoopbackAddress;

@Slf4j
class HelloWorldServerTcpTest {

    @Test
    void test() throws IOException, InterruptedException {
        final var addr = getLoopbackAddress();
        final IHelloWorldServer server;
        {
            final var socketAddress = new InetSocketAddress(addr, 0);
            server = new HelloWorldServerTcp(socketAddress);
        }
        try {
            log.debug("opening the server...");
            server.open();
            final var port = LOCAL_PORT.get();
            final var endpoint = new InetSocketAddress(addr, port);
            clients(4, endpoint, string -> {
                log.debug("received: {}", string);
            });
        } finally {
            log.debug("closing the server...");
            server.close();
        }
    }
}
