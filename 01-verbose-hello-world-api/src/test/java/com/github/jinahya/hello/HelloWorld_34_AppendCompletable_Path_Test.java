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
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.lang.Long.MAX_VALUE;
import static java.nio.ByteBuffer.allocate;
import static java.nio.channels.AsynchronousFileChannel.open;
import static java.nio.file.Files.createTempFile;
import static java.nio.file.Files.size;
import static java.util.Arrays.asList;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
        willAnswer(i -> supplyAsync(() -> {
            var channel = i.getArgument(0, AsynchronousFileChannel.class);
            var position = i.getArgument(1, Long.class);
            for (var b = allocate(BYTES); b.hasRemaining(); ) {
                try {
                    channel.write(b, position + b.position()).get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException("failed to write " + b + " to " + channel, e);
                }
            }
            return channel;
        })).given(serviceInstance()).writeCompletable(notNull(), longThat(v -> v >= 0L));
    }

    @Test
    void __() throws IOException {
        var service = serviceInstance();
        var path = mock(Path.class);
        var channel = mock(AsynchronousFileChannel.class);
        var size = current().nextLong(MAX_VALUE - BYTES);
        given(channel.size()).willReturn(size);
        try (var mockedStatic = mockStatic(AsynchronousFileChannel.class)) {
            mockedStatic.when(() -> open(same(path), any(OpenOption[].class))).thenReturn(channel);
            // -------------------------------------------------------------------------------- WHEN
            var result = service.appendCompletable(path);
            // -------------------------------------------------------------------------------- THEN
            var optionsCaptor = ArgumentCaptor.forClass(OpenOption[].class);
            mockedStatic.verify(times(1),
                                () -> {
                                    try {
                                        AsynchronousFileChannel.open(path, optionsCaptor.capture());
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                });
            var options = new ArrayList<>(asList(optionsCaptor.getValue()));
            assertTrue(options.remove(StandardOpenOption.CREATE));
            assertTrue(options.remove(StandardOpenOption.WRITE));
            assertTrue(options.remove(StandardOpenOption.APPEND));
            assertTrue(options.isEmpty());
            verify(service, times(1)).writeCompletable(channel, size);
            verify(channel, times(1)).force(false);
            verify(channel, times(1)).close();
            assertSame(path, result);
        }
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
