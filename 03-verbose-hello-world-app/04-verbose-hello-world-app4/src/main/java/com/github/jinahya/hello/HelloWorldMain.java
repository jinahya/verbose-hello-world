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

import jakarta.enterprise.inject.se.SeContainerInitializer;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;

/**
 * A program whose {@link #main(String[])} method prints {@code hello, world} to
 * {@link System#out}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S106",  // Standard outputs should not be used directly to log anything
        "java:S6813"  // Field dependency injection should be avoided
})
class HelloWorldMain {

    /**
     * The main method of this program which prints {@code hello, world} to {@link System#out}
     * followed by a platform-specific line separator.
     *
     * @param args an array of command line arguments
     * @throws IOException if an I/O error occurs.
     */
    public static void main(final String... args) throws IOException {
        try (var container = SeContainerInitializer.newInstance().initialize()) {
            final var instance = CDI.current().select(HelloWorldMain.class).get();
            final var channel = instance.service.write(Channels.newChannel(System.out));
            for (var b = ByteBuffer.wrap(System.lineSeparator().getBytes()); b.hasRemaining(); ) {
                final var written = channel.write(b);
                assert written >= 0;
            }
        }
    }

    // -------------------------------------------------------------------------------- CONSTRUCTORS
    private HelloWorldMain() {
        super();
    }

    // ---------------------------------------------------------------------------------------------
    @HelloWorldQualifier
    @Inject
    private HelloWorld service;
}
