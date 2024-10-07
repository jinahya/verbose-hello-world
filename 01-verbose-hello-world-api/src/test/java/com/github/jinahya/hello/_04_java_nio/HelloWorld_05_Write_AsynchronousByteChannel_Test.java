package com.github.jinahya.hello._04_java_nio;

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
import com.github.jinahya.hello.HelloWorldTest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
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
class HelloWorld_05_Write_AsynchronousByteChannel_Test extends HelloWorldTest {

    /**
     * Verifies {@link HelloWorld#write(AsynchronousByteChannel) write(channel)} method throws a
     * {@link NullPointerException} when the {@code channel} argument is {@code null}.
     */
    @DisplayName("""
            should throw a NullPointerException
            when the <channel> argument is <null>"""
    )
    @Test
    void _ThrowNullPointerException_ChannelIsNull() {
        // ----------------------------------------------------------------------------------- given
        var service = service();
        var channel = (AsynchronousByteChannel) null;
        // ------------------------------------------------------------------------------- when/then
        // assert, <service.write(channel)> will throw a NullPointerException
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.write(channel)
        );
    }

    /**
     * Verifies {@link HelloWorld#write(AsynchronousByteChannel) write(channel)} method invokes
     * {@link HelloWorld#put(ByteBuffer) put(buffer)} method with a byte buffer of
     * {@value HelloWorld#BYTES} bytes, and writes the buffer to specified {@code channel}.
     *
     * @throws InterruptedException if interrupted while testing.
     * @throws ExecutionException   if failed to execute.
     */
    @DisplayName("""
            should invoke put(buffer[12])
            and write the <buffer> to the <channel> while the the <buffer> has remaining"""
    )
    @Test
    void __() throws InterruptedException, ExecutionException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // stub, <service.put(buffer)> will increase <buffer>'s <position> by <HelloWorld.BYTES>
        Mockito.doAnswer(i -> {
                    var buffer = i.getArgument(0, ByteBuffer.class);
                    buffer.position(buffer.position() + HelloWorld.BYTES);
                    return buffer;
                })
                .when(service)
                .put(ArgumentMatchers.argThat(b -> b != null && b.remaining() >= HelloWorld.BYTES));
        final var writtenSoFar = new LongAdder();
        final var channel = Mockito.mock(AsynchronousByteChannel.class);
        // stub, <channel.write(buffer)> will return a <future> drains the <buffer>
        final var futureReference = new AtomicReference<Future<Integer>>();
        Mockito.doAnswer(w -> {
            // preceding future's get() should be invoked
            final var previousFuture = futureReference.get();
            if (previousFuture != null) {
                Mockito.verify(previousFuture, Mockito.times(1)).get();
            }
            final var src = w.getArgument(0, ByteBuffer.class);
            @SuppressWarnings({"unchecked"})
            final var future = (Future<Integer>) Mockito.mock(Future.class);
            // stub, <future.get()> will increase <buffer>'s <position> by a random value
            Mockito.doAnswer(g -> {
                final var result = ThreadLocalRandom.current().nextInt(src.remaining()) + 1;
                src.position(src.position() + result);
                writtenSoFar.add(result);
                return result;
            }).when(future).get();
            futureReference.set(future);
            return future;
        }).when(channel).write(ArgumentMatchers.argThat(b -> b != null && b.hasRemaining()));
        // ------------------------------------------------------------------------------------ when
        final var result = service.write(channel);
        // ------------------------------------------------------------------------------------ then
        // verify, <service.put(buffer[12])> invoked, once
        final var buffer = verify_put_buffer12_invoked_once();
        // verify, <channel.write(buffer)> invoked, at least once

        // assert, <buffer> has no <remaining>

        // assert, total number of bytes written is equal to <HelloWorld.BYTES>

        // assert, <result> is same as <channel>
        Assertions.assertSame(channel, result);
    }
}
