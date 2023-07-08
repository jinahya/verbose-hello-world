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
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.nio.file.Files.createTempFile;
import static java.nio.file.Files.size;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.longThat;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * A class for testing {@link HelloWorld#appendCompletable(Path) appendCompletable(path)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_34_AppendCompletable_Path_Arguments_Test
 */
@DisplayName("appendCompletable(path)")
@Slf4j
class HelloWorld_34_AppendCompletable_Path_Test extends _HelloWorldTest {

    @BeforeEach
    void beforeEach() {
        doAnswer(i -> supplyAsync(() -> {
            var channel = i.getArgument(0, AsynchronousFileChannel.class);
            var position = i.getArgument(1, Long.class);
            var buffer = ByteBuffer.allocate(BYTES);
            while (buffer.hasRemaining()) {
                try {
                    channel.write(buffer, position + buffer.position()).get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(
                            "failed to write " + buffer + " to " + channel, e);
                }
            }
            return channel;
        })).when(serviceInstance()).writeCompletable(notNull(), longThat(v -> v >= 0L));
    }

    @Test
    void __(@TempDir Path tempDir) throws IOException {
        // ----------------------------------------------------------------------------------- GIVEN
        var service = serviceInstance();
        var path = createTempFile(tempDir, null, null);
        if (current().nextBoolean()) {
            // TODO: write some bytes to the path
        }
        var size = size(path);
        // ------------------------------------------------------------------------------------ WHEN
        var future = service.appendCompletable(path);
        var result = future.join();
        // ------------------------------------------------------------------------------------ THEN
        verify(service, times(1)).writeCompletable(
                notNull(), // <channel>
                eq(size)   // <position>
        );
        assertSame(path, result);
        assertEquals(BYTES, size(path) - size);
    }
}
