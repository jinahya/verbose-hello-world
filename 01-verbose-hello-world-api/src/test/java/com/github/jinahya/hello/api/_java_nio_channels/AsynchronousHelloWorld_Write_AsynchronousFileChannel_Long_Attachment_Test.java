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

import java.nio.channels.*;
import java.util.concurrent.*;
import java.util.function.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing
 * {@link AsynchronousHelloWorld#write(AsynchronousFileChannel, long, Object) write(channel,
 * position, attachment)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(channel, position, attachment)")
@Slf4j
abstract class AsynchronousHelloWorld_Write_AsynchronousFileChannel_Long_Attachment_Test<
        T extends AsynchronousHelloWorld<HelloWorld>
        >
        extends AsynchronousHelloWorld__Test<HelloWorld, T> {

    AsynchronousHelloWorld_Write_AsynchronousFileChannel_Long_Attachment_Test(
            final Function<? super HelloWorld, ? extends T> initializer) {
        super(HelloWorld.class, initializer);
    }

    /**
     * Verifies that the method throws a {@link NullPointerException} when the {@code channel}
     * argument is {@code null}.
     */
    @DisplayName("should throw a <NullPointerException> when the <channel> argument is <null>")
    @Test
    void _ThrowNullPointerException_ChannelIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = asynchronousService();
        final var channel = (AsynchronousFileChannel) null;
        final var position = 0L;
        // ----------------------------------------------------------------------------- when / then
        assertThrows(NullPointerException.class, () -> service.write(channel, position, null));
    }

    /**
     * Verifies that the method throws an {@link IllegalArgumentException} when the {@code position}
     * argument is negative.
     */
    @DisplayName(
            "should throw an <IllegalArgumentException> when the <position> argument is <negative>")
    @Test
    void _ThrowIllegalArgumentException_PositionIsNegative() {
        // ----------------------------------------------------------------------------------- given
        final var service = asynchronousService();
        final var channel = mock(AsynchronousFileChannel.class);
        final var position = ThreadLocalRandom.current().nextLong() | Long.MIN_VALUE;
        // ----------------------------------------------------------------------------- when / then
        assertThrows(IllegalArgumentException.class, () -> service.write(channel, position, null));
    }

    /**
     * Verifies that the returned {@link java.util.concurrent.CompletionStage stage} completes with
     * the supplied {@code attachment} once all {@value HelloWorld#BYTES} bytes have been written.
     *
     * @throws Exception if an error occurs.
     */
    @DisplayName("""
            should complete the returned stage with the <attachment>
            once all <hello-world-bytes> have been written""")
    @Test
    @SuppressWarnings({"unchecked"})
    void __completed() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var service = asynchronousService();
        final var channel = mock(AsynchronousFileChannel.class);
        final var position = ThreadLocalRandom.current().nextLong(1024L);
        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
        doAnswer(i -> {
            final var handler = i.getArgument(3, CompletionHandler.class);
            handler.completed(channel, attachment);
            return null;
        }).when(service).write(same(channel), eq(position), same(attachment), notNull());
        // ------------------------------------------------------------------------------------ when
        final var result = service.write(channel, position, attachment);
        // ------------------------------------------------------------------------------------ then
        assertSame(attachment, result.toCompletableFuture().get(8L, TimeUnit.SECONDS));
        verify(service, times(1)).write(same(channel), eq(position), same(attachment), notNull());
    }

    /**
     * Verifies that the returned {@link java.util.concurrent.CompletionStage stage} completes
     * exceptionally when the {@code channel} fails — possibly synchronously on the first
     * invocation, or asynchronously after one or more partial writes.
     */
    @DisplayName("""
            should complete the returned stage exceptionally
            when the <channel> fails on or after partial writes""")
    @Test
    @SuppressWarnings({"unchecked"})
    void __failed() {
        // ----------------------------------------------------------------------------------- given
        final var service = asynchronousService();
        final var channel = mock(AsynchronousFileChannel.class);
        final var exc = new RuntimeException("simulated write failure");
        final var position = ThreadLocalRandom.current().nextLong(1024L);
        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
        doAnswer(i -> {
            final var handler = i.getArgument(3, CompletionHandler.class);
            handler.failed(exc, attachment);
            return null;
        }).when(service).write(same(channel), eq(position), same(attachment), notNull());
        // ------------------------------------------------------------------------------------ when
        final var result = service.write(channel, position, attachment);
        // ------------------------------------------------------------------------------------ then
//        assertSame(attachment, result.toCompletableFuture().get(8L, TimeUnit.SECONDS)); // should be remained as commented-out
        final var thrown = assertThrows(
                ExecutionException.class,
                () -> result.toCompletableFuture().get(8L, TimeUnit.SECONDS)
        );
        assertSame(exc, thrown.getCause());
        verify(service, times(1)).write(same(channel), eq(position), same(attachment), notNull());
    }
}
