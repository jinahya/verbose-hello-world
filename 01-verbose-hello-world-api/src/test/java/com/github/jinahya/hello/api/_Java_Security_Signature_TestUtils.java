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

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Objects;

/**
 * Test utilities for the {@link java.security.Signature} class.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
@SuppressWarnings({"java:S101"})
public final class _Java_Security_Signature_TestUtils {

    /**
     * Updates all bytes read from the specified input stream, using the specified buffer, to the
     * specified signature.
     *
     * @param signature the signature to be updated.
     * @param b         a byte array used as a read buffer; must not be empty.
     * @param stream    the input stream from which bytes are read.
     * @return the total number of bytes read from the {@code stream} (and updated to the
     * {@code signature}).
     * @throws IOException        if an I/O error occurs while reading from the {@code stream}.
     * @throws SignatureException if the {@code signature} is not properly initialized.
     */
    public static long update(final Signature signature, final byte[] b, final InputStream stream)
            throws IOException, SignatureException {
        Objects.requireNonNull(signature, "signature is null");
        if (Objects.requireNonNull(b, "b is null").length == 0) {
            throw new IllegalArgumentException("b.length is zero");
        }
        Objects.requireNonNull(stream, "stream is null");
        long count = 0L;
        for (int r; (r = stream.read(b)) != -1; ) {
            signature.update(b, 0, r);
            count += r;
        }
        return count;
    }

    /**
     * Updates all bytes read from the specified file, using the specified buffer, to the specified
     * signature.
     *
     * @param signature the signature to be updated.
     * @param b         a byte array used as a read buffer; must not be empty.
     * @param file      the file from which bytes are read.
     * @return the total number of bytes read from the {@code file} (and updated to the
     * {@code signature}).
     * @throws IOException        if an I/O error occurs while reading from the {@code file}.
     * @throws SignatureException if the {@code signature} is not properly initialized.
     */
    public static long update(final Signature signature, final byte[] b, final File file)
            throws IOException, SignatureException {
        Objects.requireNonNull(file, "file is null");
        try (var stream = new FileInputStream(file)) {
            return update(signature, b, stream);
        }
    }

    /**
     * Updates all bytes read from the specified readable byte channel, using the specified buffer,
     * to the specified signature.
     *
     * @param signature the signature to be updated.
     * @param b         a byte buffer used as a read buffer; must have a positive capacity.
     * @param channel   the readable byte channel from which bytes are read.
     * @return the total number of bytes read from the {@code channel} (and updated to the
     * {@code signature}).
     * @throws IOException        if an I/O error occurs while reading from the {@code channel}.
     * @throws SignatureException if the {@code signature} is not properly initialized.
     */
    public static long update(final Signature signature, final ByteBuffer b,
                              final ReadableByteChannel channel)
            throws IOException, SignatureException {
        Objects.requireNonNull(signature, "signature is null");
        if (Objects.requireNonNull(b, "b is null").capacity() == 0) {
            throw new IllegalArgumentException("b.capacity is zero");
        }
        Objects.requireNonNull(channel, "stream is null");
        long count = 0L;
        while (channel.read(b.clear()) != -1) {
            signature.update(b.flip());
        }
        return count;
    }

    /**
     * Updates all bytes read from the file at the specified path, using the specified buffer, to
     * the specified signature.
     *
     * @param signature the signature to be updated.
     * @param b         a byte buffer used as a read buffer; must have a positive capacity.
     * @param path      the path to the file from which bytes are read.
     * @return the total number of bytes read from the file at the {@code path} (and updated to the
     * {@code signature}).
     * @throws IOException        if an I/O error occurs while reading from the file.
     * @throws SignatureException if the {@code signature} is not properly initialized.
     */
    public static long update(final Signature signature, final ByteBuffer b, final Path path)
            throws IOException, SignatureException {
        Objects.requireNonNull(path, "path is null");
        try (var channel = FileChannel.open(path, StandardOpenOption.READ)) {
            return update(signature, b, channel);
        }
    }

    private _Java_Security_Signature_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
