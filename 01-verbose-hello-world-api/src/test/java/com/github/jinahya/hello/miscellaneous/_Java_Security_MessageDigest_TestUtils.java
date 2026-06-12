package com.github.jinahya.hello.miscellaneous;

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

import lombok.extern.slf4j.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static com.github.jinahya.hello.miscellaneous._Java_Security_MessageDigest_TestConstants.*;

/**
 * Test utilities for the {@link MessageDigest} class.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
@SuppressWarnings({"java:S101"})
public final class _Java_Security_MessageDigest_TestUtils {

    public static Stream<String> algorithmStream() {
        return MESSAGE_DIGEST_ALGORITHMS.stream();
    }

    public static Stream<String> latestLtsAlgorithms() {
        return _Java_Security_MessageDigest_TestConstants.ALGORITHMS_LATEST_LTS.stream();
    }

    public static Stream<String> latestJdkAlgorithms() {
        return _Java_Security_MessageDigest_TestConstants.ALGORITHMS_LATEST_JDK.stream();
    }

    public static void acceptEachAlgorithm(final Consumer<? super String> consumer) {
        algorithmStream().forEach(consumer);
    }

    public static void acceptInstanceForEachAlgorithm(
            final Consumer<? super MessageDigest> consumer) {
        acceptEachAlgorithm(a -> {
            try {
                consumer.accept(MessageDigest.getInstance(a));
            } catch (final NoSuchAlgorithmException nsae) {
                throw new RuntimeException(nsae);
            }
        });
    }

    // ---------------------------------------------------------------------------------------------
    public static void printMessageDigest(final MessageDigest digest, final byte[] result) {
        System.out.printf("%20s | %20s | %3d(%4d) | %s%n",
                          digest.getProvider().getName(),
                          digest.getAlgorithm(),
                          result.length,
                          result.length << 3,
                          __TestUtils.format(result)
        );
    }

    public static void printMessageDigest(final MessageDigest digest) {
        printMessageDigest(digest, digest.digest());
    }

    // ---------------------------------------------------------------------------------------------
    static void messageDigestAlgorithm(final Provider provider, final Consumer<String> consumer) {
        for (final var service : provider.getServices()) {
            if (!service.getType().equals(MESSAGE_DIGEST_SERVICE_TYPE)) {
                continue;
            }
            consumer.accept(service.getAlgorithm());
        }
    }

    static void messageDigestProviderAndAlgorithm(final BiConsumer<Provider, String> consumer) {
        _Java_Security_Provider_TestUtils.acceptEachProvider(provider -> {
            messageDigestAlgorithm(provider, a -> {
                consumer.accept(provider, a);
            });
        });
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Updates all bytes read from the specified input stream, using the specified buffer, to the
     * specified message digest.
     *
     * @param digest the message digest to be updated.
     * @param b      a byte array used as a read buffer; must not be empty.
     * @param stream the input stream from which bytes are read.
     * @return the total number of bytes read from the {@code stream} (and updated to the
     * {@code digest}).
     * @throws IOException if an I/O error occurs while reading from the {@code stream}.
     */
    public static long update(final MessageDigest digest, final byte[] b, final InputStream stream)
            throws IOException {
        Objects.requireNonNull(digest, "digest is null");
        if (Objects.requireNonNull(b, "b is null").length == 0) {
            throw new IllegalArgumentException("b.length is zero");
        }
        Objects.requireNonNull(stream, "stream is null");
        long count = 0L;
        for (int r; (r = stream.read(b)) != -1; ) {
            digest.update(b, 0, r);
            count += r;
        }
        return count;
    }

    /**
     * Updates all bytes read from the specified file, using the specified buffer, to the specified
     * message digest.
     *
     * @param digest the message digest to be updated.
     * @param b      a byte array used as a read buffer; must not be empty.
     * @param file   the file from which bytes are read.
     * @return the total number of bytes read from the {@code file} (and updated to the
     * {@code digest}).
     * @throws IOException if an I/O error occurs while reading from the {@code file}.
     */
    public static long update(final MessageDigest digest, final byte[] b, final File file)
            throws IOException {
        Objects.requireNonNull(file, "file is null");
        try (var stream = new FileInputStream(file)) {
            return update(digest, b, stream);
        }
    }

    /**
     * Updates all bytes read from the specified readable byte channel, using the specified buffer,
     * to the specified message digest.
     *
     * @param digest  the message digest to be updated.
     * @param b       a byte buffer used as a read buffer; must have a positive capacity.
     * @param channel the readable byte channel from which bytes are read.
     * @return the total number of bytes read from the {@code channel} (and updated to the
     * {@code digest}).
     * @throws IOException if an I/O error occurs while reading from the {@code channel}.
     */
    public static long update(final MessageDigest digest, final ByteBuffer b,
                              final ReadableByteChannel channel)
            throws IOException {
        Objects.requireNonNull(digest, "digest is null");
        if (Objects.requireNonNull(b, "b is null").capacity() == 0) {
            throw new IllegalArgumentException("b.capacity is zero");
        }
        Objects.requireNonNull(channel, "channel is null");
        long count = 0L;
        while (channel.read(b.clear()) != -1) {
            digest.update(b.flip());
        }
        return count;
    }

    /**
     * Updates all bytes read from the file at the specified path, using the specified buffer, to
     * the specified message digest.
     *
     * @param digest the message digest to be updated.
     * @param b      a byte buffer used as a read buffer; must have a positive capacity.
     * @param path   the path to the file from which bytes are read.
     * @return the total number of bytes read from the file at the {@code path} (and updated to the
     * {@code digest}).
     * @throws IOException if an I/O error occurs while reading from the file.
     */
    public static long update(final MessageDigest digest, final ByteBuffer b, final Path path)
            throws IOException {
        Objects.requireNonNull(path, "path is null");
        try (var channel = FileChannel.open(path, StandardOpenOption.READ)) {
            return update(digest, b, channel);
        }
    }

    private _Java_Security_MessageDigest_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
