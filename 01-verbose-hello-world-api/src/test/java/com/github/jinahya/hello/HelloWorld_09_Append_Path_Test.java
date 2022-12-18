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

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.nio.ByteBuffer.allocate;
import static java.nio.file.Files.createTempFile;
import static java.nio.file.Files.size;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.lenient;

/**
 * A class for testing {@link HelloWorld#append(Path)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_09_Append_Path_Arguments_Test
 */
@DisplayName("append(Path)")
@Slf4j
class HelloWorld_09_Append_Path_Test extends HelloWorldTest {

    @BeforeEach
    void stub_WriteChannel_Write12Bytes() throws IOException {
        var service = service();
        lenient()
                .doAnswer(i -> {
                    var channel = i.getArgument(0, WritableByteChannel.class);
                    for (var buffer = allocate(BYTES); buffer.hasRemaining(); ) {
                        channel.write(buffer);
                    }
                    return channel;
                })
                .when(service)
                .write((WritableByteChannel) notNull());
    }

    /**
     * Asserts {@link HelloWorld#append(Path) append(path)} method invokes
     * {@link HelloWorld#write(WritableByteChannel) write(channel)} method with an instance of
     * {@link java.nio.channels.FileChannel}, and asserts {@value HelloWorld#BYTES} bytes are
     * appended to specified {@code path}.
     *
     * @param tempDir a temporary directory to test with.
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("-> write(FileChannel)"
                 + " -> 12 bytes are appended")
    @Test
    void _InvokeWriteChannel12BytesWritten_(@TempDir Path tempDir) throws IOException {
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
        // GIVEN
        var service = service();
        var path = createTempFile(tempDir, null, null);
        // WHEN
        var actual = service.append(path);
        // THEN
        assertSame(path, actual);
    }
}
