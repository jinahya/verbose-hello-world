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
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static java.lang.Integer.parseInt;

/**
 * A class whose {@link #main(String[])} method connects to a remote socket and prints the response.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
public class HelloWorldMain {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The main method of this program which connects to a remote socket and prints the response.
     *
     * @param args an array of command line arguments
     * @throws IOException if an I/O error occurs.
     */
    public static void main(final String... args) throws IOException {
        final int port = parseInt(args[0]);
        final String host = args.length > 1 ? args[1] : InetAddress.getLocalHost().getHostAddress();
        try (Socket socket = new Socket(host, port)) {
            final byte[] array = new byte[HelloWorld.BYTES];
            new DataInputStream(socket.getInputStream()).readFully(array);
            System.out.printf("%s%n", new String(array, StandardCharsets.US_ASCII));
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance.
     */
    private HelloWorldMain() {
        super();
    }
}