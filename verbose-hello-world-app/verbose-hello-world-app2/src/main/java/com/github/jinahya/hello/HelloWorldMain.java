package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-app2
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

import java.io.IOException;
import java.util.logging.Logger;

import static java.lang.invoke.MethodHandles.lookup;
import static java.util.logging.Logger.getLogger;

/**
 * A class whose {@link #main(String[])} method prints {@code hello, world} to {@link System#out}.
 */
public class HelloWorldMain {

    private static final Logger logger = getLogger(lookup().lookupClass().getName());

    /**
     * The main method of this program which prints {@code hello, world} followed by a new line character.
     *
     * @param args an array of main arguments
     * @throws IOException if an I/O error occurs.
     */
    public static void main(final String[] args) throws IOException {
        // TODO: implement!
    }

    /**
     * Creates a new instance.
     */
    private HelloWorldMain() {
        super();
    }
}
