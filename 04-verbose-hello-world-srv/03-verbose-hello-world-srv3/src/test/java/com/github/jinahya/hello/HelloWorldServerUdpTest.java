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
import java.net.SocketAddress;

import static com.github.jinahya.hello.HelloWorldClientUdp.clients;
import static java.lang.System.arraycopy;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;

@Slf4j
class HelloWorldServerUdpTest {

    @Test
    void test() throws IOException, InterruptedException {
        final InetAddress host = InetAddress.getLocalHost();
        final IHelloWorldServer server;
        {
            HelloWorld service = IHelloWorldServerUtils.loadHelloWorld();
            if (true) { // TODO: falsify or remove when HelloWorld#set(array) method is implemented!
                service = spy(service);
                // https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#13
                doAnswer(i -> {
                    final byte[] array = i.getArgument(0);
                    final byte[] src = "hello, world".getBytes(US_ASCII);
                    arraycopy(src, 0, array, 0, src.length);
                    return array;
                }).when(service).set(notNull());
            }
            final SocketAddress endpoint = new InetSocketAddress(host, 0);
            server = new HelloWorldServerUdp(
                    service, endpoint, newCachedThreadPool());
        }
        try {
            server.open();
            final int port = HelloWorldServerUdp.LOCAL_PORT.get();
            final SocketAddress endpoint = new InetSocketAddress(host, port);
            final byte[] expected = "hello, world".getBytes(US_ASCII);
            clients(4, endpoint, b -> assertArrayEquals(expected, b));
        } finally {
            server.close();
        }
    }
}
