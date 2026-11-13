package com.github.jinahya.hello.api._javax_net_ssl;


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

import javax.net.ssl.*;
import java.nio.*;
import java.util.concurrent.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing
 * {@link com.github.jinahya.hello.api.HelloWorld#wrap(SSLEngine, ByteBuffer) wrap(engine, dst)}
 * method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.base/javax/net/ssl/SSLEngine.html">javax.net.ssl.SSLEngine</a>
 */
@DisplayName("wrap(engine, dst)")
@Slf4j
class HelloWorld_Wrap_SSLEngine_Dst_Test extends HelloWorld__Test {

    private static SSLEngineResult result(final SSLEngineResult.Status status, final int consumed,
                                          final int produced) {
        return new SSLEngineResult(status, SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING,
                                   consumed, produced);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that the
     * {@link com.github.jinahya.hello.api.HelloWorld#wrap(SSLEngine, ByteBuffer) wrap(engine, dst)}
     * method throws a {@link NullPointerException} when the {@code engine} argument is
     * {@code null}.
     */
    @DisplayName("throws NPE / engine is null")
    @Test
    void _ThrowNullPointerException_EngineIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final SSLEngine engine = null;
        final var dst = ByteBuffer.allocate(HelloWorld.BYTES);
        // ----------------------------------------------------------------------------- when / then
        assertThrows(NullPointerException.class, () -> service.wrap(engine, dst));
    }

    /**
     * Verifies that the
     * {@link com.github.jinahya.hello.api.HelloWorld#wrap(SSLEngine, ByteBuffer) wrap(engine, dst)}
     * method throws a {@link NullPointerException} when the {@code dst} argument is {@code null}.
     */
    @DisplayName("throws NPE / dst is null")
    @Test
    void _ThrowNullPointerException_DstIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var engine = mock(SSLEngine.class);
        final ByteBuffer dst = null;
        // ----------------------------------------------------------------------------- when / then
        assertThrows(NullPointerException.class, () -> service.wrap(engine, dst));
    }

    /**
     * Verifies that the
     * {@link com.github.jinahya.hello.api.HelloWorld#wrap(SSLEngine, ByteBuffer) wrap(engine, dst)}
     * method keeps invoking {@link SSLEngine#wrap(ByteBuffer, ByteBuffer) engine.wrap(src, dst)},
     * across one or more partial consumptions, until all {@value HelloWorld#BYTES} bytes are
     * consumed, and returns the {@code engine}.
     *
     * @throws SSLException if an SSL error occurs.
     */
    @DisplayName("happy path")
    @Test
    void __() throws SSLException {
        // ----------------------------------------------------------------------------------- given
        final var service = put_buffer12_increases_buffer_position_by_12(service());
        final var engine = mock(SSLEngine.class);
        final var dst = ByteBuffer.allocate(HelloWorld.BYTES << 1);
        doAnswer(w -> {
            final var src = w.getArgument(0, ByteBuffer.class);
            final var n = ThreadLocalRandom.current().nextInt(src.remaining()) + 1;
            src.position(src.position() + n);
            w.getArgument(1, ByteBuffer.class).position(dst.position() + n);
            return result(SSLEngineResult.Status.OK, n, n);
        }).when(engine).wrap(any(ByteBuffer.class), same(dst));
        // ------------------------------------------------------------------------------------ when
        final var result = service.wrap(engine, dst);
        // ------------------------------------------------------------------------------------ then
        final var buffer = put_buffer12_invoked_once(service());
        verify(engine, atLeastOnce()).wrap(same(buffer), same(dst));
        verifyNoMoreInteractions(engine);
        assertFalse(buffer.hasRemaining());
        assertEquals(HelloWorld.BYTES, dst.position());
        assertSame(engine, result);
    }

    /**
     * Verifies that the
     * {@link com.github.jinahya.hello.api.HelloWorld#wrap(SSLEngine, ByteBuffer) wrap(engine, dst)}
     * method throws a {@link BufferOverflowException} when the {@code engine} reports
     * {@link SSLEngineResult.Status#BUFFER_OVERFLOW BUFFER_OVERFLOW}.
     *
     * @throws SSLException if an SSL error occurs.
     */
    @DisplayName("throws BufferOverflowException / BUFFER_OVERFLOW")
    @Test
    void _ThrowBufferOverflowException_BufferOverflow() throws SSLException {
        // ----------------------------------------------------------------------------------- given
        final var service = put_buffer12_increases_buffer_position_by_12(service());
        final var engine = mock(SSLEngine.class);
        doReturn(result(SSLEngineResult.Status.BUFFER_OVERFLOW, 0, 0))
                .when(engine).wrap(any(ByteBuffer.class), any(ByteBuffer.class));
        final var dst = ByteBuffer.allocate(0);
        // ----------------------------------------------------------------------------- when / then
        assertThrows(BufferOverflowException.class, () -> service.wrap(engine, dst));
    }

    /**
     * Verifies that the
     * {@link com.github.jinahya.hello.api.HelloWorld#wrap(SSLEngine, ByteBuffer) wrap(engine, dst)}
     * method throws an {@link SSLException} when the {@code engine} reports
     * {@link SSLEngineResult.Status#CLOSED CLOSED}.
     *
     * @throws SSLException if an SSL error occurs.
     */
    @DisplayName("throws SSLException / CLOSED")
    @Test
    void _ThrowSSLException_Closed() throws SSLException {
        // ----------------------------------------------------------------------------------- given
        final var service = put_buffer12_increases_buffer_position_by_12(service());
        final var engine = mock(SSLEngine.class);
        doReturn(result(SSLEngineResult.Status.CLOSED, 0, 0))
                .when(engine).wrap(any(ByteBuffer.class), any(ByteBuffer.class));
        final var dst = ByteBuffer.allocate(HelloWorld.BYTES);
        // ----------------------------------------------------------------------------- when / then
        assertThrows(SSLException.class, () -> service.wrap(engine, dst));
    }

    /**
     * Verifies that the
     * {@link com.github.jinahya.hello.api.HelloWorld#wrap(SSLEngine, ByteBuffer) wrap(engine, dst)}
     * method throws an {@link SSLException}, rather than looping forever, when the {@code engine}
     * reports {@link SSLEngineResult.Status#OK OK} without consuming any bytes; e.g., while it is
     * still handshaking.
     *
     * @throws SSLException if an SSL error occurs.
     */
    @DisplayName("throws SSLException / no bytes consumed")
    @Test
    void _ThrowSSLException_NoBytesConsumed() throws SSLException {
        // ----------------------------------------------------------------------------------- given
        final var service = put_buffer12_increases_buffer_position_by_12(service());
        final var engine = mock(SSLEngine.class);
        doReturn(result(SSLEngineResult.Status.OK, 0, 64))
                .when(engine).wrap(any(ByteBuffer.class), any(ByteBuffer.class));
        final var dst = ByteBuffer.allocate(HelloWorld.BYTES << 3);
        // ----------------------------------------------------------------------------- when / then
        assertThrows(SSLException.class, () -> service.wrap(engine, dst));
    }
}
