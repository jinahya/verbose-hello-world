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
import static com.github.jinahya.hello.HelloWorldServerTcp.PORT;
import static java.net.InetAddress.getLoopbackAddress;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
class HelloWorldServerTcpTest {

    @Test
    void test() throws IOException, InterruptedException {
        final var host = getLoopbackAddress();
        final var endpoint = new InetSocketAddress(host, 0);
        try (var server = new HelloWorldServerTcp(endpoint)) {
            server.open();
            final var port = PORT.get();
            clients(4, new InetSocketAddress(host, port), s -> {
                log.debug("[C] received: {}", s);
                assertNotNull(s);
            });
        }
    }
}
