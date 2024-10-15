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
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.LongAdder;

/**
 * A class for testing
 * {@link HelloWorld#write(AsynchronousByteChannel, Object, CompletionHandler) write(channel,
 * attachment, handler)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(channel, attachment, handler)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({
        "java:S101"
})
class HelloWorld_07_Write_AsynchronousByteChannelWithHandler_Test extends HelloWorldTest {

    /**
     * Verifies that the
     * {@link HelloWorld#write(AsynchronousByteChannel, Object, CompletionHandler) write(channel,
     * attachment, handler)} method throws a {@link NullPointerException} when the {@code channel}
     * argument is {@code null}.
     */
    @DisplayName("""
            should throw a <NullPointerException>
            when the <channel> argument is <null>"""
    )
    @Test
    @SuppressWarnings({"unchecked"})
    void _ThrowNullPointerException_ChannelIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = (AsynchronousByteChannel) null;
        final var handler = Mockito.mock(CompletionHandler.class);
        final var attachment = (Void) null;
        // ------------------------------------------------------------------------------- when/then
        // assert, <service.write(channel, attachment, handler)> throws a <NullPointerException>
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.write(channel, attachment, handler)
        );
    }

    /**
     * Verifies that the
     * {@link HelloWorld#write(AsynchronousByteChannel, Object, CompletionHandler) write(channel,
     * attachment, handler)} method throws a {@link NullPointerException} when the {@code handler}
     * argument is {@code null}.
     */
    @DisplayName("""
            should throw a <eNullPointerException>
            when the <handler> argument is <null>"""
    )
    @Test
    void _ThrowNullPointerException_HandlerIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = Mockito.mock(AsynchronousByteChannel.class);
        final var handler = (CompletionHandler<AsynchronousByteChannel, Void>) null;
        final var attachment = (Void) null;
        // ------------------------------------------------------------------------------- when/then
        // assert, <service.write(channel, attachment, handler)> throws a <NullPointerException>
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.write(channel, attachment, handler)
        );
    }

    /**
     * Verifies that the
     * {@link HelloWorld#write(AsynchronousByteChannel, Object, CompletionHandler) write(channel,
     * attachment, handler)} method invokes {@link HelloWorld#put(ByteBuffer) put(buffer)} method
     * with a byte buffer of {@value HelloWorld#BYTES} bytes, continuously invokes
     * {@link AsynchronousByteChannel#write(ByteBuffer, Object, CompletionHandler)
     * channel.write(buffer, attachment, a-handler)} method while the {@code buffer} has remaining,
     * and eventually invokes
     * {@link CompletionHandler#completed(Object, Object) handler.completed(channel, attachment)}.
     */
    @DisplayName("""
            should invoke <put(buffer[12])>,
            and write the <buffer> to the <channel> while the <buffer> has remaining"""
    )
    @Test
    @SuppressWarnings({"unchecked"})
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // stub, <service.put(buffer)> will increase <buffer>'s <position> by <12>
        stub_put_buffer_will_increase_buffer_position_by_12();
        // a mock object of <AsynchronousByteChannel>
        final var channel = Mockito.mock(AsynchronousByteChannel.class);
        // the total number of bytes written to the <channel>
        final var written = new LongAdder();
        // stub, <channel.write(src, attachment, handler)> will start a new thread
        //         which increases <src>'s <position> by a random value.
        Mockito.doAnswer(i -> {
            final var src = i.getArgument(0, ByteBuffer.class);
            final var attachment = i.getArgument(1);
            final var handler = i.getArgument(2, CompletionHandler.class);
            // completes, immediately with zero when the <src> has no <remaining>
            if (!src.hasRemaining()) {
                log.warn("<src> has no remaining");
                handler.completed(0, attachment);
                return null;
            }
            // start a new thread which increases <src>'s <position> by a random value.
            Thread.ofPlatform().start(() -> {
                final var result = ThreadLocalRandom.current().nextInt(src.remaining()) + 1;
                src.position(src.position() + result);
                handler.completed(result, attachment);
                written.add(result);
            });
            return null;
        }).when(channel).write(
                ArgumentMatchers.notNull(), // <src>
                ArgumentMatchers.any(),     // <attachment>
                ArgumentMatchers.notNull()  // <handler>
        );
        // an attachment; <null> or non-<null>
        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
        // a mock object of <CompletionHandler> whose methods are logged-out
        final var handler = Mockito.mock(CompletionHandler.class,
                                         Mockito.withSettings().verboseLogging());
        // ------------------------------------------------------------------------------------ when
        service.write(channel, attachment, handler);
        // ------------------------------------------------------------------------------------ then
        // verify, <service.put(buffer[12])> invoked, once
        final var buffer = verify_put_buffer12_invoked_once();
        // verify, <handler.completed(channel, attachment)> invoked, once, within some time.
//        Mockito.verify(handler, Mockito.timeout(TimeUnit.SECONDS.toMillis(1L)).times(1))
//                .completed(channel, attachment);
        // verify, <channel.write(buffer, attachment, same-handler)> invoked, at least once.
//        final var captor = org.mockito.ArgumentCaptor.forClass(CompletionHandler.class);
//        Mockito.verify(channel, Mockito.atLeastOnce()).write(
//                ArgumentMatchers.same(buffer),
//                ArgumentMatchers.any(),
//                captor.capture()
//        );
//        final var handlers = captor.getAllValues();
//        Assertions.assertEquals(1, new java.util.HashSet<>(handlers).size());
        // assert, <buffer> ha no <remaining>
//        Assertions.assertFalse(buffer.hasRemaining());
        // assert, <written.sum()> is equal to <HelloWorld.BYTES>
//        Assertions.assertEquals(HelloWorld.BYTES, written.sum());
    }
}