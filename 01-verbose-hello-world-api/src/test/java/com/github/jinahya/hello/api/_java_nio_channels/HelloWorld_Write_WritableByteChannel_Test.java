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
import com.github.jinahya.hello.api.MockitoUtils;
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
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.LongAdder;

/**
 * A class for testing {@link HelloWorld#write(WritableByteChannel) write(channel)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(channel)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Write_WritableByteChannel_Test
        extends HelloWorldTest {

    /**
     * Verifies {@link HelloWorld#write(WritableByteChannel) write(channel)} method throws a
     * {@link NullPointerException} when {@code channel} argument is {@code null}.
     */
    @DisplayName("""
            should throw a <NullPointerException>
            when the <channel> argument is <null>"""
    )
    @Test
    void _ThrowNullPointerException_ChannelIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = (WritableByteChannel) null;
        // ------------------------------------------------------------------------------- when/then
        // assert: <service.write(channel)> throws a <NullPointerException>
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.write(channel)
        );
    }

    /**
     * Verifies {@link HelloWorld#write(WritableByteChannel) write(channel)} method invokes
     * {@link HelloWorld#put(ByteBuffer) put(buffer)} method with a byte buffer of
     * {@value HelloWorld#BYTES} bytes, and writes the {@code buffer} to specified {@code channel}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("""
            should invoke <put(buffer[12])>
            and writes the <buffer> to the <channel> while the <buffer> has <remaining>"""
    )
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorldTestUtils
                .put_buffer_will_increase_buffer_position_by_12(service());
        final var channel = Mockito.mock(WritableByteChannel.class);
        Mockito.doAnswer(i -> {
            final var src = i.getArgument(0, ByteBuffer.class);
            assert src.hasRemaining();
            final var w = ThreadLocalRandom.current().nextInt(src.remaining() + 1);
            src.position(src.position() + w);
            return w;
        }).when(channel).write(ArgumentMatchers.notNull());
        // ------------------------------------------------------------------------------------ when
        final var result = service.write(channel);
        // ------------------------------------------------------------------------------------ then
        final var buffer = HelloWorldTestUtils.put_buffer12_invoked_once(service);
//        Mockito.verify(channel, Mockito.atLeastOnce()).write(buffer);
//        Assertions.assertFalse(buffer.hasRemaining());
        Assertions.assertSame(channel, result);
    }
}
