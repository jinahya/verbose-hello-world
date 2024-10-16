package com.github.jinahya.hello.srv1;

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

import com.github.jinahya.hello.util.HelloWorldServerUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * A class whose {@link #main(String[])} method serves {@code hello, world} to clients.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorldMainUdp1 {

    /**
     * The main method of this program which serves {@code hello, world} to clients.
     *
     * @param args an array of command line arguments.
     * @throws IOException if an I/O error occurs.
     */
    public static void main(String... args) throws IOException {
        var endpoint = HelloWorldServerUtils.parseAddr(args);
        try (var server = new HelloWorldServerUdp1()) {
            server.open(endpoint, null);
            HelloWorldServerUtils.readQuitFromStandardInput();
        }
    }

    private HelloWorldMainUdp1() {
        throw new AssertionError("instantiation is not allowed");
    }
}
