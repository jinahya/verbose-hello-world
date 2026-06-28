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
        return service.set(new byte[HelloWorld.BYTES]);
    }

    /**
     * Returns a {@link ByteBuffer} containing the
     * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, produced by invoking
     * {@link HelloWorld#put(ByteBuffer) put(ByteBuffer)} on the specified service with a buffer
     * obtained from the specified supplier, and then {@linkplain ByteBuffer#flip() flipping} the
     * result.
     * <p>
     * The buffer returned by {@code supplier.get()} is passed straight through to the service; this
     * method does not inspect or validate it. Whatever the supplier returns — {@code null}, a
     * buffer with fewer or more than {@value HelloWorld#BYTES}
     * {@linkplain ByteBuffer#remaining() remaining} bytes, a read-only buffer, etc. — is the
     * service's concern, and any resulting exception propagates from
     * {@link HelloWorld#put(ByteBuffer) put(ByteBuffer)} unchanged.
     *
     * @param service  the {@link HelloWorld} service that produces the bytes.
     * @param supplier a supplier of the {@link ByteBuffer} to pass to the service.
     * @return the {@link ByteBuffer} returned by the service, after
     * {@linkplain ByteBuffer#flip() flipping}.
     * @throws NullPointerException if the {@code service} is {@code null}, or the {@code supplier}
     *                              is {@code null}.
     * @see HelloWorld#put(ByteBuffer)
     */
    public static ByteBuffer buffer(final HelloWorld service,
                                    final Supplier<? extends ByteBuffer> supplier) {
        Objects.requireNonNull(service, "service is null");
        Objects.requireNonNull(supplier, "supplier is null");
        return service.put(supplier.get()).flip();
    }

    /**
     * The single-argument convenience of
     * {@link #buffer(HelloWorld, Supplier) buffer(service, () ->
     * ByteBuffer.allocate(HelloWorld.BYTES))} — supplies a freshly heap-allocated
     * {@value HelloWorld#BYTES}-byte buffer to the service.
     *
     * @param service the {@link HelloWorld} service that produces the bytes; must not be
     *                {@code null}.
     * @return the {@link ByteBuffer} returned by the service, after
     * {@linkplain ByteBuffer#flip() flipping}; never {@code null}.
     * @throws NullPointerException if {@code service} is {@code null}.
     * @see #buffer(HelloWorld, Supplier)
     */
    public static ByteBuffer buffer(final HelloWorld service) {
        return buffer(service, () -> ByteBuffer.allocate(HelloWorld.BYTES));
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
     * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> decoded as
     * {@link StandardCharsets#US_ASCII US-ASCII}, produced by
     * {@linkplain #buffer(HelloWorld, Supplier) obtaining the byte buffer} from the specified
     * service with the specified supplier, and passing it to a fresh
     * {@link StandardCharsets#US_ASCII US-ASCII} decoder.
     *
     * @param service  the {@link HelloWorld} service that produces the bytes.
     * @param supplier a supplier of the {@link ByteBuffer} to pass to the service.
     * @return a new {@value HelloWorld#BYTES}-character {@link CharBuffer} containing the
     * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>.
     * @throws NullPointerException     if the {@code service} is {@code null}, or the
     *                                  {@code supplier} is {@code null}.
     * @throws CharacterCodingException if the US-ASCII decoder fails to decode the bytes.
     * @see #buffer(HelloWorld, Supplier)
     * @see CharsetDecoder#decode(ByteBuffer)
     */
    public static CharBuffer decode(final HelloWorld service,
                                    final Supplier<? extends ByteBuffer> supplier)
            throws CharacterCodingException {
        return StandardCharsets.US_ASCII.newDecoder().decode(buffer(service, supplier));
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
        return service.append(
                new DatagramPacket(new byte[HelloWorld.BYTES], 0)
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
     * @throws NullPointerException if the {@code service} is {@code null}.
     * @see HelloWorld#append(DatagramPacket)
     * @see DatagramPacket#DatagramPacket(byte[], int, java.net.SocketAddress)
     */
    public static DatagramPacket packet(final HelloWorld service, final SocketAddress address) {
        return service.append(
                new DatagramPacket(new byte[HelloWorld.BYTES], 0, address)
        );
    }

    // ---------------------------------------------------------------------------------------------
    private HelloWorldUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
