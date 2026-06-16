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

import com.github.jinahya.hello.api.annotations.*;
import lombok.extern.slf4j.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.file.*;
import java.security.*;
import java.security.spec.*;
import java.util.*;

/**
 * Test utilities for the {@link java.security.Signature} class.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/security/Signature.html">java.security.Signature</a>
 * (Java 25)
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/security/Signature.html">java.security.Signature</a>
 * (Java 26)
 */
@Slf4j
@SuppressWarnings({"java:S101"})
public final class _Java_Security_Signature_TestUtils {

    // ---------------------------------------------------------------------------------------------
    public record Signing(KeyPair keyPair, Signature signature, AlgorithmParameterSpec params) {

    }

    private static Signing withDSA_(final int keysize, final String algorithm)
            throws NoSuchAlgorithmException {
        final var keyPair = _Java_Security_KeyPair_TestUtils.generateKeyPair("DSA", keysize);
        final var signature = Signature.getInstance(algorithm);
        return new Signing(keyPair, signature, null);
    }

    @_LatestLTS
    @_LatestJDK
    public static Signing SHA1withDSA(final int keysize) throws NoSuchAlgorithmException {
        return withDSA_(keysize, "SHA1withDSA");
    }

    @_LatestLTS
    @_LatestJDK
    public static Signing SHA256withDSA(final int keysize) throws NoSuchAlgorithmException {
        return withDSA_(keysize, "SHA256withDSA");
    }

    private static Signing withECDSA_(final String curve, final String algorithm)
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        final var keyPair = _Java_Security_KeyPair_TestUtils.generateKeyPair(
                "EC", new ECGenParameterSpec(curve));
        final var signature = Signature.getInstance(algorithm);
        return new Signing(keyPair, signature, null);
    }

    @_LatestLTS
    @_LatestJDK
    public static Signing SHA256withECDSA()
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        return withECDSA_("secp256r1", "SHA256withECDSA");
    }

    @_LatestLTS
    @_LatestJDK
    public static Signing SHA384withECDSA()
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        return withECDSA_("secp384r1", "SHA384withECDSA");
    }

    private static Signing withRSA_(final int keysize, final String algorithm)
            throws NoSuchAlgorithmException {
        final var keyPair = _Java_Security_KeyPair_TestUtils.generateKeyPair("RSA", keysize);
        final var signature = Signature.getInstance(algorithm);
        return new Signing(keyPair, signature, null);
    }

    @_LatestLTS
    @_LatestJDK
    public static Signing SHA1withRSA(final int keysize) throws NoSuchAlgorithmException {
        return withRSA_(keysize, "SHA1withRSA");
    }

    @_LatestLTS
    @_LatestJDK
    public static Signing SHA256withRSA(final int keysize) throws NoSuchAlgorithmException {
        return withRSA_(keysize, "SHA256withRSA");
    }

    @_LatestLTS
    @_LatestJDK
    public static Signing SHA384withRSA(final int keysize) throws NoSuchAlgorithmException {
        return withRSA_(keysize, "SHA384withRSA");
    }

    private static Signing RSASSA_PSS_(final int keysize, final String hashAlgorithm)
            throws NoSuchAlgorithmException {
        final var keyPair = _Java_Security_KeyPair_TestUtils.generateKeyPair("RSA", keysize);
        final var signature = Signature.getInstance("RSASSA-PSS");
        final var saltLength = MessageDigest.getInstance(hashAlgorithm).getDigestLength();
        final var params = new PSSParameterSpec(
                hashAlgorithm, "MGF1", new MGF1ParameterSpec(hashAlgorithm), saltLength, 1);
        return new Signing(keyPair, signature, params);
    }

    @_LatestLTS
    @_LatestJDK
    public static Signing RSASSA_PSS_SHA_256(final int keysize) throws NoSuchAlgorithmException {
        return RSASSA_PSS_(keysize, "SHA-256");
    }

    @_LatestLTS
    @_LatestJDK
    public static Signing RSASSA_PSS_SHA_384(final int keysize) throws NoSuchAlgorithmException {
        return RSASSA_PSS_(keysize, "SHA-384");
    }

    // ---------------------------------------------------------------------------------------------

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
