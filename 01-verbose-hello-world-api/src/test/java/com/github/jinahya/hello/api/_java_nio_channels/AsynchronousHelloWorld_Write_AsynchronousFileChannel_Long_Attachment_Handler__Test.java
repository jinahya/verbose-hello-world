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
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.*;

import java.nio.channels.*;
import java.nio.file.*;
import java.util.concurrent.*;
import java.util.function.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * A class for exploring
 * {@link AsynchronousHelloWorld#write(AsynchronousFileChannel, long, Object, CompletionHandler)
 * write(channel, position, attachment, handler)} method with real implementations.
 *
 * @param <T> the subtype of {@link AsynchronousHelloWorld}.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(channel, position, attachment, handler)")
@Slf4j
abstract class AsynchronousHelloWorld_Write_AsynchronousFileChannel_Long_Attachment_Handler__Test<
        T extends AsynchronousHelloWorld<HelloWorld>
        >
        extends AsynchronousHelloWorld__Test<HelloWorld, T> {

    AsynchronousHelloWorld_Write_AsynchronousFileChannel_Long_Attachment_Handler__Test(
            final Function<? super HelloWorld, ? extends T> initializer) {
        super(HelloWorld.class, initializer);
    }

    // ---------------------------------------------------------------------------------------------

    @BeforeEach
    @SuppressWarnings({"unchecked"})
    void __() {
        doAnswer(i -> {
            final var channel = i.getArgument(0, AsynchronousFileChannel.class);
            final var position = i.getArgument(1, Long.class);
            final var attachment = i.getArgument(2);
            final var handler = i.getArgument(3, CompletionHandler.class);
            final var src = hello_world_byte_buffer();
            channel.write(src, position, position, new CompletionHandler<>() { // @formatter:off
                @Override
                public void completed(final Integer result, Long attachment_) {
                    if (src.hasRemaining()) {
                        attachment_ += result;
                        channel.write(src, attachment_, attachment_, this);
                        return;
                    }
                    handler.completed(channel, attachment);
                }
                @Override
                public void failed(final Throwable exc, final Long attachment_) {
                    handler.failed(exc, attachment);
                } // @formatter:on
            });
            return null;
        }).when(asynchronousService()).write(any(), anyLong(), any(), any());
    }

    /**
     * Verifies that the method writes {@code hello-world-bytes} to a real
     * {@link AsynchronousFileChannel} at a {@code position}.
     *
     * @param tempDir the temporary directory.
     * @throws Exception if an error occurs.
     */
    @DisplayName(
            "should write <hello-world-bytes> to a real <AsynchronousFileChannel> at a <position>")
    @Test
    void __(@TempDir final Path tempDir) throws Exception {
        final var tempFile = Files.createTempFile(tempDir, null, null);
        final var done = new CompletableFuture<AsynchronousFileChannel>();
        try (var channel = AsynchronousFileChannel.open(tempFile, StandardOpenOption.WRITE)) {
            final var position = ThreadLocalRandom.current().nextLong(1024L);
            asynchronousService().write(channel, position, null, new CompletionHandler<>() { // @formatter:off
                @Override
                public void completed(final AsynchronousFileChannel result,
                                      final Object attachment) {
                    done.complete(result);
                }
                @Override
                public void failed(final Throwable exc, final Object attachment) {
                    done.completeExceptionally(exc);
                } // @formatter:on
            });
            assertSame(channel, done.get());
            assertEquals(position + HelloWorld.BYTES, Files.size(tempFile));
        }
    }
}
