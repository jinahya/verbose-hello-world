package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-srv
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.Callable;

@Slf4j
final class IHelloWorldServerUtils {

    /**
     * Starts a new {@link Thread#setDaemon(boolean) daemon} thread which reads '{@code quit}' from {@link System#in}
     * and closes specified server.
     *
     * @param server the server to close.
     */
    static void readQuitToClose(final IHelloWorldServer server) {
        if (server == null) {
            throw new NullPointerException("closeable is null");
        }
        final Thread thread = new Thread(() -> {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try {
                for (String line; (line = reader.readLine()) != null; ) {
                    if (line.equalsIgnoreCase("quit")) {
                        break;
                    }
                }
                server.close();
            } catch (final IOException ioe) {
                log.error("failed to read 'quit' and/or close the server", ioe);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    static void writeQuitToClose(final Callable<Void> callable) throws IOException {
        Objects.requireNonNull(callable, "runnable is null");
        final InputStream in = System.in;
        try {
            final PipedOutputStream pos = new PipedOutputStream();
            System.setIn(new PipedInputStream(pos));
            try {
                callable.call();
            } catch (final Exception e) {
                log.debug("failed to call {}", callable, e);
            }
            pos.write("quit\n".getBytes(StandardCharsets.US_ASCII));
            pos.flush();
        } finally {
            System.setIn(in);
        }
    }

    private IHelloWorldServerUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
