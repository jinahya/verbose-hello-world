package com.github.jinahya.hello.misc;

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

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

@Slf4j
public final class _Rfc86_Utils {

    // -------------------------------------------------------------------------------- array/buffer

    /**
     * Returns a new array of bytes whose length is between 1 and 1024, both inclusive.
     *
     * @return a new array of bytes.
     */
    private static byte[] array() {
        return new byte[ThreadLocalRandom.current().nextInt(1024) + 1];
    }

    /**
     * Returns a new array of bytes whose length is between {@code 1} and {@code 1024}, both
     * inclusive.
     *
     * @return a new non-empty array of bytes.
     */
    public static byte[] newArray() {
        final var array = array();
        log.debug("array.length: {}", array.length);
        return array;
    }

    /**
     * Returns a new byte buffer {@link ByteBuffer#wrap(byte[]) wraps} a result of
     * {@link #newArray()}.
     *
     * @return a new byte buffer {@link ByteBuffer#wrap(byte[]) wraps} a result of
     * {@link #newArray()}.
     * @see #newArray()
     */
    public static ByteBuffer newBuffer() {
        final var buffer = ByteBuffer.wrap(array());
        log.debug("buffer.capacity: {}", buffer.capacity());
        return buffer;
    }

    // --------------------------------------------------------------------------------------- bytes

    /**
     * Returns a new {@code int} between {@code 0}(inclusive) and {@code 65536}(exclusive).
     *
     * @return a new {@code int} between {@code 0}(inclusive) and {@code 65536}(exclusive).
     */
    public static int randomBytes() {
        return ThreadLocalRandom.current().nextInt(65536);
    }

    // -------------------------------------------------------------------------------------- digest

    /**
     * Returns a new message digest of specified algorithm.
     *
     * @return the algorithm.
     */
    public static MessageDigest newDigest(final String algorithm) {
        Objects.requireNonNull(algorithm, "algorithm is null");
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (final NoSuchAlgorithmException nsae) {
            throw new RuntimeException("unable to creat digest with " + algorithm, nsae);
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
        digest.update(array, offset, length);
        logDigest(digest, printer);
    }

    public static void logDigest(final String algorithm, final ByteBuffer buffer,
                                 final Function<? super byte[], ? extends CharSequence> printer) {
        final var digest = newDigest(algorithm);
        digest.update(buffer);
        logDigest(digest, printer);
    }

    // -------------------------------------------------------------------------------------- client
    private static final String LOG_FORMAT_CONNECTED = "connected to {}, through {}";

    private static final String LOG_FORMAT_ACCEPTED = "accepted from {}, through {}";

    public static void logConnected(final Socket client) {
        Objects.requireNonNull(client, "client is null");
        log.info(LOG_FORMAT_CONNECTED, client.getRemoteSocketAddress(), client.getLocalAddress());
    }

    public static void logAccepted(final Socket client) {
        Objects.requireNonNull(client, "client is null");
        log.info(LOG_FORMAT_ACCEPTED, client.getRemoteSocketAddress(), client.getLocalAddress());
    }

    public static void logConnected(final SocketChannel client) throws IOException {
        Objects.requireNonNull(client, "client is null");
        log.info(LOG_FORMAT_CONNECTED, client.getRemoteAddress(), client.getLocalAddress());
    }

    public static void logAccepted(final SocketChannel client) throws IOException {
        Objects.requireNonNull(client, "client is null");
        log.info(LOG_FORMAT_ACCEPTED, client.getRemoteAddress(), client.getLocalAddress());
    }

    public static void logConnected(final AsynchronousSocketChannel client) throws IOException {
        Objects.requireNonNull(client, "client is null");
        log.info(LOG_FORMAT_CONNECTED, client.getRemoteAddress(), client.getLocalAddress());
    }

    public static void logAccepted(final AsynchronousSocketChannel client) throws IOException {
        Objects.requireNonNull(client, "client is null");
        log.info(LOG_FORMAT_ACCEPTED, client.getRemoteAddress(), client.getLocalAddress());
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new instance.
     */
    private _Rfc86_Utils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
