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

/**
 * A class whose {@link #main(String[])} method accepts socket connections and sends
 * {@code hello, world} to clients.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
public class HelloWorldMainTcp {

    /**
     * The main method of this program which accepts socket connections and sends
     * {@code hello, world} bytes to clients.
     *
     * @param args an array of command line arguments.
     * @throws IOException if an I/O error occurs.
     */
    public static void main(String... args) throws IOException {
        var endpoint = HelloWorldServerUtils.parseEndpoint(args);
        try (var server = new HelloWorldServerTcp()) {
            server.open(endpoint, null);
            HelloWorldServerUtils.readQuitFromStandardInput();
        }
    }

    private HelloWorldMainTcp() {
        throw new AssertionError("instantiation is not allowed");
    }
}
