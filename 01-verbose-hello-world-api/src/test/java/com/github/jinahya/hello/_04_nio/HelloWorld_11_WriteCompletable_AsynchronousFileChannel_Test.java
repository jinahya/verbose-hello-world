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
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.LongAdder;

/**
 * A class for testing
 * {@link HelloWorld#writeCompletable(AsynchronousFileChannel, long) writeCompletable(channel,
 * position)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_11_WriteCompletable_AsynchronousFileChannel_Arguments_Test
 */
@DisplayName("write(channel, position)")
@Slf4j
class HelloWorld_11_WriteCompletable_AsynchronousFileChannel_Test extends _HelloWorldTest {

    @BeforeEach
    @SuppressWarnings({"unchecked"})
    void _beforeEach() {
        BDDMockito.willAnswer(i -> {
            var channel = i.getArgument(0, AsynchronousFileChannel.class);
            var position = i.getArgument(1, Long.class);
            var handler = i.getArgument(2, CompletionHandler.class);
            var attachment = i.getArgument(3);
            var src = ByteBuffer.allocate(HelloWorld.BYTES);
            channel.write(src, position, position, new CompletionHandler<>() {
                @Override
                public void completed(Integer result, Long attachment_) {
                    if (!src.hasRemaining()) {
                        handler.completed(channel, attachment);
                        return;
                    }
                    attachment_ += result;
                    channel.write(src, attachment_, attachment_, this);
                }

                @Override
                public void failed(Throwable exc, Long attachment_) {
                    handler.failed(exc, attachment);
                }
            });
            return null;
        }).given(service()).writeAsync(
                ArgumentMatchers.notNull(),
                ArgumentMatchers.longThat(v -> v >= 0L),
                ArgumentMatchers.notNull(),
                ArgumentMatchers.any()
        );
    }

    /**
     * Verifies
     * {@link HelloWorld#writeCompletable(AsynchronousFileChannel, long) writeCompletable(channel,
     * position)} method returns a completable future results the {@code channel}.
     */
    @DisplayName("(channel, position)completed<channel>")
    @Test
    void _Completed_() throws CancellationException, CompletionException {
        // ----------------------------------------------------------------------------------- given
        var service = service();
        var channel = Mockito.mock(AsynchronousFileChannel.class);
        var writtenSoFar = new LongAdder();
        _stub_ToComplete(channel, writtenSoFar);
        var position = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE - HelloWorld.BYTES);
        // ------------------------------------------------------------------------------------ when
        var future = service.writeCompletable(channel, position);
        // ------------------------------------------------------------------------------------ then
        Assertions.assertNotNull(future);
        var result = future.handle((r, t) -> {
            assert r != null;
            assert t == null;
            return r;
        }).join();
        Mockito.verify(service, Mockito.times(1)).writeAsync(
                ArgumentMatchers.same(channel),
                ArgumentMatchers.eq(position),
                ArgumentMatchers.notNull(),
                ArgumentMatchers.any()
        );
        Assertions.assertSame(channel, result);
    }

    /**
     * Verifies {@link HelloWorld#writeCompletable(AsynchronousByteChannel) writeAsync(channel)}
     * method returns a completable future being completed exceptionally.
     */
    @DisplayName("(channel, position)completedExceptionally")
    @Test
    void _CompletedExceptionally_() {
        // ----------------------------------------------------------------------------------- given
        var service = service();
        var channel = Mockito.mock(AsynchronousFileChannel.class);
        var exc = _stub_ToFail(channel, Mockito.mock(Throwable.class));
        var position = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE - HelloWorld.BYTES);
        // ------------------------------------------------------------------------------------ when
        var future = service.writeCompletable(channel, position);
        // ------------------------------------------------------------------------------------ then
        Assertions.assertNotNull(future);
        Assertions.assertThrows(CompletionException.class, future::join);
    }
}
