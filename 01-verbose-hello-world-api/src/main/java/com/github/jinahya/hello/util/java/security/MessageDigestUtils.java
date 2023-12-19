package com.github.jinahya.hello.util.java.security;

/*-
 * #%L
 * verbose-hello-world-api
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

import com.github.jinahya.hello.util._ExcludeFromCoverage_PrivateConstructor_Obviously;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public final class MessageDigestUtils {

    public static Provider[] getProviders(final String algorithm) {
        Objects.requireNonNull(algorithm, "algorithm is null");
        return Security.getProviders(MessageDigestConstants.CRYPTO_SERVICE + '.' + algorithm);
    }

    // ---------------------------------------------------------------------------------------------
    public static long update(final MessageDigest digest, final InputStream stream,
                              final byte[] buffer)
            throws IOException {
        Objects.requireNonNull(digest, "digest is null");
        Objects.requireNonNull(stream, "stream is null");
        if (Objects.requireNonNull(buffer, "buffer is null").length == 0) {
            throw new IllegalArgumentException(
                    "zero-length buffer: " + Objects.toString(buffer));
        }
        var count = 0L;
        for (int r; (r = stream.read(buffer)) != -1; count += r) {
            digest.update(buffer, 0, r);
        }
        return count;
    }

//    public static long update(final MessageDigest digest, final File file, final byte[] buffer)
//            throws IOException {
//        if (Objects.requireNonNull(buffer, "buffer is null").length == 0) {
//            throw new IllegalArgumentException("zero-length buffer: " + Objects.toString(buffer));
//        }
//        try (var stream = new FileInputStream(file)) {
//            return update(digest, stream, buffer);
//        }
//    }

//    public static byte[] getDigest(final MessageDigest digest, final File file, final byte[] buffer)
//            throws IOException {
//        final var bytes = update(digest, file, buffer);
//        return digest.digest();
//    }

    public static byte[] getDigest(final String algorithm, final File file, final byte[] buffer)
            throws NoSuchAlgorithmException, IOException {
        final var digest = MessageDigest.getInstance(algorithm);
        try (var stream = new FileInputStream(file)) {
            final var bytes = update(digest, stream, buffer);
            return digest.digest();
        }
    }

    // ---------------------------------------------------------------------------------------------
    public static long update(final MessageDigest digest, final ReadableByteChannel channel,
                              final ByteBuffer buffer)
            throws IOException {
        Objects.requireNonNull(digest, "digest is null");
        Objects.requireNonNull(channel, "channel is null");
        if (Objects.requireNonNull(buffer, "buffer is null").capacity() == 0) {
            throw new IllegalArgumentException("zero-capacity buffer: " + buffer);
        }
        var count = 0L;
        for (int r; (r = channel.read(buffer.clear())) != -1; count += r) {
            digest.update(buffer.flip());
        }
        return count;
    }

//    public static long update(final MessageDigest digest, final Path path, final ByteBuffer buffer)
//            throws IOException {
//        Objects.requireNonNull(digest, "digest is null");
//        if (!Files.isRegularFile(Objects.requireNonNull(path, "path is null"))) {
//            throw new IllegalArgumentException("not a regular file: " + path);
//        }
//        if (Objects.requireNonNull(buffer, "buffer is null").capacity() == 0) {
//            throw new IllegalArgumentException("zero-capacity buffer: " + buffer);
//        }
//        try (var channel = FileChannel.open(path)) {
//            return update(digest, channel, buffer);
//        }
//    }

//    public static byte[] getDigest(final MessageDigest digest, final Path path,
//                                   final ByteBuffer buffer)
//            throws IOException {
//        try (var channel = FileChannel.open(path)) {
//            final var bytes = update(digest, channel, buffer);
//        }
//        return digest.digest();
//    }

    public static byte[] getDigest(final String algorithm, final Path path, final ByteBuffer buffer)
            throws NoSuchAlgorithmException, IOException {
        final var digest = MessageDigest.getInstance(algorithm);
        try (var channel = FileChannel.open(path)) {
            final var bytes = update(digest, channel, buffer);
            return digest.digest();
        }
    }

    // ------------------------------------------------------------------------------- MessageDigest

    /**
     * Updates specified message digest with specified number of bytes preceding specified byte
     * buffer's current position.
     *
     * @param digest the message digest to update.
     * @param buffer the byte buffer whose bytes are updated to the {@code digest}.
     * @param bytes  the number of bytes preceding the {@code buffer}'s current {@code position} to
     *               be updated to the {@code digest}.
     */
    public static void updateDigest(final MessageDigest digest, final ByteBuffer buffer,
                                    final int bytes) {
        Objects.requireNonNull(digest, "digest is null");
        Objects.requireNonNull(buffer, "buffer is null");
        if (bytes < 0) {
            throw new IllegalArgumentException("bytes(" + bytes + ") < 0");
        }
        if (bytes > buffer.position()) {
            throw new IllegalArgumentException(
                    "bytes(" + bytes + ") > buffer.position(" + buffer.position() + ")");
        }
        if (ThreadLocalRandom.current().nextBoolean()) {
            digest.update(buffer.slice(buffer.position() - bytes, bytes));
            return;
        }
        final var position = buffer.position();
        final var limit = buffer.limit();
        buffer.position(position - bytes).limit(position);
        digest.update(buffer);
        buffer.position(position).limit(limit);
    }

    // ---------------------------------------------------------------------------------------------
    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private MessageDigestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
