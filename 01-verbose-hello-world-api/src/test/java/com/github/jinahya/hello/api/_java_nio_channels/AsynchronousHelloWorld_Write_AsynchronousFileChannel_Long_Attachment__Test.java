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
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.*;
import org.mockito.*;

import java.nio.channels.*;
import java.nio.file.*;
import java.util.concurrent.*;

@畵蛇添足
@Slf4j
class AsynchronousHelloWorld_Write_AsynchronousFileChannel_Long_Attachment__Test
        extends AsynchronousHelloWorld__Test<HelloWorld> {

    AsynchronousHelloWorld_Write_AsynchronousFileChannel_Long_Attachment__Test() {
        super(HelloWorld.class);
    }

    @BeforeEach
    void __() { // @formatter:off
        Mockito.doAnswer(i -> {
            var channel = i.getArgument(0, AsynchronousFileChannel.class);
            var position = i.getArgument(1, Long.class);
            var attachment = i.getArgument(2);
            var future = new CompletableFuture<>();
            var src = HelloWorldTestUtils.hello_world_byte_buffer();
            channel.write(src, position, position, new CompletionHandler<>() {
                @Override
                public void completed(Integer n, Long p) {
                    if (src.hasRemaining()) {
                        long next = p + n;
                        channel.write(src, next, next, this);
                        return;
                    }
                    future.complete(attachment);
                }
                @Override
                public void failed(Throwable t, Long p) {
                    future.completeExceptionally(t);
                }
            });
            return future;
        }).when(asynchronousService()).write(
                ArgumentMatchers.<AsynchronousFileChannel>notNull(),
                ArgumentMatchers.anyLong(),
                ArgumentMatchers.any()
        ); // @formatter:on
    }

    @Test
    void __(@TempDir final Path dir) throws Exception { // @formatter:off
        var file = Files.createTempFile(dir, null, null);
        try (var channel = AsynchronousFileChannel.open(file, StandardOpenOption.WRITE)) {
            AsynchronousHelloWorld asynchronousService = asynchronousService();
            var attachment = new Object();
            var result = asynchronousService.write(channel, 0L, attachment).toCompletableFuture().get(8L, TimeUnit.SECONDS);
            Assertions.assertSame(attachment, result);
        }
        Assertions.assertEquals(HelloWorld.BYTES, Files.size(file)); // @formatter:on
    }
}
