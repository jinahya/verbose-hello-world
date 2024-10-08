package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-api
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

import com.github.jinahya.hello.util._ExcludeFromCoverage_PrivateConstructor_Obviously;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@SuppressWarnings({
        "java:S101"
})
public final class HelloWorldTestUtils {

    private static <T extends File> T writeSome_(final T file) throws IOException {
        Objects.requireNonNull(file, "file is null");
        try (var stream = new FileOutputStream(file)) {
            stream.write(new byte[ThreadLocalRandom.current().nextInt(128)]);
        }
        return file;
    }

    private static <T extends Path> T writeSome_(final T path) throws IOException {
        Objects.requireNonNull(path, "path is null");
        try (var channel = FileChannel.open(path, StandardOpenOption.WRITE)) {
            for (final var b = ByteBuffer.allocate(ThreadLocalRandom.current().nextInt(128));
                 b.hasRemaining(); ) {
                final var written = channel.write(b);
                assert written >= 0;
            }
        }
        return path;
    }

    @SuppressWarnings({"unchecked"})
    public static <T extends File> T writeSome(final T file) throws IOException {
        Objects.requireNonNull(file, "file is null");
        if (ThreadLocalRandom.current().nextBoolean()) {
            return (T) writeSome_(file.toPath()).toFile();
        }
        return writeSome_(file);
    }

    @SuppressWarnings({"unchecked"})
    public static <T extends Path> T writeSome(final T path) throws IOException {
        Objects.requireNonNull(path, "path is null");
        if (ThreadLocalRandom.current().nextBoolean()) {
            return (T) writeSome_(path.toFile()).toPath();
        }
        return writeSome_(path);
    }

    // ---------------------------------------------------------------------------------- Awaitility
    public static void awaitFor(final Duration duration) {
        log.debug("awaiting for {}...", duration);
        Awaitility.await()
                .timeout(duration.plusSeconds(1L))
                .pollDelay(duration)
                .untilAsserted(() -> Assertions.assertTrue(true));
    }

    public static void awaitFor(final long amount, final TemporalUnit unit) {
        awaitFor(Duration.of(amount, unit));
    }

    public static void awaitForOneSecond() {
        awaitFor(1L, ChronoUnit.SECONDS);
    }

    // ---------------------------------------------------------------------------------------------
    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private HelloWorldTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
