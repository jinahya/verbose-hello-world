package com.github.jinahya.hello.misc.c00rfc86_;

/*-
 * #%L
 * verbose-hello-world-srv-common
 * %%
 * Copyright (C) 2018 - 2023 Jinahya, Inc.
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

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

/**
 * Shared utilities for for {@link com.github.jinahya.hello.misc.c01rfc863} package and
 * {@link com.github.jinahya.hello.misc.c02rfc862} package.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
@SuppressWarnings({
        "java:S101" // class _Rfc86_...
})
public final class _Rfc86_Utils {

    // -------------------------------------------------------------------------------- array/buffer
    private static final int MIN_ARRAY_LENGTH = 1;

    private static final int MAX_ARRAY_LENGTH = 8192;

    private static byte[] array() {
        return new byte[
                ThreadLocalRandom.current().nextInt(MIN_ARRAY_LENGTH, MAX_ARRAY_LENGTH + 1)
                ];
    }

    /**
     * Returns a new array of bytes whose length is between {@value #MIN_ARRAY_LENGTH} and
     * {@value #MAX_ARRAY_LENGTH}, both inclusive.
     *
     * @return a new array of bytes.
     */
    public static byte[] newArray() {
        final var array = array();
        log.debug("array.length: {}", array.length);
        return array;
    }

    /**
     * Returns a new byte buffer {@link ByteBuffer#wrap(byte[]) wraps} the result of
     * {@link #newArray()}.
     *
     * @return a new byte buffer {@link ByteBuffer#wrap(byte[]) wraps} the result of
     * {@link #newArray()}.
     * @see #newArray()
     */
    public static ByteBuffer newBuffer() {
        final var buffer = ByteBuffer.wrap(array());
        log.debug("buffer.capacity: {}", buffer.capacity());
        assert buffer.arrayOffset() == 0;
        assert buffer.capacity() >= MIN_ARRAY_LENGTH;
        assert buffer.capacity() <= MAX_ARRAY_LENGTH;
        return buffer;
    }

    // --------------------------------------------------------------------------------------- bytes
    private static final int BOUND_RANDOM_BYTES = 65536;

    /**
     * Returns a new {@code int} between {@code 0}(inclusive) and
     * {@value #BOUND_RANDOM_BYTES}(exclusive).
     *
     * @return a new {@code int} between {@code 0}(inclusive) and
     * {@value #BOUND_RANDOM_BYTES}(exclusive).
     */
    public static int newRandomBytes() {
        return ThreadLocalRandom.current().nextInt(BOUND_RANDOM_BYTES);
    }

    // -------------------------------------------------------------------------------------- digest

    /**
     * Returns a new message digest object that implements the specified digest algorithm..
     *
     * @return the name of the algorithm.
     */
    public static MessageDigest newDigest(final String algorithm) {
        Objects.requireNonNull(algorithm, "algorithm is null");
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (final NoSuchAlgorithmException nsae) {
            throw new IllegalArgumentException("algorithm is unknown: " + algorithm, nsae);
        }
    }

    public static void logDigest(final MessageDigest digest,
                                 final Function<? super byte[], ? extends CharSequence> printer) {
        Objects.requireNonNull(digest, "digest is null");
        Objects.requireNonNull(printer, "printer is null");
        log.info("digest: {}", printer.apply(digest.digest()));
    }

    public static void logDigest(final String algorithm, final byte[] array, final int offset,
                                 final int length,
                                 final Function<? super byte[], ? extends CharSequence> printer) {
        final var digest = newDigest(algorithm);
        digest.update(
                array,  // <input>
                offset, // <offset>
                length  // <len>
        );
        logDigest(digest, printer);
    }

    public static void logDigest(final String algorithm, final ByteBuffer buffer,
                                 final Function<? super byte[], ? extends CharSequence> printer) {
        final var digest = newDigest(algorithm);
        digest.update(
                buffer // input
        );
        logDigest(digest, printer);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new instance.
     */
    private _Rfc86_Utils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
