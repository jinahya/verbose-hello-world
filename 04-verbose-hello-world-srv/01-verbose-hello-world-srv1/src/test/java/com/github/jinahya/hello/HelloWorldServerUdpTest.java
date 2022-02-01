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
import java.util.ServiceLoader;

@Slf4j
class HelloWorldServerUdpTest {

    @Test
    void test() throws IOException, InterruptedException {
        final InetAddress host = InetAddress.getLoopbackAddress();
        final IHelloWorldServer server;
        {
            final HelloWorld service = IHelloWorldServerUtils.loadHelloWorld();
            final SocketAddress endpoint = new InetSocketAddress(host, 0);
            server = new HelloWorldServerUdp(service, endpoint);
        }
        try {
            server.open();
            final int port = HelloWorldServerUdp.LOCAL_PORT.get();
            final SocketAddress endpoint = new InetSocketAddress(host, port);
            HelloWorldClientUdp.clients(4, endpoint, b -> {
                // TODO: Implement!
            });
        } finally {
            server.close();
        }
    }
}
