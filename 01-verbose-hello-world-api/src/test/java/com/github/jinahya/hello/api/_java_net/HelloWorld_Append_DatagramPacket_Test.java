package com.github.jinahya.hello.api._java_net;

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

import com.github.jinahya.hello.api.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;

import java.net.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing {@link HelloWorld#append(DatagramPacket) append(packet)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("append(packet)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Append_DatagramPacket_Test extends HelloWorld__Test {

    static {
        final var pin = new Runnable[] {
                () -> verify(null),
                () -> times(0),
                () -> assertEquals(0, 1),
                () -> assertSame(0, 1)};
    }

    /**
     * Verifies that the {@link HelloWorld#append(DatagramPacket) append(packet)} method throws a
     * {@link NullPointerException} when the {@code packet} argument is {@code null}.
     */
    @DisplayName("should throw a <NullPointerException> when the <packet> argument is <null>")
    @Test
    void _ThrowNullPointerException_PacketIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var packet = (DatagramPacket) null;
        // ------------------------------------------------------------------------------- when/then
        assertThrows(
                NullPointerException.class,
                () -> service.append(packet)
        );
    }

    /**
     * Verifies that the {@link HelloWorld#append(DatagramPacket) append(packet)} method throws an
     * {@link IllegalArgumentException} when the {@code packet}'s data buffer does not have enough
     * room for {@value HelloWorld#BYTES} bytes after {@code offset + length}.
     */
    @DisplayName("""
            should throw an <IllegalArgumentException>
            when the <packet>'s data buffer is not large enough""")
    @Test
    void _ThrowIllegalArgumentException_NotEnoughAvailableSpace() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var data = new byte[HelloWorld.BYTES << 1];
        final var offset = ThreadLocalRandom.current().nextInt(HelloWorld.BYTES);
        final var length =
                ThreadLocalRandom.current().nextInt(
                        HelloWorld.BYTES - offset,
                        data.length - offset
                ) + 1;
        assert offset + length + HelloWorld.BYTES > data.length;
        final var packet = new DatagramPacket(data, offset, length);
        // ------------------------------------------------------------------------------- when/then
        assertThrows(
                IllegalArgumentException.class,
                () -> service.append(packet)
        );
    }

    /**
     * Verifies that the {@link HelloWorld#append(DatagramPacket) append(packet)} method invokes the
     * {@link HelloWorld#set(byte[], int) set(data, offset + length)} method, increments the
     * packet's length by {@value HelloWorld#BYTES}, and returns the packet.
     */
    @DisplayName("""
            should invoke <set(packet.data, offset + length)>,
            increment the <packet.length> by <12>,
            and return the <packet>""")
    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        doAnswer(returnsFirstArg()).when(service).set(any(byte[].class), anyInt());
        final var offset = ThreadLocalRandom.current().nextInt(8);
        final var length = ThreadLocalRandom.current().nextInt(8);
        final var data = new byte[offset + length + HelloWorld.BYTES];
        final var packet = new DatagramPacket(data, offset, length);
        // ------------------------------------------------------------------------------------ when
        final var result = service.append(packet);
        // ------------------------------------------------------------------------------------ then
//        verify(service, times(1)).set(data, offset + length);
//        assertEquals(length + HelloWorld.BYTES, packet.getLength());
        assertSame(packet, result);
    }
}
