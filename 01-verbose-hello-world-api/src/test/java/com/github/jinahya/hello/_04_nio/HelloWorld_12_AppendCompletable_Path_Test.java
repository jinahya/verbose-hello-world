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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

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
                    var src = ByteBuffer.allocate(HelloWorld.BYTES);
                    channel.write(
                            src,
                            position,
                            position,
                            new CompletionHandler<>() { // @formatter:off
                                @Override
                                public void completed(final Integer result, Long attachment) {
                                    if (!src.hasRemaining()) {
                                        future.complete(channel);
                                        return;
                                    }
                                    attachment += result;
                                    channel.write(src, attachment, attachment, this);
                                }
                                @Override
                                public void failed(final Throwable exc, final Long attachment) {
                                    future.completeExceptionally(exc);
                                } // @formatter:on
                            });
                    return future;
                })
                .given(service())
                .writeCompletable(ArgumentMatchers.notNull(),
                                  ArgumentMatchers.longThat(v -> v >= 0L));
    }

    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        var service = service();
        var path = Mockito.mock(Path.class);
        var channel = Mockito.mock(AsynchronousFileChannel.class);
        _stub_ToComplete(channel, null);
        var size = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE - HelloWorld.BYTES);
        BDDMockito.given(channel.size()).willReturn(size);
        try (var mockedStatic = Mockito.mockStatic(AsynchronousFileChannel.class)) {
            mockedStatic.when(() -> AsynchronousFileChannel.open(
                            ArgumentMatchers.same(path),
                            ArgumentMatchers.any(OpenOption[].class)))
                    .thenReturn(channel);
            // -------------------------------------------------------------------------------- when
            final var result = service.appendCompletable(path).join();
            // -------------------------------------------------------------------------------- then
            var optionsCaptor = ArgumentCaptor.forClass(OpenOption[].class);
            mockedStatic.verify(() -> AsynchronousFileChannel.open(ArgumentMatchers.same(path),
                                                                   optionsCaptor.capture()),
                                Mockito.times(1));
            var options = new ArrayList<>(Arrays.asList(optionsCaptor.getValue()));
            Assertions.assertTrue(options.remove(StandardOpenOption.CREATE));
            Assertions.assertTrue(options.remove(StandardOpenOption.WRITE));
            Assertions.assertTrue(options.isEmpty());
            Mockito.verify(channel, Mockito.times(1)).size();
            Mockito.verify(service, Mockito.times(1)).writeCompletable(channel, size);
            Mockito.verify(channel, Mockito.times(1)).force(true);
            Mockito.verify(channel, Mockito.times(1)).close();
            Assertions.assertSame(path, result);
        }
    }
}
