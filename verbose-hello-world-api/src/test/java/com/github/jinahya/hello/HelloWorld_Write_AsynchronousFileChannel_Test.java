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
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

import static java.nio.channels.AsynchronousFileChannel.open;
import static java.nio.file.Files.createTempFile;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * A class for testing {@link HelloWorld#write(AsynchronousByteChannel)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorld_Write_AsynchronousFileChannel_Test extends HelloWorldTest {

    /**
     * Asserts {@link HelloWorld#write(AsynchronousFileChannel, long)} throws a {@link NullPointerException} when {@code
     * channel} argument is {@code null}.
     */
    @DisplayName("write(channel, position) throws NullPointerException when channel is null")
    @Test
    void append_NullPointerException_ChannelIsNull() {
        assertThrows(NullPointerException.class, () -> helloWorld.write((AsynchronousFileChannel) null, 0L));
    }

    /**
     * Asserts {@link HelloWorld#write(AsynchronousFileChannel, long)} method invokes {@link HelloWorld#put(ByteBuffer)}
     * and writes the buffer to {@code channel}.
     *
     * @throws IOException          if an I/O error occurs.
     * @throws ExecutionException   if failed to work.
     * @throws InterruptedException if interrupted while executing.
     */
    @DisplayName("write(channel, position) invokes put(buffer) writes the buffer to channel")
    @Test
    void write_InvokePutBufferWriteBufferToChannel_(final @TempDir Path tempDir)
            throws IOException, ExecutionException, InterruptedException {
        final Path file = createTempFile(tempDir, null, null);
        try (AsynchronousFileChannel channel = spy(open(file, WRITE))) { // APPEND not allowed!
            final long position = current().nextLong(128L);
            helloWorld.write(channel, position);
            final ArgumentCaptor<ByteBuffer> bufferCaptor1 = ArgumentCaptor.forClass(ByteBuffer.class);
            verify(helloWorld, times(1)).put(bufferCaptor1.capture());
            final ByteBuffer buffer1 = bufferCaptor1.getValue();
        }
    }
}
