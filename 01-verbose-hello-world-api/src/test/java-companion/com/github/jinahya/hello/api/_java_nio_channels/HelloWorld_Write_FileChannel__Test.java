package com.github.jinahya.hello.api._java_nio_channels;


/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2026 Jinahya, Inc.
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

import com.github.jinahya.hello.api.*;
import com.github.jinahya.hello.api.annotations.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.*;
import org.mockito.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * A class for exploring {@link HelloWorld#write(FileChannel, long) write(channel, position)} method
 * with real implementations.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@_HideNameFromPublishing
@DisplayName("HelloWorld.write(FileChannel, position) / extras")
@Slf4j
class HelloWorld_Write_FileChannel__Test extends HelloWorld__Test {

    /**
     * The {@link TempDir} shared by tests in this class.
     */
    @TempDir
    private static Path tempDir;

    // ---------------------------------------------------------------------------------------------

    /**
     * Stubs {@code service.write(channel, position)} to write the {@code hello-world-bytes} to the
     * channel before each test.
     *
     * @throws IOException if an I/O error occurs.
     */
    @BeforeEach
    void __stubService() throws IOException {
        doAnswer(i -> {
            final var channel = i.getArgument(0, FileChannel.class);
            var position = i.getArgument(1, Long.class);
            for (final var b = hello_world_byte_buffer(); b.hasRemaining(); ) {
                position += channel.write(b, position);
            }
            return channel;
        }).when(service()).write(
                ArgumentMatchers.<FileChannel>notNull(),
                longThat(v -> v >= 0L)
        );
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that the method writes the {@code hello-world-bytes} to a real {@link FileChannel}
     * at a {@code position}, without changing the channel's own position. The values of the bytes
     * before the {@code position} are unspecified, and are not verified.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("real FileChannel")
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var path = Files.createTempFile(tempDir, null, null);
        final var position = ThreadLocalRandom.current().nextLong(0L, 128L);
        // ------------------------------------------------------------------------------------ when
        try (var channel = FileChannel.open(path, StandardOpenOption.WRITE)) {
            service().write(channel, position);
            // -------------------------------------------------------------------------------- then
            assertEquals(0L, channel.position());
        }
        // ------------------------------------------------------------------------------------ then
        // the values of the bytes before the position, if any, are unspecified
        final var bytes = Files.readAllBytes(path);
        assertEquals(position + HelloWorld.BYTES, bytes.length);
        assertArrayEquals(
                hello_world_byte_array(),
                Arrays.copyOfRange(bytes, (int) position, bytes.length)
        );
    }
}
