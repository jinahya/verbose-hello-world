package com.github.jinahya.hello._04_nio;

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

import com.github.jinahya.hello.HelloWorld;
import com.github.jinahya.hello._HelloWorldTest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;

import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.lang.Long.MAX_VALUE;
import static java.nio.ByteBuffer.allocate;
import static java.nio.channels.AsynchronousFileChannel.open;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.util.Arrays.asList;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.longThat;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * A class for testing {@link HelloWorld#appendCompletable(Path) appendCompletable(path)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_12_AppendCompletable_Path_Arguments_Test
 */
@DisplayName("appendCompletable(path)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_12_AppendCompletable_Path_Test extends _HelloWorldTest {

    @BeforeEach
    void beforeEach() {
        BDDMockito.willAnswer(i -> {
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
        }).given(service())
                .writeCompletable(notNull(), longThat(v -> v >= 0L));
    }

    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        var service = service();
        var path = mock(Path.class);
        var channel = mock(AsynchronousFileChannel.class);
        _stub_ToComplete(channel, null);
        var size = current().nextLong(MAX_VALUE - BYTES);
        given(channel.size()).willReturn(size);
        try (var mockedStatic = mockStatic(AsynchronousFileChannel.class)) {
            mockedStatic.when(() -> open(same(path), any(OpenOption[].class))).thenReturn(channel);
            // -------------------------------------------------------------------------------- when
            var result = service.appendCompletable(path).join();
            // -------------------------------------------------------------------------------- then
            var optionsCaptor = forClass(OpenOption[].class);
            mockedStatic.verify(() -> open(same(path), optionsCaptor.capture()), times(1));
            var options = new ArrayList<>(asList(optionsCaptor.getValue()));
            assertTrue(options.remove(StandardOpenOption.CREATE));
            assertTrue(options.remove(WRITE));
            assertTrue(options.isEmpty());
            verify(channel, times(1)).size();
            verify(service, times(1)).writeCompletable(channel, size);
            verify(channel, times(1)).force(true);
            verify(channel, times(1)).close();
            assertSame(path, result);
        }
    }
}
