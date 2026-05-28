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

import static com.github.jinahya.hello.api.HelloWorldTestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
class AsynchronousHelloWorld_Write_AsynchronousFileChannel_Long_Attachment__Test
        extends AsynchronousHelloWorld__Test<HelloWorld> {

    AsynchronousHelloWorld_Write_AsynchronousFileChannel_Long_Attachment__Test() {
        super(HelloWorld.class);
    }

    @BeforeEach
    void __stubService() {
        doAnswer(i -> {
            final var channel = i.getArgument(0, AsynchronousFileChannel.class);
            final var position = i.getArgument(1, Long.class);
            final var attachment = i.getArgument(2);
            final var future = new CompletableFuture<>();
            final var src = hello_world_byte_buffer();
            channel.write(src, position, position, new CompletionHandler<>() { // @formatter:off
                @Override public void completed(final Integer result, Long attachment_) {
                    if (src.hasRemaining()) {
                        attachment_ += result;
                        channel.write(src, attachment_, attachment_, this);
                        return;
                    }
                    future.complete(attachment);
                }
                @Override public void failed(final Throwable exc, final Long attachment_) {
                    future.completeExceptionally(exc);
                }  // @formatter:on
            });
            return future;
        }).when(asynchronousService()).write(any(), anyLong(), any());
    }

    @Test
    void __(@TempDir final Path dir) throws Exception {
        var file = Files.createTempFile(dir, null, null);
        try (var channel = AsynchronousFileChannel.open(file, StandardOpenOption.WRITE)) {
            final var position = ThreadLocalRandom.current().nextLong(1024L);
            final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
            final var result = asynchronousService()
                    .write(channel, position, attachment).toCompletableFuture()
                    .get(8L, TimeUnit.SECONDS);
            assertSame(attachment, result);
            assertEquals(position + HelloWorld.BYTES, Files.size(file));
        }
    }
}
