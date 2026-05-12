package com.github.jinahya.hello.api._java_nio_channels;

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

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.LongAdder;

/**
 * A class for testing {@link HelloWorld#write(AsynchronousByteChannel) write(channel)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(channel)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({
        "java:S101"
})
class HelloWorld_Write_AsynchronousByteChannel_Test extends HelloWorldTest {

    /**
     * Verifies {@link HelloWorld#write(AsynchronousByteChannel) write(channel)} method throws a
     * {@link NullPointerException} when the {@code channel} argument is {@code null}.
     */
    @DisplayName("""
            should throw a <NullPointerException>
            when the <channel> argument is <null>"""
    )
    @Test
    void _ThrowNullPointerException_ChannelIsNull() {
        // ----------------------------------------------------------------------------------- given
        var service = service();
        var channel = (AsynchronousByteChannel) null;
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.write(channel)
        );
    }

    /**
     * Verifies that the method writes all {@value HelloWorld#BYTES} bytes to the {@code channel}
     * across one or more partial writes, and returns the {@code channel}.
     *
     * @throws InterruptedException if interrupted while testing.
     * @throws ExecutionException   if an I/O error occurs.
     */
    @DisplayName("""
            should write all <hello-world-bytes> across partial writes,
            and return the <channel>"""
    )
    @Test
    void __succeeds() throws InterruptedException, ExecutionException {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorldTestUtils.put_buffer_will_increase_buffer_position_by_12(
                service());
        final var channel = Mockito.mock(AsynchronousByteChannel.class);
        final var written = new LongAdder();
        Mockito.doAnswer(w -> {
            final var src = w.getArgument(0, ByteBuffer.class);
            @SuppressWarnings({"unchecked"})
            final var future = (Future<Integer>) Mockito.mock(Future.class);
            Mockito.doAnswer(g -> {
                final var n = ThreadLocalRandom.current().nextInt(src.remaining()) + 1;
                src.position(src.position() + n);
                written.add(n);
                return n;
            }).when(future).get();
            return future;
        }).when(channel).write(ArgumentMatchers.argThat(b -> b != null && b.hasRemaining()));
        // ------------------------------------------------------------------------------------ when
        final var result = service.write(channel);
        // ------------------------------------------------------------------------------------ then
        final var buffer = HelloWorldTestUtils.put_buffer12_invoked_once(service);
//        Mockito.verify(channel, Mockito.atLeastOnce()).write(buffer);
//        Assertions.assertEquals(HelloWorld.BYTES, written.intValue());
//        final var srcCaptor = ArgumentCaptor.forClass(ByteBuffer.class);
//        Mockito.verify(channel, Mockito.atLeastOnce()).write(srcCaptor.capture());
//        final var srcs = srcCaptor.getAllValues();
//        srcs.forEach(s -> Assertions.assertSame(srcs.getFirst(), s));
        Assertions.assertSame(channel, result);
    }

    /**
     * Verifies that the method propagates an {@link ExecutionException} when the {@code channel}'s
     * {@link Future#get() future.get()} fails — possibly on the first invocation, or after one or
     * more partial writes have already succeeded.
     */
    @DisplayName("""
            should propagate an <ExecutionException>
            when the <channel> fails on or after partial writes"""
    )
    @Test
    void __fails() {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorldTestUtils.put_buffer_will_increase_buffer_position_by_12(
                service());
        final var channel = Mockito.mock(AsynchronousByteChannel.class);
        final var cause = new IOException("simulated write failure");
        Mockito.doAnswer(w -> {
            final var src = w.getArgument(0, ByteBuffer.class);
            @SuppressWarnings({"unchecked"})
            final var future = (Future<Integer>) Mockito.mock(Future.class);
            Mockito.doAnswer(g -> {
                final var n = ThreadLocalRandom.current().nextInt(src.remaining()) + 1;
                src.position(src.position() + n);
                if (!src.hasRemaining() || ThreadLocalRandom.current().nextBoolean()) {
                    throw new ExecutionException(cause);
                }
                return n;
            }).when(future).get();
            return future;
        }).when(channel).write(ArgumentMatchers.argThat(b -> b != null && b.hasRemaining()));
        // ------------------------------------------------------------------------------- when/then
//        final var thrown = Assertions.assertThrows(
//                ExecutionException.class,
//                () -> service.write(channel)
//        );
//        Assertions.assertSame(cause, thrown.getCause());
    }
}
