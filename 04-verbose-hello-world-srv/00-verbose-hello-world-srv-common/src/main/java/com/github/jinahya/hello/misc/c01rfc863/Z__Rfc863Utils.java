package com.github.jinahya.hello.misc.c01rfc863;

/*-
 * #%L
 * verbose-hello-world-srv-common
 * %%
 * Copyright (C) 2018 - 2023 Jinahya, Inc.
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

import org.slf4j.event.Level;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

final class Z__Rfc863Utils {

    static Process fork(final Class<?> main) throws IOException {
        Objects.requireNonNull(main, "main is null");
        final String home = System.getProperty("java.home");
        final String java = Paths.get(home, "bin", "java").toString();
        final String path = System.getProperty("java.class.path");
        final var command = List.of(
                java,
                "-cp",
                path,
                main.getName(),
                Level.ERROR.name()
        );
        return new ProcessBuilder(command)
                .inheritIO()
                .start();
    }

    static ExecutorService newThreadPool() {
        return Executors.newFixedThreadPool(128);
    }

    private Z__Rfc863Utils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
