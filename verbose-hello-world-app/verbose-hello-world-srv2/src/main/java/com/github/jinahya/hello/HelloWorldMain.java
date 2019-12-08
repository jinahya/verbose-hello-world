package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-srv2
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

import org.slf4j.Logger;

import java.io.IOException;

import static com.github.jinahya.hello.HelloWorldProvider.findAnyAvailableInstance;
import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * A class whose {@link #main(String[])} method accepts socket connections and sends {@code hello, world} to clients.
 */
public class HelloWorldMain {

    private static final Logger logger = getLogger(lookup().lookupClass());

    /**
     * The main method of this program which accepts socket connections and sends {@code hello, world} to clients.
     *
     * @param args an array of command line arguments
     * @throws IOException if an I/O error occurs.
     */
    public static void main(final String[] args) throws IOException {
        final HelloWorld service = findAnyAvailableInstance();
        // TODO: implement!
    }

    /**
     * Creates a new instance.
     */
    private HelloWorldMain() {
        super();
    }
}
