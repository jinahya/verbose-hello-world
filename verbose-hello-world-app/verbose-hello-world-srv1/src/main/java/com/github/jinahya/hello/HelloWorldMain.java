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

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;

import static java.util.ServiceLoader.load;

/**
 * A class whose {@link #main(String[])} method accepts socket connections and sends {@code hello, world} to clients.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
public class HelloWorldMain {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Connects to localhost with specified port number and reads exactly {@value com.github.jinahya.hello.HelloWorld#BYTES}
     * bytes and prints it as a {@link StandardCharsets#US_ASCII US-ASCII} string.
     *
     * @param port the local port number to connect.
     */
    private static void connect(final int port) {
        try {
            try (Socket client = new Socket()) {
                final SocketAddress endpoint = new InetSocketAddress(InetAddress.getLocalHost(), port);
                client.connect(endpoint, 8192);
                final byte[] array = new byte[HelloWorld.BYTES];
                new DataInputStream(client.getInputStream()).readFully(array);
                System.out.printf("%s%n", new String(array, StandardCharsets.US_ASCII));
            }
        } catch (final IOException ioe) {
            log.error("failed to connect", ioe);
        }
    }

    /**
     * The main method of this program which accepts socket connections and sends {@code hello, world} to clients.
     *
     * @param args an array of command line arguments
     * @throws IOException if an I/O error occurs.
     */
    public static void main(final String... args) throws IOException {
        final HelloWorld helloWorld = load(HelloWorld.class).iterator().next();
        log.info("localhost: {}", InetAddress.getLocalHost());
        // TODO: implement!
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance.
     */
    private HelloWorldMain() {
        super();
    }
}
