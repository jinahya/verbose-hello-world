package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-srv1
 * %%
 * Copyright (C) 2018 - 2019 Jinahya, Inc.
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
import java.net.SocketAddress;

/**
 * A program serves {@code hello, world} bytes to clients through a UDP socket.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
public class HelloWorldMainUdp2 {

    /**
     * The main method of this program which {@link HelloWorldServer#open(SocketAddress) opens} an
     * instance of {@link HelloWorldServerUdp2}, with an endpoint
     * {@link HelloWorldServerUtils#parseEndpoint(String...) parsed} from specified command line
     * arguments, and sends {@code hello, world} bytes to clients.
     *
     * @param args an array of command line arguments from which an endpoint is parsed.
     * @throws IOException          if an I/O error occurs.
     * @throws InterruptedException if interrupted while running.
     * @see HelloWorldServerUtils#parseEndpoint(String...)
     * @see HelloWorldServer#open(SocketAddress)
     */
    public static void main(String... args) throws IOException, InterruptedException {
        var endpoint = HelloWorldServerUtils.parseEndpoint(args);
        try (var server = new HelloWorldServerUdp2()) {
            server.open(endpoint);
            HelloWorldServerUtils.startReadingQuitFromStandardInput().join();
        }
    }

    /**
     * Creates a new instance.
     */
    private HelloWorldMainUdp2() {
        throw new AssertionError("instantiation is not allowed");
    }
}
