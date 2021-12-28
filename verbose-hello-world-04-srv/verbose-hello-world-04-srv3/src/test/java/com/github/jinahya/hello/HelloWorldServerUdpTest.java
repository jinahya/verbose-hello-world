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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ServiceLoader;
import java.util.concurrent.Executors;

@Slf4j
class HelloWorldServerUdpTest {

    @Test
    void test() throws IOException, InterruptedException {
        final InetAddress host = InetAddress.getLoopbackAddress();
        final IHelloWorldServer server;
        {
            HelloWorld service = ServiceLoader.load(HelloWorld.class).iterator()
                    .next();
            if (true) { // TODO: falsify or remove when HelloWorld#set(array) method is implemented!
                service = Mockito.spy(service);
                // https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#13
                Mockito.doAnswer(i -> {
                            final byte[] array = i.getArgument(0);
                            final byte[] src = "hello, world".getBytes(
                                    StandardCharsets.US_ASCII);
                            System.arraycopy(src, 0, array, 0, src.length);
                            return array;
                        })
                        .when(service)
                        .set(ArgumentMatchers.notNull());
            }
            final SocketAddress endpoint = new InetSocketAddress(host, 0);
            server = new HelloWorldServerUdp(service, endpoint,
                                             Executors::newCachedThreadPool);
        }
        server.open();
        final int port = HelloWorldServerUdp.LOCAL_PORT.get();
        final SocketAddress endpoint = new InetSocketAddress(host, port);
        HelloWorldClientUdp.clients(4, endpoint, b -> {
            Assertions.assertArrayEquals(
                    "hello, world".getBytes(StandardCharsets.US_ASCII), b);
        });
        server.close();
    }
}
