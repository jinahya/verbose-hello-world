package com.github.jinahya.hello.api;

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

import com.github.jinahya.hello.api.util._ExcludeFromCoverage_PrivateConstructor_Obviously;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

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

    static HelloWorld requireMock(final HelloWorld service) {
        Objects.requireNonNull(service, "service is null");
        if (!Mockito.mockingDetails(service).isMock()) {
            throw new IllegalArgumentException("not a mock: " + service);
        }
        return service;
    }

    /**
     * Stubs given mock serivce's {@link HelloWorld#set(byte[]) set(array)} method to just return
     * the {@code array}.
     *
     * @param service the mock service.
     * @see #verify_set_array12_invoked_once(HelloWorld)
     */
    static void stub_set_array_will_return_the_array(final HelloWorld service) {
        requireMock(service);
        Mockito.doAnswer(i -> i.getArgument(0))
                .when(service)
                .set(ArgumentMatchers.any());
    }

    static byte[] verify_set_array12_invoked_once(final HelloWorld service) {
        requireMock(service);
        final var captor = ArgumentCaptor.forClass(byte[].class);
        Mockito.verify(service, Mockito.times(1)).set(captor.capture());
        final var array = captor.getValue();
        Assertions.assertNotNull(array);
        Assertions.assertEquals(HelloWorld.BYTES, array.length);
        return array;
    }

    /**
     * Stubs given mock service's {@link HelloWorld#put(ByteBuffer) put(buffer)} method to just
     * return the {@code bufefer} whose {@link ByteBuffer#position() position} is increased by
     * {@value HelloWorld#BYTES}.
     *
     * @param service the mock service.
     * @see #verify_put_buffer12_invoked_once(HelloWorld)
     */
    static void stub_put_buffer_will_increase_buffer_position_by_12(final HelloWorld service) {
        requireMock(service);
        Mockito.doAnswer(i -> {
                    final var buffer = i.getArgument(0, ByteBuffer.class);
                    buffer.position(buffer.position() + HelloWorld.BYTES);
                    return buffer;
                })
                .when(service)
                .put(ArgumentMatchers.argThat(b -> b != null && b.remaining() >= HelloWorld.BYTES));
    }

    static ByteBuffer verify_put_buffer12_invoked_once(final HelloWorld service) {
        requireMock(service);
        final var captor = ArgumentCaptor.forClass(ByteBuffer.class);
        Mockito.verify(service, Mockito.times(1)).put(captor.capture());
        final var buffer = captor.getValue();
        Assertions.assertNotNull(buffer);
        Assertions.assertEquals(HelloWorld.BYTES, buffer.capacity());
        return buffer;
    }

    // ---------------------------------------------------------------------------------------------
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
