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

import static com.github.jinahya.hello.api.HelloWorldTestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing {@link HelloWorld#send(DatagramChannel)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorld_Send_DatagramChannel_Test extends HelloWorldTest {

    // ---------------------------------------------------------------------------------------------
    @DisplayName("(null)NullPointerException")
    @Test
    void _ThrowNullPointerException_ChannelIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = (DatagramChannel) null;
        // ----------------------------------------------------------------------------- when / then
        assertThrows(NullPointerException.class, () -> service.send(channel));
    }

    @DisplayName("(!connected)IllegalArgumentException")
    @Test
    void _ThrowIllegalArgumentException_ChannelIsNotConnected() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = mock(DatagramChannel.class);
        assert !channel.isConnected();
        // ----------------------------------------------------------------------------- when / then
        assertThrows(IllegalArgumentException.class, () -> service.send(channel));
    }

    @Test
    void __ChannelIsBlocking() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        doAnswer(returnsFirstArg()).when(service).<DatagramSocket>send(any());
        final var channel = mock(DatagramChannel.class);
        when(channel.isConnected()).thenReturn(true);
        when(channel.isBlocking()).thenReturn(true);
        final var socket = mock(DatagramSocket.class);
        when(channel.socket()).thenReturn(socket);
        when(socket.getChannel()).thenReturn(channel);
        // ------------------------------------------------------------------------------------ when
        final var result = service.send(channel);
        // ------------------------------------------------------------------------------------ then
        verify(service, times(1)).send(socket);
        assertSame(channel, result);
    }

    @Test
    void __ChannelIsNotBlocking() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = write_writablebytechannel_returns_channel(service());
        final var channel = mock(DatagramChannel.class);
        when(channel.isConnected()).thenReturn(true);
        when(channel.isBlocking()).thenReturn(false);
        // ------------------------------------------------------------------------------------ when
        final var result = service.send(channel);
        // ------------------------------------------------------------------------------------ then
        verify(service, times(1)).write(channel);
        assertSame(channel, result);
    }
}
