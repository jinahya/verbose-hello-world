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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;
import java.util.concurrent.atomic.LongAdder;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.nio.ByteBuffer.allocate;
import static java.nio.channels.FileChannel.open;
import static java.nio.file.Files.createTempFile;
import static java.nio.file.Files.size;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

/**
 * A class for testing {@link HelloWorld#append(Path) append(path)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_09_Append_Path_Arguments_Test
 */
@DisplayName("append(path)")
@Slf4j
class HelloWorld_09_Append_Path_Test extends HelloWorldTest {

    /**
     * Stubs {@link HelloWorld#write(WritableByteChannel) write(channel)} method writes
     * {@value HelloWorld#BYTES} bytes, and returns the {@code channel}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @org.junit.jupiter.api.BeforeEach
    void stub_Write12Bytes_WriteChannel() throws IOException {
        var service = service();
        doAnswer(i -> {
            WritableByteChannel channel = i.getArgument(0);
            for (var b = allocate(BYTES); b.hasRemaining(); ) {
                channel.write(b);
            }
            return channel;
        }).when(service).write(any(WritableByteChannel.class));
    }

    @DisplayName("-> write(FileChannel)"
                 + " -> 12 bytes are appended")
    @Test
    void _InvokeWriteChannel_() throws IOException {
        // GIVEN
        var service = service();
        var path = mock(Path.class);
        var channel = mock(FileChannel.class);
        var writtenSoFar = new LongAdder();
        when(channel.write(any(ByteBuffer.class))).thenAnswer(i -> {
            ByteBuffer src = i.getArgument(0);
            assert src.hasRemaining();
            var written = current().nextInt(src.remaining() + 1);
            src.position(src.position() + written);
            writtenSoFar.add(written);
            return written;
        });
        try (MockedStatic<FileChannel> mocked = mockStatic(FileChannel.class)) {
            mocked.when(() -> open(same(path), any())).thenAnswer(i -> {
                var arguments = i.getRawArguments(); // Path, OpenOption...
                // TODO: Assert arguments.length == 2
                // TODO: Assert arguments[0] is same as `path` variable
                // TODO: Assert arguments[1] is instance of OpenOption[].class
                // TODO: Assert arguments[1] contains StandardOpenOption.WRITE
                // TODO: Assert arguments[1] contains StandardOpenOption.APPEND
                return channel;
            });
            // WHEN
            service.append(path);
            // THEN, once, FileChannel.open(path, WRITE, APPEND, ...) invoked
            // TODO: Verify FileChannel.open(path, WRITE, APPEND...) invoked, once
            // THEN: once, write(channel) invoked
            // TODO: Assert write(channel) invoked, once
        }
    }

    /**
     * Asserts {@link HelloWorld#append(Path) append(path)} method invokes
     * {@link HelloWorld#write(WritableByteChannel) write(channel)} method with an instance of
     * {@link FileChannel}, and asserts {@value HelloWorld#BYTES} bytes are appended to specified
     * {@code path}.
     *
     * @param tempDir a temporary directory to test with.
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("-> write(FileChannel)"
                 + " -> 12 bytes are appended")
    @Test
    void _InvokeWriteChannel12BytesAppended_(@TempDir Path tempDir) throws IOException {
        // GIVEN: HelloWorld
        var service = service();
        // GIVEN: Path
        var path = createTempFile(tempDir, null, null);
        var size = size(path);
        // WHEN
        service.append(path);
        // THEN: once, write(FileChannel) invoked
        // THEN: 12 byte are appended to the path
    }

    /**
     * Asserts {@link HelloWorld#append(Path) append(path)} method returns the {@code path}
     * argument.
     *
     * @param tempDir a temporary directory to test with.
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("returns path")
    @Test
    void _ReturnPath_(@TempDir Path tempDir) throws IOException {
        // GIVEN: HelloWorld
        var service = service();
        // GIVEN: Path
        var path = createTempFile(tempDir, null, null);
        // WHEN
        var actual = service.append(path);
        // THEN
        assertSame(path, actual);
    }
}
