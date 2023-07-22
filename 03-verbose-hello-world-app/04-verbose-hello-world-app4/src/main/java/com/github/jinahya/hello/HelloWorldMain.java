package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-app3
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

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.inject.se.SeContainerInitializer;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * A program whose {@link #main(String[])} method prints {@code hello, world} to
 * {@link System#out}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
public class HelloWorldMain {

    /**
     * The main method of this program which prints {@code hello, world} to {@link System#out}
     * followed by a platform-specific line separator.
     *
     * @param args an array of command line arguments
     * @throws IOException if an I/O error occurs.
     */
    public static void main(String... args) throws IOException {
        try (var container = SeContainerInitializer.newInstance().initialize()) {
            var main = CDI.current().select(HelloWorldMain.class).get();
            // TODO: Print the 'hello, world' to System.out using main.service
            // TODO: Print a system-dependent line separator the the System.out
        }
    }

    /**
     * Creates a new instance.
     */
    private HelloWorldMain() {
        super();
    }

    @PostConstruct
    private void onPostConstruct() {
    }

    @PreDestroy
    private void onPreDestroy() {
    }

    /**
     * An injected instance of {@link HelloWorld} interface.
     */
    @Inject
    private HelloWorld service;
}
