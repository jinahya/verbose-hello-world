package com.github.jinahya.hello.api;

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

import java.net.*;
import java.nio.*;
import java.nio.charset.*;
import java.util.*;
import java.util.function.*;

import static com.github.jinahya.hello.api.HelloWorld.*;

/**
 * Convenience methods that produce the
 * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> in several common Java I/O
 * shapes — {@code byte[]}, {@link ByteBuffer}, {@link String}, {@link CharBuffer}, and
 * {@link DatagramPacket} — by delegating to the specified {@link HelloWorld} service.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public final class HelloWorldUtils {

    /**
     * Returns a fresh {@value HelloWorld#BYTES}-byte array containing the
     * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, produced by invoking
     * {@link HelloWorld#set(byte[]) set(byte[])} on the specified service.
     *
     * @param service the {@link HelloWorld} service that produces the bytes.
     * @return a new {@code byte[]} of length {@value HelloWorld#BYTES} containing the
     * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>.
     * @throws NullPointerException if the {@code service} is {@code null}.
     * @see HelloWorld#set(byte[])
     */
    public static byte[] array(final HelloWorld service) {
        Objects.requireNonNull(service, "service is null");
        return service.set(new byte[BYTES]);
    }

    /**
     * Returns a new {@value HelloWorld#BYTES}-byte {@link ByteBuffer} on which the
     * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> are put, produced by
     * invoking {@link HelloWorld#put(ByteBuffer) put(ByteBuffer)} on the specified service with a
     * freshly heap-allocated {@value HelloWorld#BYTES}-byte buffer.
     * <p>
     * The buffer is returned as {@link HelloWorld#put(ByteBuffer) put(ByteBuffer)} leaves it; it
     * is
     * <em>not</em> {@linkplain ByteBuffer#flip() flipped}. Invoke {@link ByteBuffer#flip() flip()}
     * on the result to read the bytes.
     *
     * @param service the {@link HelloWorld} service that produces the bytes.
     * @return the {@link ByteBuffer} returned by
     * {@link HelloWorld#put(ByteBuffer) put(ByteBuffer)}.
     * @throws NullPointerException if the {@code service} is {@code null}.
     * @see HelloWorld#put(ByteBuffer)
     */
    public static ByteBuffer buffer(final HelloWorld service) {
        Objects.requireNonNull(service, "service is null");
        return service.put(ByteBuffer.allocate(BYTES));
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns a new {@link String} containing the
     * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> decoded as
     * {@link StandardCharsets#US_ASCII US-ASCII}, produced by
     * {@linkplain #array(HelloWorld) obtaining the byte array} from the specified service.
     *
     * @param service the {@link HelloWorld} service that produces the bytes.
     * @return a new {@value HelloWorld#BYTES}-character {@link String} containing the
     * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>.
     * @throws NullPointerException if the {@code service} is {@code null}.
     * @see #array(HelloWorld)
     */
    public static String string(final HelloWorld service) {
        Objects.requireNonNull(service, "service is null");
        return new String(array(service), StandardCharsets.US_ASCII);
    }

    /**
     * Returns a new {@link CharBuffer} containing the
     * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> decoded by the specified
     * decoder, produced by {@linkplain #buffer(HelloWorld) obtaining the byte buffer} from the
     * specified service, {@linkplain ByteBuffer#flip() flipping} it, and passing it to
     * {@link CharsetDecoder#decode(ByteBuffer) decoder.decode(buffer)}.
     * <p>
     * The {@link CharsetDecoder#decode(ByteBuffer) decode(buffer)} method resets the decoder before
     * decoding, so the same decoder may be reused for sequential invocations. A decoder is not
     * thread-safe, though; do not share one among concurrent invocations. Whether malformed or
     * unmappable input is reported, replaced, or ignored is up to the decoder's
     * {@linkplain CharsetDecoder#malformedInputAction() actions}.
     * <p>
     * The result reads {@code "hello, world"} only when the decoder's charset is ASCII-compatible
     * (e.g., {@link StandardCharsets#US_ASCII US-ASCII},
     * {@link StandardCharsets#ISO_8859_1 ISO-8859-1}, or {@link StandardCharsets#UTF_8 UTF-8}).
     *
     * @param service the {@link HelloWorld} service that produces the bytes.
     * @param decoder the decoder with which the bytes are decoded.
     * @return a new {@link CharBuffer} containing the decoded
     * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>.
     * @throws NullPointerException     if the {@code service} is {@code null}, or the
     *                                  {@code decoder} is {@code null}.
     * @throws CharacterCodingException if the {@code decoder} reports a decoding error.
     * @see #buffer(HelloWorld)
     * @see CharsetDecoder#decode(ByteBuffer)
     */
    public static CharBuffer decode(final HelloWorld service, final CharsetDecoder decoder)
            throws CharacterCodingException {
        Objects.requireNonNull(decoder, "decoder is null");
        return decoder.decode(buffer(service).flip());
    }

    /**
     * Returns a new {@link CharBuffer} containing the
     * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> decoded in the specified
     * charset, produced by invoking
     * {@link #decode(HelloWorld, CharsetDecoder) decode(service, decoder)} with a
     * {@linkplain Charset#newDecoder() new decoder} of the {@code charset}.
     * <p>
     * Unlike {@link Charset#decode(ByteBuffer) charset.decode(buffer)}, which replaces malformed or
     * unmappable input, a new decoder <em>reports</em> it, so this method throws a
     * {@link CharacterCodingException} instead of returning replacement characters.
     *
     * @param service the {@link HelloWorld} service that produces the bytes.
     * @param charset the charset in which the bytes are decoded.
     * @return a new {@link CharBuffer} containing the decoded
     * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>.
     * @throws NullPointerException     if the {@code service} is {@code null}, or the
     *                                  {@code charset} is {@code null}.
     * @throws CharacterCodingException if the bytes are malformed or unmappable in the
     *                                  {@code charset}.
     * @see #decode(HelloWorld, CharsetDecoder)
     * @see Charset#newDecoder()
     */
    public static CharBuffer decode(final HelloWorld service, final Charset charset)
            throws CharacterCodingException {
        Objects.requireNonNull(charset, "charset is null");
        return decode(service, charset.newDecoder());
    }

    /**
     * Returns a new {@link CharBuffer} containing the
     * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> decoded as
     * {@link StandardCharsets#US_ASCII US-ASCII}, produced by invoking
     * {@link #decode(HelloWorld, Charset) decode(service, charset)} with
     * {@link StandardCharsets#US_ASCII}.
     *
     * @param service the {@link HelloWorld} service that produces the bytes.
     * @return a new {@value HelloWorld#BYTES}-character {@link CharBuffer} containing the
     * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>.
     * @throws NullPointerException     if the {@code service} is {@code null}.
     * @throws CharacterCodingException if the bytes are not valid US-ASCII.
     * @see #decode(HelloWorld, Charset)
     */
    public static CharBuffer decode(final HelloWorld service) throws CharacterCodingException {
        return decode(service, StandardCharsets.US_ASCII);
    }

    // ------------------------------------------------------------------------------------ java.net

    /**
     * Returns a new {@link DatagramPacket} of length {@value HelloWorld#BYTES} whose data buffer
     * holds the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, produced by
     * constructing a {@value HelloWorld#BYTES}-byte packet and passing it to
     * {@link HelloWorld#append(DatagramPacket) append(packet)} on the specified service.
     * <p>
     * The returned packet has no destination address; assign one before
     * {@linkplain java.net.DatagramSocket#send(DatagramPacket) sending}.
     *
     * @param service the {@link HelloWorld} service that produces the bytes.
     * @return a new {@link DatagramPacket} of length {@value HelloWorld#BYTES}.
     * @throws NullPointerException if the {@code service} is {@code null}.
     * @see HelloWorld#append(DatagramPacket)
     * @see DatagramPacket#DatagramPacket(byte[], int)
     */
    public static DatagramPacket packet(final HelloWorld service) {
        Objects.requireNonNull(service, "service is null");
        return service.append(
                new DatagramPacket(new byte[BYTES], 0)
        );
    }

    /**
     * Returns a new {@link DatagramPacket} of length {@value HelloWorld#BYTES} addressed to the
     * specified socket address whose data buffer holds the
     * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, produced by
     * constructing a {@value HelloWorld#BYTES}-byte packet addressed to the {@code address} and
     * passing it to {@link HelloWorld#append(DatagramPacket) append(packet)} on the specified
     * service.
     *
     * @param service the {@link HelloWorld} service that produces the bytes.
     * @param address the destination {@link SocketAddress}.
     * @return a new {@link DatagramPacket} of length {@value HelloWorld#BYTES} addressed to
     * {@code address}.
     * @throws NullPointerException if either {@code service} or {@code address} is {@code null}.
     * @see HelloWorld#append(DatagramPacket)
     * @see DatagramPacket#DatagramPacket(byte[], int, java.net.SocketAddress)
     */
    public static DatagramPacket packet(final HelloWorld service, final SocketAddress address) {
        Objects.requireNonNull(service, "service is null");
        Objects.requireNonNull(address, "address is null");
        return service.append(
                new DatagramPacket(new byte[BYTES], 0, address)
        );
    }

    // ---------------------------------------------------------------------------------------------
    private HelloWorldUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
