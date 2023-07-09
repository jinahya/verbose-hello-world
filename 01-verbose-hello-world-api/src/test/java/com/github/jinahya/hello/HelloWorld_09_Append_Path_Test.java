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

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;
import java.util.Set;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.nio.ByteBuffer.allocate;
import static java.nio.channels.FileChannel.open;
import static java.nio.file.Files.createTempFile;
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
 * @see HelloWorld_09_Append_Path_Arguments_Test
 */
@DisplayName("append(path)")
@Slf4j
class HelloWorld_09_Append_Path_Test extends _HelloWorldTest {

    @BeforeEach
    void _beforeEach() throws IOException {
        // stubs serviceInstance() as its write(channel) method to write 12 bytes to the channel
        doAnswer(i -> {
            var channel = i.getArgument(0, WritableByteChannel.class);
            for (var buffer = allocate(BYTES); buffer.hasRemaining(); ) {
                channel.write(buffer);
            }
            return channel;
        }).when(serviceInstance()).write(notNull(WritableByteChannel.class));
    }

    @DisplayName("(path) -> write(FileChannel.open(path, CREATE, WRITE, APPEND))")
    @Test
    void _InvokeWriteChannelOpenedFromPath_() throws IOException {
        // ----------------------------------------------------------------------------------- GIVEN
        var service = serviceInstance();
        var path = mock(Path.class);
        var channel = mock(FileChannel.class);
        try (var mockedStatic = mockStatic(FileChannel.class)) {
            mockedStatic.when(() -> open(same(path), notNull())).thenAnswer(i -> {
                var arguments = i.getRawArguments(); // [path, options]
                var options = Set.of(arguments[1]);
                // TODO: Assert, options contains StandardOpenOption.CREATE
                // TODO: Assert, options contains StandardOpenOption.WRITE
                // TODO: Assert, options contains StandardOpenOption.APPEND
                return channel;
            });
            // -------------------------------------------------------------------------------- WHEN
            var result = service.append(path);
            // -------------------------------------------------------------------------------- THEN
            // TODO: Verify, write(channel) invoked, once.
            // TODO: Verify, channel.force(false) invoked, once.
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
    void _12BytesAppended_(@TempDir Path tempDir) throws IOException {
        // GIVEN
        var service = serviceInstance();
        doAnswer(i -> {
            WritableByteChannel channel = i.getArgument(0);
            for (var src = allocate(BYTES); src.hasRemaining(); ) {
                channel.write(src);
            }
            return channel;
        }).when(service).write(any(WritableByteChannel.class));
        var path = createTempFile(tempDir, null, null);
        var size = size(path);
        // WHEN
        service.append(path);
        // THEN: 12 byte are appended to the path
        // TODO: Assert size(path) is equal to size + BYTES
    }
}
