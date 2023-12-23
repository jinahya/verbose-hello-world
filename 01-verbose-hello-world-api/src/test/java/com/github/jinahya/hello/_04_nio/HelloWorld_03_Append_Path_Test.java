package com.github.jinahya.hello._04_nio;

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

import com.github.jinahya.hello.HelloWorld;
import com.github.jinahya.hello._HelloWorldTest;
import com.github.jinahya.hello.畵蛇添足;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ThreadLocalRandom;

import static java.nio.file.Files.size;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

/**
 * A class for testing {@link HelloWorld#append(Path) append(path)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_03_Append_Path_Arguments_Test
 */
@DisplayName("append(path)")
@Slf4j
@SuppressWarnings({
        "java:S101"
})
class HelloWorld_03_Append_Path_Test
        extends _HelloWorldTest {

    @BeforeEach
    void _beforeEach() throws IOException {
        doAnswer(i -> {
            final var channel = i.getArgument(0, WritableByteChannel.class);
            for (final var src = ByteBuffer.allocate(HelloWorld.BYTES); src.hasRemaining(); ) {
                final var w = channel.write(src);
                assert w >= 0;
            }
            return channel;
        }).when(serviceInstance()).write(notNull(WritableByteChannel.class));
    }

    @DisplayName("-> write(FileChannel.open(path, CREATE, WRITE, APPEND))")
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = serviceInstance();
        final var path = mock(Path.class);
        final var channel = _stub_ToWriteSome(mock(FileChannel.class), null);
        try (var mock = mockStatic(FileChannel.class)) {
            mock.when(() -> FileChannel.open(same(path), any(OpenOption[].class)))
                    .thenReturn(channel);
            // -------------------------------------------------------------------------------- when
            final var result = service.append(path);
            // -------------------------------------------------------------------------------- then
            // TODO: Verify, FileChannel.open(path, WRITE, CREATE, APPEND) invoked, once.
            // TODO: Verify, write(channel) invoked, once.
            // TODO: Verify, channel.force(true) invoked, once.
            // TODO: Verify, channel.close() invoked, once.
            assertSame(path, result);
        }
    }

    /**
     * Asserts {@link HelloWorld#append(Path) append(path)} method appends {@value HelloWorld#BYTES}
     * bytes to {@code path}.
     *
     * @param tempDir a temporary directory to test with.
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("-> 12 bytes are appended")
    @Test
    @畵蛇添足
    void __(@TempDir Path tempDir) throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = serviceInstance();
        final var path = Files.createTempFile(tempDir, null, null);
        // write some bytes to the path
        if (ThreadLocalRandom.current().nextBoolean()) {
            try (var channel = FileChannel.open(path, StandardOpenOption.WRITE)) {
                final var buffer = ByteBuffer.allocate(ThreadLocalRandom.current().nextInt(1024));
                while (buffer.hasRemaining()) {
                    final var w = channel.write(buffer);
                    assert w >= 0;
                }
                channel.force(false);
            }
        }
        final var size = size(path);
        log.debug("path.size before: {}", size);
        // ------------------------------------------------------------------------------------ when
        final var result = service.append(path);
        log.debug("path.size after: {}", size(path));
        // ------------------------------------------------------------------------------------ then
        // TODO: assert path's size is equal to size + HelloWorld.BYTE
    }
}
