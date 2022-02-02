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
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ServiceLoader;

import static com.github.jinahya.hello.HelloWorldClientUdp.clients;
import static com.github.jinahya.hello.HelloWorldServerUdp.LOCAL_PORT;
import static com.github.jinahya.hello.IHelloWorldServerUtils.loadHelloWorld;
import static java.nio.charset.StandardCharsets.US_ASCII;

@Slf4j
class HelloWorldServerUdpTest {

    @Test
    void test() throws IOException, InterruptedException {
        final InetAddress host = InetAddress.getLoopbackAddress();
        final IHelloWorldServer server;
        {
            final HelloWorld helloWorld = loadHelloWorld();
            final SocketAddress socketAddress = new InetSocketAddress(host, 0);
            server = new HelloWorldServerUdp(helloWorld, socketAddress);
        }
        try {
            log.debug("opening the server...");
            server.open();
            final int port = LOCAL_PORT.get();
            final SocketAddress endpoint = new InetSocketAddress(host, port);
            final byte[] expected = "hello, world".getBytes(US_ASCII);
            clients(4, endpoint, b -> {
                // TODO: Implement!
            });
        } finally {
            log.debug("closing the server...");
            server.close();
        }
    }
}
