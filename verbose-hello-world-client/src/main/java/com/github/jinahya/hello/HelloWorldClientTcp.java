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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * A class whose {@link #main(String[])} method connects to a remote socket and prints the response.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
public class HelloWorldClientTcp extends HelloWorldClient {

    /**
     * The main method of this program which connects to a remote socket and prints the response.
     *
     * @param args an array of command line arguments
     * @throws IOException if an I/O error occurs.
     */
    public static void main(final String... args) throws IOException {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host(), port()));
            final byte[] array = new byte[HelloWorld.BYTES];
            new DataInputStream(socket.getInputStream()).readFully(array);
            System.out.printf("%s%n", new String(array, StandardCharsets.US_ASCII));
        }
    }

    private HelloWorldClientTcp() {
        throw new AssertionError("instantiation is not allowed");
    }
}
