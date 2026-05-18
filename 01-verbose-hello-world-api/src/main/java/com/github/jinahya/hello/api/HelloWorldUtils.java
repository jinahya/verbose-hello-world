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

import com.github.jinahya.hello.api.util._ExcludeFromCoverage_PrivateConstructor_Obviously;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Convenience methods that produce the
 * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> in several common Java I/O
 * shapes — {@code byte[]}, {@link ByteBuffer}, and {@link String} — by delegating to a given
 * {@link HelloWorld} service.
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
     * Returns a fresh {@link ByteBuffer} of capacity {@value HelloWorld#BYTES} containing the
     * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, by delegating to
     * {@link #buffer(HelloWorld, Supplier) buffer(service, supplier)} with a supplier that
     * {@linkplain ByteBuffer#allocate(int) allocates} a new {@value HelloWorld#BYTES}-byte buffer.
     * <p>
     * The returned buffer is ready for reading — {@code position} is {@code 0} and {@code limit} is
     * {@value HelloWorld#BYTES}.
     *
     * @param service the {@link HelloWorld} service that produces the bytes.
     * @return a new {@link ByteBuffer} containing the
     * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, ready for reading.
     * @throws NullPointerException if the {@code service} is {@code null}.
     * @see #buffer(HelloWorld, Supplier)
     */
    public static ByteBuffer buffer(final HelloWorld service) {
        return buffer(service, () -> ByteBuffer.allocate(HelloWorld.BYTES));
    }

    // ---------------------------------------------------------------------------------------------
    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private HelloWorldUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
