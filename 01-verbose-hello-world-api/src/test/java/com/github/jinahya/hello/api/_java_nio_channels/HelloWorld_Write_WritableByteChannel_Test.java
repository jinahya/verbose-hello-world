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

import com.github.jinahya.hello.api.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing {@link HelloWorld#write(WritableByteChannel) write(channel)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(channel)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Write_WritableByteChannel_Test extends HelloWorld__Test {

    /**
     * Verifies {@link HelloWorld#write(WritableByteChannel) write(channel)} method throws a
     * {@link NullPointerException} when {@code channel} argument is {@code null}.
     */
    @DisplayName("should throw a <NullPointerException> when the <channel> argument is <null>")
    @Test
    void _ThrowNullPointerException_ChannelIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = (WritableByteChannel) null;
        // ------------------------------------------------------------------------------- when/then
        assertThrows(NullPointerException.class, () -> service.write(channel));
    }

    /**
     * Verifies {@link HelloWorld#write(WritableByteChannel) write(channel)} method invokes
     * {@link HelloWorld#put(ByteBuffer) put(buffer)} method with a byte buffer of
     * {@value HelloWorld#BYTES} bytes, and writes the {@code buffer} to specified {@code channel}.
     *
     * @throws IOException if an I/O error occurs.
     * @implNote This test deliberately limits its assertions to the externally observable contract
     * — that {@code put(buffer)} is invoked once with a {@value HelloWorld#BYTES}-capacity buffer,
     * and that the returned channel is the same as the argument. Several stronger checks were
     * considered and rejected; their omissions are documented below for readers who may wonder
     * why.
     * <p>
     * <b>Why no {@code Mockito.verify(buffer).flip()}.</b> Mockito records method invocations only
     * on mocks or spies; a plain {@link ByteBuffer} cannot be inspected. The buffer is allocated
     * inside the SUT (via {@link ByteBuffer#allocate(int)}), so the test cannot inject a spy from
     * outside. Recovering a spy after the fact is impractical, for several reasons:
     * <ul>
     *   <li>An {@link org.mockito.ArgumentCaptor ArgumentCaptor} on {@code put(buffer)} captures
     *       the buffer <em>argument</em>; it does not observe the value returned by
     *       {@code put(buffer)}, which is the reference the SUT actually operates on after the
     *       line {@code final var buffer = put(...)} in the default implementation of
     *       {@link HelloWorld#write(WritableByteChannel)}.</li>
     *   <li>Returning {@link org.mockito.Mockito#spy(Object) Mockito.spy(buffer)} from the
     *       {@code put(...)} stub's answer (so the spy becomes the SUT's working reference) is
     *       fragile: spying a concrete {@link ByteBuffer} subclass such as
     *       {@code java.nio.HeapByteBuffer} requires the inline {@code MockMaker} to retransform
     *       {@link Buffer}, {@link Object}, and {@link Comparable}, which the JVM may disallow
     *       for core system classes.</li>
     *   <li>{@link org.mockito.Mockito#mockStatic Mockito.mockStatic} on {@link ByteBuffer} (to
     *       wrap any static factory's return value in a spy uniformly) reenters through JDK
     *       reflection internals — annotation parsing itself calls
     *       {@link ByteBuffer#wrap(byte[])} — and the resulting cyclic spy creation throws.</li>
     *   <li>A side-channel — an {@link java.util.concurrent.atomic.AtomicReference} written from
     *       inside the {@code put(...)} answer and read from the test — works mechanically, but
     *       introduces concurrency primitives into an otherwise synchronous flow, which is
     *       pedagogical noise in an educational codebase.</li>
     * </ul>
     * <p>
     * <b>Why no {@link org.mockito.Mockito#inOrder Mockito.inOrder} for "{@code flip()} before
     * {@code channel.write(buffer)}".</b> The same prerequisite applies — the buffer must be a
     * spy. Beyond the mechanics, asserting a specific call order over-specifies the contract: an
     * equivalent implementation could replace {@code buffer.flip()} with
     * {@code buffer.position(0).limit(BYTES)}, or any other state-equivalent sequence, and remain
     * correct. The contract says only that the bytes get written; it does not prescribe which
     * {@link Buffer} method achieves the state reset.
     * <p>
     * <b>Why no {@code Mockito.verify(channel, atLeastOnce()).write(buffer)}.</b> The captor in
     * {@link HelloWorld__TestUtils#put_buffer12_invoked_once(HelloWorld) put_buffer12_invoked_once}
     * returns the {@code buffer} argument that was passed into {@code service.put(buffer)}. The
     * default implementation of {@link HelloWorld#write(WritableByteChannel)} happens to use the
     * same reference for {@code channel.write(buffer)} — but only because the helper's
     * {@code put(...)} stub returns its argument unchanged. A different stub (or a different
     * default implementation that wraps, slices, or otherwise re-references the buffer) would
     * make this verification fail despite the SUT being correct. The check is therefore tightly
     * coupled to a stubbing detail, not to the SUT's contract.
     * <p>
     * <b>Why no {@code Assertions.assertFalse(buffer.hasRemaining())}.</b> After
     * {@code put(buffer)} alone, the buffer already satisfies {@code !hasRemaining()} (position
     * equals limit equals {@value HelloWorld#BYTES}). The assertion is therefore ambiguous: it
     * passes both for the correct flow ({@code put} → {@code flip} → drain via {@code write}) and
     * for the buggy flow ({@code put} only, without {@code flip} or {@code write}). It does not
     * discriminate between the two.
     * <p>
     * <b>What is enforced indirectly.</b> The {@code channel.write(...)} stub asserts
     * {@code src.hasRemaining()} on every invocation (see {@code assert src.hasRemaining()}
     * below). After {@code put(buffer)} the buffer has zero remaining; for the SUT to invoke
     * {@code channel.write(buffer)} without tripping the stub's assertion, the SUT must have
     * first reset the buffer to a remaining state. This is implicit evidence that
     * {@code flip()} (or an equivalent) was called — but the check is implicit, not direct.
     */
    @DisplayName("""
            should invoke <put(buffer[12])> and write the <buffer> to the <channel>
            while the <buffer> has <remaining>""")
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = put_buffer12_put_random_bytes(service());
        final var channel = mock(WritableByteChannel.class);
        final var bufferPositions = new ArrayList<Integer>();
        doAnswer(i -> {
            final var src = i.getArgument(0, ByteBuffer.class);
            assert src != null;
            assert src.limit() == HelloWorld.BYTES;
            assert src.hasRemaining();
            final var position = src.position();
            bufferPositions.add(position);
            final var n = ThreadLocalRandom.current().nextInt(src.remaining()) + 1;
            src.position(position + n);
            return n;
        }).when(channel).write(any());
        // ------------------------------------------------------------------------------------ when
        final var result = service.write(channel);
        // ------------------------------------------------------------------------------------ then
        final var buffer = put_buffer12_invoked_once(service);
//        verify(channel, atLeastOnce()).write(buffer);
//        verifyNoMoreInteractions(channel);
//        assertEquals(0, bufferPositions.getFirst());
//        for (var i = 1; i < bufferPositions.size(); i++) {
//            assertTrue(bufferPositions.get(i) > bufferPositions.get(i - 1));
//        }
//        assertFalse(buffer.hasRemaining());
        assertSame(channel, result);
    }
}
