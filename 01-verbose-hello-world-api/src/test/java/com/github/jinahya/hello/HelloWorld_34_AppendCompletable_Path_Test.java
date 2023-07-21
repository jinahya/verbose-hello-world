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
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.FileChannel;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.lang.Long.MAX_VALUE;
import static java.nio.ByteBuffer.allocate;
import static java.nio.channels.AsynchronousFileChannel.open;
import static java.nio.file.Files.createTempFile;
import static java.nio.file.Files.getLastModifiedTime;
import static java.nio.file.Files.size;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.util.Arrays.asList;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.longThat;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
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
    void _beforeEach() {
        willAnswer(i -> {
            var channel = i.getArgument(0, AsynchronousFileChannel.class);
            var position = i.getArgument(1, Long.class);
            var future = new CompletableFuture<>();
            var src = allocate(BYTES);
            channel.write(src, position, position, new CompletionHandler<>() { // @formatter:off
                @Override public void completed(Integer result, Long attachment) {
                    if (!src.hasRemaining()) {
                        future.complete(channel);
                        return;
                    }
                    attachment += result;
                    channel.write(src, attachment, attachment, this);
                }
                @Override public void failed(Throwable exc, Long attachment) {
                    future.completeExceptionally(exc);
                } // @formatter:on
            });
            return future;
        }).given(serviceInstance()).writeCompletable(notNull(), longThat(v -> v >= 0L));
    }

    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- GIVEN
        var service = serviceInstance();
        var path = mock(Path.class);
        var channel = mock(AsynchronousFileChannel.class);
        _stub_ToComplete(channel, null);
        var size = current().nextLong(MAX_VALUE - BYTES);
        given(channel.size()).willReturn(size);
        try (var mockedStatic = mockStatic(AsynchronousFileChannel.class)) {
            mockedStatic.when(() -> open(same(path), any(OpenOption[].class))).thenReturn(channel);
            // -------------------------------------------------------------------------------- WHEN
            var result = service.appendCompletable(path).join();
            // -------------------------------------------------------------------------------- THEN
            var optionsCaptor = forClass(OpenOption[].class);
            mockedStatic.verify(() -> open(same(path), optionsCaptor.capture()), times(1));
            var options = new ArrayList<>(asList(optionsCaptor.getValue()));
            assertTrue(options.remove(StandardOpenOption.CREATE));
            assertTrue(options.remove(WRITE));
            assertTrue(options.isEmpty());
            verify(channel, times(1)).size();
            verify(service, times(1)).writeCompletable(channel, size);
            verify(channel, times(1)).force(false);
            verify(channel, times(1)).close();
            assertSame(path, result);
        }
    }

    @畵蛇添足
    @Test
    void __(@TempDir Path tempDir) throws IOException {
        // ----------------------------------------------------------------------------------- GIVEN
        var service = serviceInstance();
        var path = createTempFile(tempDir, null, null);
        if (current().nextBoolean()) {
            log.debug("lastModifiedTime: {}", getLastModifiedTime(path));
            try (var channel = FileChannel.open(path, WRITE)) {
                for (var src = allocate(current().nextInt(1024)); src.hasRemaining(); ) {
                    channel.write(src);
                }
                channel.force(true);
                log.debug("lastModifiedTime: {}", getLastModifiedTime(path));
            }
        }
        var size = size(path);
        // ------------------------------------------------------------------------------------ WHEN
        service.appendCompletable(path).join();
        // ------------------------------------------------------------------------------------ THEN
        assertEquals(size + BYTES, size(path));
    }
}
