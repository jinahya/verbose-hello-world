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
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
final class IHelloWorldServerUtils {

    /**
     * Starts a new daemon thread which reads lines from {@link System#in} and closes specified closeable for "{@code
     * quit}".
     *
     * @param closeable the closeable to close.
     */
    static void readQuitToClose(final Closeable closeable) throws InterruptedException, IOException {
        if (closeable == null) {
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
            } catch (final IOException ioe) {
                log.error("failed to read 'quit'", ioe);
            }
        });
        thread.setDaemon(true);
        thread.start();
        thread.join();
        closeable.close();
    }

    private IHelloWorldServerUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
