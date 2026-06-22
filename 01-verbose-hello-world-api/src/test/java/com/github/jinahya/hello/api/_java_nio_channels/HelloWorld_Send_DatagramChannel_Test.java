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

import java.io.*;
import java.net.*;
import java.nio.channels.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing {@link HelloWorld#send(DatagramChannel)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("HelloWorld.send(DatagramChannel)")
@Slf4j
class HelloWorld_Send_DatagramChannel_Test extends HelloWorld__Test {

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that the method throws a {@link NullPointerException} when the {@code channel}
     * argument is {@code null}.
     */
    @DisplayName("throws NPE / channel is null")
    @Test
    void _ThrowNullPointerException_ChannelIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = (DatagramChannel) null;
        // ----------------------------------------------------------------------------- when / then
        assertThrows(NullPointerException.class, () -> service.send(channel));
    }

    /**
     * Verifies that the method throws an {@link IllegalArgumentException} when the {@code channel}
     * is not connected.
     */
    @DisplayName("throws IAE / channel not connected")
    @Test
    void _ThrowIllegalArgumentException_ChannelIsNotConnected() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = mock(DatagramChannel.class);
        assert !channel.isConnected();
        // ----------------------------------------------------------------------------- when / then
        assertThrows(IllegalArgumentException.class, () -> service.send(channel));
    }

    /**
     * Verifies that the method delegates to {@code send(socket)} when the {@code channel} is in
     * blocking mode.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("blocking / delegates to send(socket)")
    @Test
    void __ChannelIsBlocking() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        doAnswer(returnsFirstArg()).when(service).<DatagramSocket>send(any());
        final var channel = mock(DatagramChannel.class);
        doReturn(true).when(channel).isConnected();
        doReturn(true).when(channel).isBlocking();
        final var socket = mock(DatagramSocket.class);
        doReturn(socket).when(channel).socket();
        doReturn(channel).when(socket).getChannel();
        // ------------------------------------------------------------------------------------ when
        final var result = service.send(channel);
        // ------------------------------------------------------------------------------------ then
        verify(service, times(1)).send(socket);
        assertSame(channel, result);
    }

    /**
     * Verifies that the method delegates to {@code write(channel)} when the {@code channel} is in
     * non-blocking mode.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("non-blocking / delegates to write(channel)")
    @Test
    void __ChannelIsNotBlocking() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = write_writablebytechannel_returns_channel(service());
        final var channel = mock(DatagramChannel.class);
        doReturn(true).when(channel).isConnected();
        doReturn(false).when(channel).isBlocking();
        // ------------------------------------------------------------------------------------ when
        final var result = service.send(channel);
        // ------------------------------------------------------------------------------------ then
        verify(service, times(1)).write(channel);
        assertSame(channel, result);
    }
}
