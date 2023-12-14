package com.github.jinahya.hello.javax.crypto;

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

import com.github.jinahya.hello.util.java.io.FileUtils;
import com.github.jinahya.hello.util.java.io._JavaIoUtils;
import com.github.jinahya.hello.util.java.nio.JavaNioUtils;
import com.github.jinahya.hello.util.java.nio.file.PathUtils;
import com.github.jinahya.hello.util.java.security.MessageDigestUtils;
import com.github.jinahya.hello.util.javax.crypto.CipherUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HexFormat;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * A class for testing classes defined in {@link java.security} package.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/21/docs/specs/security/standard-names.html#cipher-algorithms"><code>Cipher</code>
 * Algorithms</a>
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/21/docs/specs/security/standard-names.html#cipher-algorithm-modes"><code>Cipher</code>
 * Algorithm modes</a>
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/21/docs/specs/security/standard-names.html#cipher-algorithm-paddings"><code>Cihper</code>
 * Algorithm Paddings</code></a>
 */
@DisplayName("javax.crypto.Cipher")
@Slf4j
class CipherTest {

    static {
        Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 1);
//        Security.insertProviderAt(new gnu.crypto.jce.GnuCrypto(), 1);
//        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    private static Stream<String> algorithms() {
        return Stream.of(
                "AES",
                "AESWrap",
                "AESWrapPad",
                "ARCFOUR",
                "ChaCha20",
                "ChaCha20-Poly1305",
                "DES",
                "DESede",
                "DESedeWrap",
                "ECIES",
//                "PBEWith<digest>And<encryption>",
//                "PBEWith<prf>And<encryption>",
                "RC2",
                "RC4",
                "RC5",
                "RSA"
        );
    }

    private static Stream<String> modes() {
        return Stream.of(
                "NONE",
                "CBC",
                "CCM",
                "CFB", "CFBx",
                "CTR",
                "CTS",
                "ECB",
                "GCM",
                "KW",
                "KWP",
                "OFB", "OFBx",
                "PCBC"
        );
    }

    private static Stream<String> paddings() {
        return Stream.of(
                "NoPadding",
                "ISO10126Padding",
                "OAEPPadding",
//                "OAEPWith<digest>And<mgf>Padding",
                "PKCS1Padding",
                "PKCS5Padding",
                "SSL3Padding"
        );
    }

    private static Stream<Arguments> requiredToBeSupportedTransformationsAnsKeysizss() {
        return Stream.of(
                arguments("AES/CBC/NoPadding", List.of(128)),
                arguments("AES/CBC/PKCS5Padding", List.of(128)),
                arguments("AES/ECB/NoPadding", List.of(128)),
                arguments("AES/ECB/PKCS5Padding", List.of(128)),
                arguments("AES/GCM/NoPadding", List.of(128)),
                arguments("DESede/CBC/NoPadding", List.of(168)),
                arguments("DESede/CBC/PKCS5Padding", List.of(168)),
                arguments("DESede/ECB/NoPadding", List.of(168)),
                arguments("DESede/ECB/PKCS5Padding", List.of(168)),
                arguments("RSA/ECB/PKCS1Padding", List.of(1024, 2048)),
                arguments("RSA/ECB/OAEPWithSHA-1AndMGF1Padding", List.of(1024)),
                arguments("RSA/ECB/OAEPWithSHA-1AndMGF1Padding", List.of(2048)),
                arguments("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", List.of(1024, 2048))
        );
    }

    private static Stream<String> transformationsRequiredToBeSupported() {
        return requiredToBeSupportedTransformationsAnsKeysizss()
                .map(a -> (String) a.get()[0]);
    }

    @DisplayName("getInstance(transformation-required-to-be-supported")
    @MethodSource({"transformationsRequiredToBeSupported"})
    @ParameterizedTest
    void getInstance_NotThrow_requiredToBeSupportedTransformations__(final String transformation) {
        try {
            Cipher.getInstance(transformation);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            log.warn("unable to get a cipher instance; transformation: {}", transformation, e);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    static Key generateKey(final String algorithm, final int keysize)
            throws NoSuchAlgorithmException {
        final var generator = KeyGenerator.getInstance(algorithm);
        generator.init(keysize);
        return generator.generateKey();
    }

    private static KeyPair generateKeyPair(final String algorithm, final int keysize)
            throws NoSuchAlgorithmException {
        final var generator = KeyPairGenerator.getInstance(algorithm);
        generator.initialize(keysize);
        return generator.generateKeyPair();
    }

    private static IntStream aesKeysizes() {
        return IntStream.of(
                128
//                ,
//                192,
//                256
        );
    }

    @DisplayName("AES/CBC/NoPadding")
    @Nested
    class AesCbcNoPaddingTest {

        private static IntStream keysizes() {
            return aesKeysizes();
        }

        @MethodSource({"keysizes"})
        @ParameterizedTest(name = "[{index}] keysize: {0}")
        void __(final int keysize, @TempDir final File dir)
                throws IOException, NoSuchPaddingException, NoSuchAlgorithmException,
                       InvalidAlgorithmParameterException, InvalidKeyException,
                       IllegalBlockSizeException, BadPaddingException {
            // ------------------------------------------------------------------------------- files
            final var plainFile = File.createTempFile("tmp", "tmp", dir);
            final var encryptedFile = File.createTempFile("tmp", "tmp", dir);
            final var decryptedFile = File.createTempFile("tmp", "tmp", dir);
            // ------------------------------------------------------------------------------ cipher
            final var algorithm = "AES";
            final var mode = "CBC";
            final var padding = "NoPadding";
            final var transformation = algorithm + '/' + mode + '/' + padding;
            final var cipher = Cipher.getInstance(transformation);
            final var blockSize = cipher.getBlockSize();
            assert blockSize == 128 >> 3;
            // --------------------------------------------------------------------------------- key
            final var key = generateKey(algorithm, keysize);
            // ------------------------------------------------------------------------------ params
            final AlgorithmParameterSpec params;
            {
                final var iv = new byte[blockSize];
                ThreadLocalRandom.current().nextBytes(iv);
                params = new IvParameterSpec(iv);
            }
            // ----------------------------------------------------------------------------- encrypt
            {
                FileUtils.writeRandomBytes(
                        plainFile,
                        false,
                        ThreadLocalRandom.current().nextInt(8192) / blockSize * blockSize,
                        new byte[1024]
                );
                log.debug("plainFile.length: {}", plainFile.length());
                cipher.init(Cipher.ENCRYPT_MODE, key, params);
                try (var input = new FileInputStream(plainFile);
                     var output = new FileOutputStream(encryptedFile)) {
                    final long bytes = CipherUtils.update(
                            cipher,
                            input,
                            new byte[1024],
                            output
                    );
                    assertThat(bytes).isEqualTo(plainFile.length());
                    output.write(cipher.doFinal());
                    output.flush();
                }
            }
            // ----------------------------------------------------------------------------- decrypt
            {
                cipher.init(Cipher.DECRYPT_MODE, key, params);
                try (var input = new FileInputStream(encryptedFile);
                     var outputStream = new FileOutputStream(decryptedFile)) {
                    final var bytes = CipherUtils.update(
                            cipher,
                            input,
                            new byte[1024],
                            outputStream
                    );
                    assertThat(bytes).isEqualTo(encryptedFile.length());
                    outputStream.write(cipher.doFinal());
                    outputStream.flush();
                }
                log.debug("decryptedFile.length: {}", decryptedFile.length());
                assertThat(decryptedFile).hasSize(plainFile.length());
            }
            // ------------------------------------------------------------------------------ verify
            {
                final var buffer = new byte[1024];
                final var plainFileDigest = MessageDigestUtils.getDigest(
                        algorithm,
                        plainFile,
                        buffer
                );
                log.debug("plainFile.digest: {}", HexFormat.of().formatHex(plainFileDigest));
                final byte[] decryptedFileDigest = MessageDigestUtils.getDigest(
                        algorithm,
                        decryptedFile,
                        buffer
                );
                log.debug("decryptedFile.digest: {}",
                          HexFormat.of().formatHex(decryptedFileDigest));
                assertThat(decryptedFileDigest).isEqualTo(plainFileDigest);
            }
        }

        @MethodSource({"keysizes"})
        @ParameterizedTest(name = "[{index}] keysize: {0}")
        void __(final int keysize, @TempDir final Path dir)
                throws IOException, NoSuchPaddingException, NoSuchAlgorithmException,
                       InvalidAlgorithmParameterException, InvalidKeyException,
                       IllegalBlockSizeException, BadPaddingException {
            // ------------------------------------------------------------------------------- paths
            final var plainPath = Files.createTempFile(dir, null, null);
            final var encryptedPath = Files.createTempFile(dir, null, null);
            final var decryptedPath = Files.createTempFile(dir, null, null);
            // ------------------------------------------------------------------------------ cipher
            final var algorithm = "AES";
            final var mode = "CBC";
            final var padding = "NoPadding";
            final var transformation = algorithm + '/' + mode + '/' + padding;
            final var cipher = Cipher.getInstance(transformation);
            final var blockSize = cipher.getBlockSize();
            assert blockSize == 128 >> 3;
            // --------------------------------------------------------------------------------- key
            final var key = generateKey(algorithm, keysize);
            // ------------------------------------------------------------------------------ params
            final AlgorithmParameterSpec params;
            {
                final var iv = new byte[blockSize];
                SecureRandom.getInstanceStrong().nextBytes(iv);
                params = new IvParameterSpec(iv);
            }
            // ----------------------------------------------------------------------------- encrypt
            {
                PathUtils.writeRandomBytes(
                        plainPath,
                        ThreadLocalRandom.current().nextInt(8192) / blockSize * blockSize,
                        ByteBuffer.allocate(1024),
                        0L
                );
                log.debug("plainPath.size: {}", Files.size(plainPath));
                cipher.init(Cipher.ENCRYPT_MODE, key, params);
                try (var readable = FileChannel.open(plainPath, StandardOpenOption.READ);
                     var writable = FileChannel.open(encryptedPath, StandardOpenOption.WRITE)) {
                    final long bytes = CipherUtils.update(
                            cipher,
                            readable,
                            ByteBuffer.allocate(1024),
                            writable
                    );
                    assertThat(bytes).isEqualTo(Files.size(plainPath));
                    for (var b = ByteBuffer.wrap(cipher.doFinal()); b.hasRemaining(); ) {
                        final var w = writable.write(b);
                        assert w >= 0;
                    }
                    writable.force(false);
                }
            }
            // ----------------------------------------------------------------------------- decrypt
            {
                cipher.init(Cipher.DECRYPT_MODE, key, params);
                try (var readable = FileChannel.open(encryptedPath, StandardOpenOption.READ);
                     var writable = FileChannel.open(decryptedPath, StandardOpenOption.WRITE)) {
                    final var bytes = CipherUtils.update(
                            cipher,
                            readable,
                            ByteBuffer.allocate(1024),
                            writable
                    );
                    assertThat(bytes).isEqualTo(Files.size(encryptedPath));
                    for (final var b = ByteBuffer.wrap(cipher.doFinal()); b.hasRemaining(); ) {
                        final var w = writable.write(b);
                        assert w >= 0;
                    }
                    writable.force(false);
                }
                log.debug("decryptedPath.size: {}", Files.size(decryptedPath));
                assertThat(decryptedPath).hasSize(Files.size(plainPath));
            }
            // ------------------------------------------------------------------------------ verify
            {
                final var buffer = ByteBuffer.allocate(1024);
                final var plainPathDigest = MessageDigestUtils.getDigest(
                        algorithm,
                        plainPath,
                        buffer
                );
                log.debug("plainPath.digest: {}", HexFormat.of().formatHex(plainPathDigest));
                final var decryptedPathDigest = MessageDigestUtils.getDigest(
                        algorithm,
                        decryptedPath,
                        buffer
                );
                log.debug("decryptedPath.digest: {}",
                          HexFormat.of().formatHex(decryptedPathDigest));
                assertThat(decryptedPathDigest).isEqualTo(plainPathDigest);
            }
        }
    }

    @DisplayName("AES/CBC/PKCS5Padding")
    @Nested
    class AesCbcPkcs5PaddingTest {

        private static IntStream keysizes() {
            return aesKeysizes();
        }

        @MethodSource({"keysizes"})
        @ParameterizedTest(name = "[{index}] keysize: {0}")
        void __(final int keysize, @TempDir final File dir)
                throws IOException, NoSuchPaddingException, NoSuchAlgorithmException,
                       InvalidAlgorithmParameterException, InvalidKeyException,
                       IllegalBlockSizeException, BadPaddingException {
            // ------------------------------------------------------------------------------- files
            final var plainFile = File.createTempFile("tmp", "tmp", dir);
            final var encryptedFile = File.createTempFile("tmp", "tmp", dir);
            final var decryptedFile = File.createTempFile("tmp", "tmp", dir);
            // ------------------------------------------------------------------------------ cipher
            final var algorithm = "AES";
            final var mode = "CBC";
            final var padding = "PKCS5Padding";
            final var transformation = algorithm + '/' + mode + '/' + padding;
            final var cipher = Cipher.getInstance(transformation);
            final var blockSize = cipher.getBlockSize();
            assert blockSize == 128 >> 3;
            // --------------------------------------------------------------------------------- key
            final var key = generateKey(algorithm, keysize);
            // ------------------------------------------------------------------------------ params
            final AlgorithmParameterSpec params;
            {
                final var iv = new byte[blockSize];
                SecureRandom.getInstanceStrong().nextBytes(iv);
                params = new IvParameterSpec(iv);
            }
            // ----------------------------------------------------------------------------- encrypt
            {
                FileUtils.writeRandomBytes(
                        plainFile,
                        false,
                        ThreadLocalRandom.current().nextInt(8192),
                        new byte[1024]
                );
                log.debug("plainFile.length: {}", plainFile.length());
                cipher.init(Cipher.ENCRYPT_MODE, key, params);
                try (var input = new FileInputStream(plainFile);
                     var output = new FileOutputStream(encryptedFile)) {
                    final long bytes = CipherUtils.update(
                            cipher,
                            input,
                            new byte[1024],
                            output
                    );
                    assertThat(bytes).isEqualTo(plainFile.length());
                    output.write(cipher.doFinal());
                    output.flush();
                }
            }
            // ----------------------------------------------------------------------------- decrypt
            {
                cipher.init(Cipher.DECRYPT_MODE, key, params);
                try (var input = new FileInputStream(encryptedFile);
                     var outputStream = new FileOutputStream(decryptedFile)) {
                    final var bytes = CipherUtils.update(
                            cipher,
                            input,
                            new byte[1024],
                            outputStream
                    );
                    assertThat(bytes).isEqualTo(encryptedFile.length());
                    outputStream.write(cipher.doFinal());
                    outputStream.flush();
                }
                log.debug("decryptedFile.length: {}", decryptedFile.length());
                assertThat(decryptedFile).hasSize(plainFile.length());
            }
            // ------------------------------------------------------------------------------ verify
            {
                final var digest = MessageDigest.getInstance("SHA-1");
                final var buffer = new byte[1024];
                final var plainFileDigest = MessageDigestUtils.getDigest(
                        algorithm,
                        plainFile,
                        buffer
                );
                log.debug("plainFile.digest: {}", HexFormat.of().formatHex(plainFileDigest));
                final var decryptedFileDigest = MessageDigestUtils.getDigest(
                        algorithm,
                        decryptedFile,
                        buffer
                );
                log.debug("decryptedFile.digest: {}",
                          HexFormat.of().formatHex(decryptedFileDigest));
                assertThat(decryptedFileDigest).isEqualTo(plainFileDigest);
            }
        }

        @MethodSource({"keysizes"})
        @ParameterizedTest(name = "[{index}] keysize: {0}")
        void __(final int keysize, @TempDir final Path dir)
                throws IOException, NoSuchPaddingException, NoSuchAlgorithmException,
                       InvalidAlgorithmParameterException, InvalidKeyException,
                       IllegalBlockSizeException, BadPaddingException {
            // ------------------------------------------------------------------------------- paths
            final var plainPath = Files.createTempFile(dir, null, null);
            final var encryptedPath = Files.createTempFile(dir, null, null);
            final var decryptedPath = Files.createTempFile(dir, null, null);
            // ------------------------------------------------------------------------------ cipher
            final var algorithm = "AES";
            final var mode = "CBC";
            final var padding = "PKCS5Padding";
            final var transformation = algorithm + '/' + mode + '/' + padding;
            final var cipher = Cipher.getInstance(transformation);
            final var blockSize = cipher.getBlockSize();
            assert blockSize == 128 >> 3;
            // --------------------------------------------------------------------------------- key
            final var key = generateKey(algorithm, keysize);
            // ------------------------------------------------------------------------------ params
            final AlgorithmParameterSpec params;
            {
                final var iv = new byte[blockSize];
                SecureRandom.getInstanceStrong().nextBytes(iv);
                params = new IvParameterSpec(iv);
            }
            // ----------------------------------------------------------------------------- encrypt
            {
                PathUtils.writeRandomBytes(
                        plainPath,
                        ThreadLocalRandom.current().nextInt(8192),
                        ByteBuffer.allocate(1024),
                        0L
                );
                log.debug("plainPath.size: {}", Files.size(plainPath));
                cipher.init(Cipher.ENCRYPT_MODE, key, params);
                try (var readable = FileChannel.open(plainPath, StandardOpenOption.READ);
                     var writable = FileChannel.open(encryptedPath, StandardOpenOption.WRITE)) {
                    final long bytes = CipherUtils.update(
                            cipher,
                            readable,
                            ByteBuffer.allocate(1024),
                            writable
                    );
                    assertThat(bytes).isEqualTo(Files.size(plainPath));
                    for (var buffer = ByteBuffer.wrap(cipher.doFinal());
                         buffer.hasRemaining(); ) {
                        final var w = writable.write(buffer);
                        assert w >= 0;
                    }
                    writable.force(false);
                }
            }
            // ----------------------------------------------------------------------------- decrypt
            {
                cipher.init(Cipher.DECRYPT_MODE, key, params);
                try (var readable = FileChannel.open(encryptedPath, StandardOpenOption.READ);
                     var writable = FileChannel.open(decryptedPath, StandardOpenOption.WRITE)) {
                    final var bytes = CipherUtils.update(
                            cipher,
                            readable,
                            ByteBuffer.allocate(1024),
                            writable
                    );
                    assertThat(bytes).isEqualTo(Files.size(encryptedPath));
                    for (final var b = ByteBuffer.wrap(cipher.doFinal()); b.hasRemaining(); ) {
                        final var w = writable.write(b);
                        assert w >= 0;
                    }
                    writable.force(false);
                }
                log.debug("decryptedPath.size: {}", Files.size(decryptedPath));
                assertThat(decryptedPath).hasSize(Files.size(plainPath));
            }
            // ------------------------------------------------------------------------------ verify
            {
                final var buffer = ByteBuffer.allocate(1024);
                final var plainPathDigest = MessageDigestUtils.getDigest(
                        algorithm,
                        plainPath,
                        buffer
                );
                log.debug("plainPath.digest: {}", HexFormat.of().formatHex(plainPathDigest));
                final var decryptedPathDigest = MessageDigestUtils.getDigest(
                        algorithm,
                        decryptedPath,
                        buffer
                );
                log.debug("decryptedPath.digest: {}",
                          HexFormat.of().formatHex(decryptedPathDigest));
                assertThat(decryptedPathDigest).isEqualTo(plainPathDigest);
            }
        }
    }

    @DisplayName("AES/ECB/NoPadding")
    @Nested
    class AesEcbNoPaddingTest {

        private static IntStream keysizes() {
            return aesKeysizes();
        }

        @MethodSource({"keysizes"})
        @ParameterizedTest(name = "[{index}] keysize: {0}")
        void __(final int keysize, @TempDir final File dir)
                throws IOException, NoSuchPaddingException, NoSuchAlgorithmException,
                       InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
            // ------------------------------------------------------------------------------- files
            final var plainFile = File.createTempFile("tmp", "tmp", dir);
            final var encryptedFile = File.createTempFile("tmp", "tmp", dir);
            final var decryptedFile = File.createTempFile("tmp", "tmp", dir);
            // ------------------------------------------------------------------------------ cipher
            final var algorithm = "AES";
            final var mode = "ECB";
            final var padding = "NoPadding";
            final var transformation = algorithm + '/' + mode + '/' + padding;
            final var cipher = Cipher.getInstance(transformation);
            final var blockSize = cipher.getBlockSize();
            assert blockSize == 128 >> 3;
            // --------------------------------------------------------------------------------- key
            final var key = generateKey(algorithm, keysize);
            // ----------------------------------------------------------------------------- encrypt
            {
                FileUtils.writeRandomBytes(
                        plainFile,
                        false,
                        ThreadLocalRandom.current().nextInt(8192) / blockSize * blockSize,
                        new byte[1024]
                );
                log.debug("plainFile.length: {}", plainFile.length());
                cipher.init(Cipher.ENCRYPT_MODE, key);
                try (var input = new FileInputStream(plainFile);
                     var output = new FileOutputStream(encryptedFile)) {
                    final long bytes = CipherUtils.update(
                            cipher,
                            input,
                            new byte[1024],
                            output
                    );
                    assertThat(bytes).isEqualTo(plainFile.length());
                    output.write(cipher.doFinal());
                    output.flush();
                }
            }
            // ----------------------------------------------------------------------------- decrypt
            {
                cipher.init(Cipher.DECRYPT_MODE, key);
                try (var input = new FileInputStream(encryptedFile);
                     var outputStream = new FileOutputStream(decryptedFile)) {
                    final var bytes = CipherUtils.update(
                            cipher,
                            input,
                            new byte[1024],
                            outputStream
                    );
                    assertThat(bytes).isEqualTo(encryptedFile.length());
                    outputStream.write(cipher.doFinal());
                    outputStream.flush();
                }
                log.debug("decryptedFile.length: {}", decryptedFile.length());
                assertThat(decryptedFile).hasSize(plainFile.length());
            }
            // ------------------------------------------------------------------------------ verify
            {
                final byte[] plainFileDigest = MessageDigestUtils.getDigest(
                        algorithm,
                        plainFile,
                        new byte[1024]
                );
                log.debug("plainFileDigest: {}", HexFormat.of().formatHex(plainFileDigest));
                final byte[] decryptedFileDigest = MessageDigestUtils.getDigest(
                        algorithm,
                        decryptedFile,
                        new byte[1024]
                );
                log.debug("decryptedFileDigest: {}", HexFormat.of().formatHex(decryptedFileDigest));
                assertThat(decryptedFileDigest).isEqualTo(plainFileDigest);
            }
        }

        @MethodSource({"keysizes"})
        @ParameterizedTest(name = "[{index}] keysize: {0}")
        void __(final int keysize, @TempDir final Path dir)
                throws IOException, NoSuchPaddingException, NoSuchAlgorithmException,
                       InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
            // ------------------------------------------------------------------------------- paths
            final var plainPath = Files.createTempFile(dir, null, null);
            final var encryptedPath = Files.createTempFile(dir, null, null);
            final var decryptedPath = Files.createTempFile(dir, null, null);
            // ------------------------------------------------------------------------------ cipher
            final var algorithm = "AES";
            final var mode = "ECB";
            final var padding = "NoPadding";
            final var transformation = algorithm + '/' + mode + '/' + padding;
            final var cipher = Cipher.getInstance(transformation);
            final var blockSize = cipher.getBlockSize();
            assert blockSize == 128 >> 3;
            // --------------------------------------------------------------------------------- key
            final var key = generateKey(algorithm, keysize);
            // ----------------------------------------------------------------------------- encrypt
            {
                PathUtils.writeRandomBytes(
                        plainPath,
                        ThreadLocalRandom.current().nextInt(8192) / blockSize * blockSize,
                        ByteBuffer.allocate(1024),
                        0L
                );
                log.debug("plainPath.size: {}", Files.size(plainPath));
                cipher.init(Cipher.ENCRYPT_MODE, key);
                try (var readable = FileChannel.open(plainPath, StandardOpenOption.READ);
                     var writable = FileChannel.open(encryptedPath, StandardOpenOption.WRITE)) {
                    final long bytes = CipherUtils.update(
                            cipher,
                            readable,
                            ByteBuffer.allocate(1024),
                            writable
                    );
                    assertThat(bytes).isEqualTo(Files.size(plainPath));
                    for (var buffer = ByteBuffer.wrap(cipher.doFinal());
                         buffer.hasRemaining(); ) {
                        final var w = writable.write(buffer);
                        assert w >= 0;
                    }
                    writable.force(false);
                }
            }
            // ----------------------------------------------------------------------------- decrypt
            {
                cipher.init(Cipher.DECRYPT_MODE, key);
                try (var readable = FileChannel.open(encryptedPath, StandardOpenOption.READ);
                     var writable = FileChannel.open(decryptedPath, StandardOpenOption.WRITE)) {
                    final var bytes = CipherUtils.update(
                            cipher,
                            readable,
                            ByteBuffer.allocate(1024),
                            writable
                    );
                    assertThat(bytes).isEqualTo(Files.size(encryptedPath));
                    for (final var buffer = ByteBuffer.wrap(cipher.doFinal());
                         buffer.hasRemaining(); ) {
                        final var w = writable.write(buffer);
                        assert w >= 0;
                    }
                    writable.force(false);
                }
                log.debug("decryptedPath.size: {}", Files.size(decryptedPath));
                assertThat(decryptedPath).hasSize(Files.size(plainPath));
            }
            // ------------------------------------------------------------------------------ verify
            {
                final var digest = MessageDigest.getInstance("SHA-1");
                final var plainPathDigest = MessageDigestUtils.getDigest(
                        algorithm,
                        plainPath,
                        ByteBuffer.allocate(1024)
                );
                log.debug("plainPathDigest: {}", HexFormat.of().formatHex(plainPathDigest));
                final var decryptedPathDigest = MessageDigestUtils.getDigest(
                        algorithm,
                        decryptedPath,
                        ByteBuffer.allocate(1024)
                );
                log.debug("decryptedPathDigest: {}", HexFormat.of().formatHex(decryptedPathDigest));
                assertThat(decryptedPathDigest).isEqualTo(plainPathDigest);
            }
        }
    }

    @DisplayName("AES/ECB/PKCS5Padding")
    @Nested
    class AesEcbPkcs5PaddingTest {

        private static IntStream keysizes() {
            return aesKeysizes();
        }

        @MethodSource({"keysizes"})
        @ParameterizedTest(name = "[{index}] keysize: {0}")
        void __(final int keysize, @TempDir final File dir)
                throws IOException, NoSuchPaddingException, NoSuchAlgorithmException,
                       InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
            // ------------------------------------------------------------------------------- files
            final var plainFile = File.createTempFile("tmp", "tmp", dir);
            final var encryptedFile = File.createTempFile("tmp", "tmp", dir);
            final var decryptedFile = File.createTempFile("tmp", "tmp", dir);
            // ------------------------------------------------------------------------------ cipher
            final var algorithm = "AES";
            final var mode = "ECB";
            final var padding = "PKCS5Padding";
            final var transformation = algorithm + '/' + mode + '/' + padding;
            final var cipher = Cipher.getInstance(transformation);
            final var blockSize = cipher.getBlockSize();
            assert blockSize == 128 >> 3;
            // --------------------------------------------------------------------------------- key
            final var key = generateKey(algorithm, keysize);
            // ----------------------------------------------------------------------------- encrypt
            FileUtils.writeRandomBytes(
                    plainFile,
                    false,
                    ThreadLocalRandom.current().nextInt(8192),
                    new byte[1024]
            );
            log.debug("plainFile.length: {}", plainFile.length());
            cipher.init(Cipher.ENCRYPT_MODE, key);
            try (var input = new FileInputStream(plainFile);
                 var output = new FileOutputStream(encryptedFile)) {
                final long bytes = CipherUtils.update(
                        cipher,
                        input,
                        new byte[1024],
                        output
                );
                assertThat(bytes).isEqualTo(plainFile.length());
                output.write(cipher.doFinal());
                output.flush();
            }
            // ----------------------------------------------------------------------------- decrypt
            cipher.init(Cipher.DECRYPT_MODE, key);
            try (var input = new FileInputStream(encryptedFile);
                 var outputStream = new FileOutputStream(decryptedFile)) {
                final var bytes = CipherUtils.update(
                        cipher,
                        input,
                        new byte[1024],
                        outputStream
                );
                assertThat(bytes).isEqualTo(encryptedFile.length());
                outputStream.write(cipher.doFinal());
                outputStream.flush();
            }
            log.debug("decryptedFile.length: {}", decryptedFile.length());
            assertThat(decryptedFile).hasSize(plainFile.length());
            // ------------------------------------------------------------------------------ verify
            final var plainFileDigest = MessageDigestUtils.getDigest(
                    algorithm,
                    plainFile,
                    new byte[1024]
            );
            log.debug("plainFileDigest: {}", HexFormat.of().formatHex(plainFileDigest));
            final var decryptedFileDigest = MessageDigestUtils.getDigest(
                    algorithm,
                    decryptedFile,
                    new byte[1024]
            );
            log.debug("decryptedFileDigest: {}", HexFormat.of().formatHex(decryptedFileDigest));
            assertThat(decryptedFileDigest).isEqualTo(plainFileDigest);
        }

        @MethodSource({"keysizes"})
        @ParameterizedTest(name = "[{index}] keysize: {0}")
        void __(final int keysize, @TempDir final Path dir)
                throws IOException, NoSuchPaddingException, NoSuchAlgorithmException,
                       InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
            // ------------------------------------------------------------------------------- paths
            final var plainPath = Files.createTempFile(dir, null, null);
            final var encryptedPath = Files.createTempFile(dir, null, null);
            final var decryptedPath = Files.createTempFile(dir, null, null);
            // ------------------------------------------------------------------------------ cipher
            final var algorithm = "AES";
            final var mode = "ECB";
            final var padding = "PKCS5Padding";
            final var transformation = algorithm + '/' + mode + '/' + padding;
            final var cipher = Cipher.getInstance(transformation);
            final var blockSize = cipher.getBlockSize();
            assert blockSize == 128 >> 3 : "block size is always 128 regardless of the key size";
            // --------------------------------------------------------------------------------- key
            final var key = generateKey(algorithm, keysize);
            // ----------------------------------------------------------------------------- encrypt
            PathUtils.writeRandomBytes(
                    plainPath,
                    ThreadLocalRandom.current().nextInt(8192),
                    ByteBuffer.allocate(1024),
                    0L
            );
            log.debug("plainPath.size: {}", Files.size(plainPath));
            cipher.init(Cipher.ENCRYPT_MODE, key);
            try (var readable = FileChannel.open(plainPath, StandardOpenOption.READ);
                 var writable = FileChannel.open(encryptedPath,
                                                 StandardOpenOption.WRITE)) {
                final long bytes = CipherUtils.update(
                        cipher,
                        readable,
                        ByteBuffer.allocate(1024),
                        writable
                );
                assertThat(bytes).isEqualTo(Files.size(plainPath));
                for (var buffer = ByteBuffer.wrap(cipher.doFinal());
                     buffer.hasRemaining(); ) {
                    final var w = writable.write(buffer);
                    assert w >= 0;
                }
                writable.force(false);
            }
            // ----------------------------------------------------------------------------- decrypt
            cipher.init(Cipher.DECRYPT_MODE, key);
            try (var readable = FileChannel.open(encryptedPath, StandardOpenOption.READ);
                 var writable = FileChannel.open(decryptedPath, StandardOpenOption.WRITE)) {
                final var bytes = CipherUtils.update(
                        cipher,
                        readable,
                        ByteBuffer.allocate(1024),
                        writable
                );
                assertThat(bytes).isEqualTo(Files.size(encryptedPath));
                for (final var b = ByteBuffer.wrap(cipher.doFinal()); b.hasRemaining(); ) {
                    final var w = writable.write(b);
                    assert w >= 0;
                }
                writable.force(false);
            }
            log.debug("decryptedPath.size: {}", Files.size(decryptedPath));
            assertThat(decryptedPath).hasSize(Files.size(plainPath));
            // ------------------------------------------------------------------------------ verify
            final var plainFileDigest = MessageDigestUtils.getDigest(
                    algorithm,
                    plainPath,
                    ByteBuffer.allocate(1024)
            );
            log.debug("plainPathDigest: {}", HexFormat.of().formatHex(plainFileDigest));
            final var decryptedFileDigest = MessageDigestUtils.getDigest(
                    algorithm,
                    decryptedPath,
                    ByteBuffer.allocate(1024)
            );
            log.debug("decryptedPathDigest: {}", HexFormat.of().formatHex(decryptedFileDigest));
            assertThat(decryptedFileDigest).isEqualTo(plainFileDigest);
        }
    }

    @DisplayName("AES/GCM/NoPadding")
    @Nested
    class AesGcmNoPaddingTest {

        private static IntStream keysizes() {
            return aesKeysizes();
        }

        @MethodSource({"keysizes"})
        @ParameterizedTest(name = "[{index}] keysize: {0}")
        void __(final int keysize, @TempDir final File dir)
                throws IOException, NoSuchPaddingException, NoSuchAlgorithmException,
                       InvalidAlgorithmParameterException, InvalidKeyException,
                       IllegalBlockSizeException, BadPaddingException {
            // ------------------------------------------------------------------------------- files
            final var plainFile = File.createTempFile("tmp", "tmp", dir);
            final var encryptedFile = File.createTempFile("tmp", "tmp", dir);
            final var decryptedFile = File.createTempFile("tmp", "tmp", dir);
            // ------------------------------------------------------------------------------ cipher
            final var algorithm = "AES";
            final var mode = "GCM";
            final var padding = "NoPadding";
            final var transformation = algorithm + '/' + mode + '/' + padding;
            final var cipher = Cipher.getInstance(transformation);
            final var blockSize = cipher.getBlockSize();
            assert blockSize == 128 >> 3;
            // --------------------------------------------------------------------------------- key
            final var key = generateKey(algorithm, keysize);
            // ------------------------------------------------------------------------------ params
            final AlgorithmParameterSpec params;
            {
                final var tLens = new int[] {128, 120, 112, 104, 96};
                final var tLen = tLens[ThreadLocalRandom.current().nextInt(tLens.length)];
                final var iv = new byte[blockSize];
                ThreadLocalRandom.current().nextBytes(iv);
                params = new GCMParameterSpec(tLen, iv);
            }
            // --------------------------------------------------------------------------------- aad
            final var aad = new byte[blockSize];
            SecureRandom.getInstanceStrong().nextBytes(aad);
            // ----------------------------------------------------------------------------- encrypt
            FileUtils.writeRandomBytes(
                    plainFile,
                    false,
                    ThreadLocalRandom.current().nextInt(8192) / blockSize * blockSize,
                    new byte[1024]
            );
            log.debug("plainFile.length: {}", plainFile.length());
            cipher.init(Cipher.ENCRYPT_MODE, key, params);
            cipher.updateAAD(aad);
            try (var input = new FileInputStream(plainFile);
                 var output = new FileOutputStream(encryptedFile)) {
                final long bytes = CipherUtils.update(
                        cipher,
                        input,
                        new byte[1024],
                        output
                );
                assertThat(bytes).isEqualTo(plainFile.length());
                output.write(cipher.doFinal());
                output.flush();
            }
            // ----------------------------------------------------------------------------- decrypt
            cipher.init(Cipher.DECRYPT_MODE, key, params);
            cipher.updateAAD(aad);
            try (var input = new FileInputStream(encryptedFile);
                 var outputStream = new FileOutputStream(decryptedFile)) {
                final var bytes = CipherUtils.update(
                        cipher,
                        input,
                        new byte[1024],
                        outputStream
                );
                assertThat(bytes).isEqualTo(encryptedFile.length());
                outputStream.write(cipher.doFinal());
                outputStream.flush();
            }
            log.debug("decryptedFile.length: {}", decryptedFile.length());
            assertThat(decryptedFile).hasSize(plainFile.length());
            // ------------------------------------------------------------------------------ verify
            final var plainFileDigest = MessageDigestUtils.getDigest(
                    algorithm,
                    plainFile,
                    new byte[1024]
            );
            log.debug("plainFileDigest: {}", HexFormat.of().formatHex(plainFileDigest));
            final var decryptedFileDigest = MessageDigestUtils.getDigest(
                    algorithm,
                    decryptedFile,
                    new byte[1024]
            );
            log.debug("decryptedFileDigest: {}", HexFormat.of().formatHex(decryptedFileDigest));
            assertThat(decryptedFileDigest).isEqualTo(plainFileDigest);
        }

        @MethodSource({"keysizes"})
        @ParameterizedTest(name = "[{index}] keysize: {0}")
        void __(final int keySize, @TempDir final Path dir)
                throws IOException, NoSuchPaddingException, NoSuchAlgorithmException,
                       InvalidAlgorithmParameterException, InvalidKeyException,
                       IllegalBlockSizeException, BadPaddingException {
            // ------------------------------------------------------------------------------- paths
            final var plainPath = Files.createTempFile(dir, null, null);
            final var encryptedPath = Files.createTempFile(dir, null, null);
            final var decryptedPath = Files.createTempFile(dir, null, null);
            // ------------------------------------------------------------------------------ cipher
            final var algorithm = "AES";
            final var mode = "GCM";
            final var padding = "NoPadding";
            final var transformation = algorithm + '/' + mode + '/' + padding;
            final var cipher = Cipher.getInstance(transformation);
            final var blockSize = cipher.getBlockSize();
            assert blockSize == 128 >> 3;
            // --------------------------------------------------------------------------------- key
            final var key = generateKey(algorithm, keySize);
            // ------------------------------------------------------------------------------ params
            final AlgorithmParameterSpec params;
            {
                final var tLens = new int[] {128, 120, 112, 104, 96};
                final var tLen = tLens[ThreadLocalRandom.current().nextInt(tLens.length)];
                final var iv = new byte[blockSize];
                ThreadLocalRandom.current().nextBytes(iv);
                params = new GCMParameterSpec(tLen, iv);
            }
            // --------------------------------------------------------------------------------- aad
            final var aad = new byte[blockSize];
            SecureRandom.getInstanceStrong().nextBytes(aad);
            // ----------------------------------------------------------------------------- encrypt
            PathUtils.writeRandomBytes(
                    plainPath,
                    ThreadLocalRandom.current().nextInt(8192) / blockSize * blockSize,
                    ByteBuffer.allocate(1024),
                    0L
            );
            log.debug("palinPath.size: {}", Files.size(plainPath));
            cipher.init(Cipher.ENCRYPT_MODE, key, params);
            cipher.updateAAD(aad);
            try (var readable = FileChannel.open(plainPath, StandardOpenOption.READ);
                 var writable = FileChannel.open(encryptedPath, StandardOpenOption.WRITE)) {
                final long bytes = CipherUtils.update(
                        cipher,
                        readable,
                        ByteBuffer.allocate(1024),
                        writable
                );
                assertThat(bytes).isEqualTo(Files.size(plainPath));
                for (var buffer = ByteBuffer.wrap(cipher.doFinal());
                     buffer.hasRemaining(); ) {
                    final var w = writable.write(buffer);
                    assert w >= 0;
                }
                writable.force(false);
            }
            // ----------------------------------------------------------------------------- decrypt
            cipher.init(Cipher.DECRYPT_MODE, key, params);
            cipher.updateAAD(aad);
            try (var readable = FileChannel.open(encryptedPath, StandardOpenOption.READ);
                 var writable = FileChannel.open(decryptedPath, StandardOpenOption.WRITE)) {
                final var bytes = CipherUtils.update(
                        cipher,
                        readable,
                        ByteBuffer.allocate(1024),
                        writable
                );
                assertThat(bytes).isEqualTo(Files.size(encryptedPath));
                for (final var buffer = ByteBuffer.wrap(cipher.doFinal());
                     buffer.hasRemaining(); ) {
                    final var w = writable.write(buffer);
                    assert w >= 0;
                }
                writable.force(false);
            }
            log.debug("decryptedPath.size: {}", Files.size(decryptedPath));
            assertThat(decryptedPath).hasSize(Files.size(plainPath));
            // ------------------------------------------------------------------------------ verify
            final var plainFileDigest = MessageDigestUtils.getDigest(
                    algorithm,
                    plainPath,
                    ByteBuffer.allocate(1024)
            );
            log.debug("plainPathDigest: {}", HexFormat.of().formatHex(plainFileDigest));
            final var decryptedFileDigest = MessageDigestUtils.getDigest(
                    algorithm,
                    decryptedPath,
                    ByteBuffer.allocate(1024)
            );
            log.debug("decryptedPathDigest: {}", HexFormat.of().formatHex(decryptedFileDigest));
            assertThat(decryptedFileDigest).isEqualTo(plainFileDigest);
        }
    }

    private static IntStream desKeysizes() {
        return IntStream.of(
                168
        );
    }

    // https://www.thalesdocs.com/gphsm/ptk/5.7/docs/Content/PTK-J/Ciphers/DESede.htm
    @DisplayName("DESede/CBC/NoPadding")
    @Nested
    class DesedeCbcNoPaddingTest {

        private static IntStream keysizes() {
            return desKeysizes();
        }

        @MethodSource({"keysizes"})
        @ParameterizedTest(name = "[{index}] keysize: {0}")
        void __(final int keysize, @TempDir final File dir)
                throws IOException, NoSuchPaddingException, NoSuchAlgorithmException,
                       InvalidAlgorithmParameterException, InvalidKeyException,
                       IllegalBlockSizeException, BadPaddingException {
            // ------------------------------------------------------------------------------- files
            final var plainFile = _JavaIoUtils.createTempFileInAndWriteSome(dir);
            final var encryptedFile = File.createTempFile("tmp", "tmp", dir);
            final var decryptedFile = File.createTempFile("tmp", "tmp", dir);
            // ------------------------------------------------------------------------------ cipher
            final var algorithm = "DESede";
            final var mode = "CBC";
            final var padding = "NoPadding";
            final var transformation = algorithm + '/' + mode + '/' + padding;
            final var cipher = Cipher.getInstance(transformation);
            final var blockSize = cipher.getBlockSize();
            assert blockSize == 64 >> 3;
            {
                final var required = (int) (blockSize - (plainFile.length() % blockSize));
                try (var stream = new FileOutputStream(plainFile, true)) {
                    stream.write(new byte[required]);
                    stream.flush();
                }
                assertThat(plainFile.length() % blockSize).isZero();
            }
            // --------------------------------------------------------------------------------- key
            final var key = generateKey(algorithm, keysize);
            // ------------------------------------------------------------------------------ params
            final AlgorithmParameterSpec params;
            {
                final var iv = new byte[blockSize];
                ThreadLocalRandom.current().nextBytes(iv);
                params = new IvParameterSpec(iv);
            }
            // ----------------------------------------------------------------------------- encrypt
            {
                cipher.init(Cipher.ENCRYPT_MODE, key, params);
                try (var input = new FileInputStream(plainFile);
                     var output = new FileOutputStream(encryptedFile)) {
                    final long bytes = CipherUtils.update(
                            cipher,
                            input,
                            new byte[1024],
                            output
                    );
                    assertThat(bytes).isEqualTo(plainFile.length());
                    output.write(cipher.doFinal());
                    output.flush();
                }
            }
            // ----------------------------------------------------------------------------- decrypt
            {
                cipher.init(Cipher.DECRYPT_MODE, key, params);
                try (var input = new FileInputStream(encryptedFile);
                     var outputStream = new FileOutputStream(decryptedFile)) {
                    final var bytes = CipherUtils.update(
                            cipher,
                            input,
                            new byte[1024],
                            outputStream
                    );
                    assertThat(bytes).isEqualTo(encryptedFile.length());
                    outputStream.write(cipher.doFinal());
                    outputStream.flush();
                }
                assertThat(decryptedFile).hasSize(plainFile.length());
            }
            // ------------------------------------------------------------------------------ verify
            {
                final var plainFileDigest = MessageDigestUtils.getDigest(
                        algorithm,
                        plainFile,
                        new byte[1024]
                );
                final var decryptedFileDigest = MessageDigestUtils.getDigest(
                        algorithm,
                        decryptedFile,
                        new byte[1024]
                );
                assertThat(decryptedFileDigest).isEqualTo(plainFileDigest);
            }
        }

        @MethodSource({"keysizes"})
        @ParameterizedTest(name = "[{index}] keysize: {0}")
        void __(final int keysize, @TempDir final Path dir)
                throws IOException, NoSuchPaddingException, NoSuchAlgorithmException,
                       InvalidAlgorithmParameterException, InvalidKeyException,
                       IllegalBlockSizeException, BadPaddingException {
            // ------------------------------------------------------------------------------- paths
            final var plainPath = JavaNioUtils.createTempFileInAndWriteSome(dir);
            final var encryptedPath = Files.createTempFile(dir, null, null);
            final var decryptedPath = Files.createTempFile(dir, null, null);
            // ------------------------------------------------------------------------------ cipher
            final var algorithm = "DESede";
            final var mode = "CBC";
            final var padding = "NoPadding";
            final var transformation = algorithm + '/' + mode + '/' + padding;
            final var cipher = Cipher.getInstance(transformation);
            final var blockSize = cipher.getBlockSize();
            assert blockSize == 64 >> 3;
            {
                final var required = (int) (blockSize - (Files.size(plainPath) % blockSize));
                try (var channel = FileChannel.open(plainPath, StandardOpenOption.WRITE,
                                                    StandardOpenOption.APPEND)) {
                    for (final var buffer = ByteBuffer.allocate(required);
                         buffer.hasRemaining(); ) {
                        final var w = channel.write(buffer);
                        assert w >= 0;
                    }
                    channel.force(false);
                }
                assertThat(Files.size(plainPath) % blockSize).isZero();
            }
            // --------------------------------------------------------------------------------- key
            final var key = generateKey(algorithm, keysize);
            // ------------------------------------------------------------------------------ params
            final AlgorithmParameterSpec params;
            {
                final var iv = new byte[blockSize];
                SecureRandom.getInstanceStrong().nextBytes(iv);
                params = new IvParameterSpec(iv);
            }
            // ----------------------------------------------------------------------------- encrypt
            {
                cipher.init(Cipher.ENCRYPT_MODE, key, params);
                try (var readable = FileChannel.open(plainPath, StandardOpenOption.READ);
                     var writable = FileChannel.open(encryptedPath, StandardOpenOption.WRITE)) {
                    final long bytes = CipherUtils.update(
                            cipher,
                            readable,
                            ByteBuffer.allocate(1024),
                            writable
                    );
                    assertThat(bytes).isEqualTo(Files.size(plainPath));
                    for (var buffer = ByteBuffer.wrap(cipher.doFinal());
                         buffer.hasRemaining(); ) {
                        final var w = writable.write(buffer);
                        assert w >= 0;
                    }
                    writable.force(false);
                }
            }
            // ----------------------------------------------------------------------------- decrypt
            {
                cipher.init(Cipher.DECRYPT_MODE, key, params);
                try (var readable = FileChannel.open(encryptedPath, StandardOpenOption.READ);
                     var writable = FileChannel.open(decryptedPath, StandardOpenOption.WRITE)) {
                    final var bytes = CipherUtils.update(
                            cipher,
                            readable,
                            ByteBuffer.allocate(1024),
                            writable
                    );
                    assertThat(bytes).isEqualTo(Files.size(encryptedPath));
                    for (final var buffer = ByteBuffer.wrap(cipher.doFinal());
                         buffer.hasRemaining(); ) {
                        final var w = writable.write(buffer);
                        assert w >= 0;
                    }
                    writable.force(false);
                }
                assertThat(decryptedPath).hasSize(Files.size(plainPath));
            }
            // ------------------------------------------------------------------------------ verify
            {
                final var plainFileDigest = MessageDigestUtils.getDigest(
                        algorithm,
                        plainPath,
                        ByteBuffer.allocate(1024)
                );
                final var decryptedFileDigest = MessageDigestUtils.getDigest(
                        algorithm,
                        decryptedPath,
                        ByteBuffer.allocate(1024)
                );
                assertThat(decryptedFileDigest).isEqualTo(plainFileDigest);
            }
        }
    }

    // https://www.thalesdocs.com/gphsm/ptk/5.7/docs/Content/PTK-J/Ciphers/DESede.htm
    @DisplayName("DESede/CBC/PKCS5Padding")
    @Nested
    class DesedeCbcPkcs5PaddingTest {

        private static IntStream keysizes() {
            return desKeysizes();
        }

        @MethodSource({"keysizes"})
        @ParameterizedTest(name = "[{index}] keysize: {0}")
        void __(final int keysize, @TempDir final File dir)
                throws IOException, NoSuchPaddingException, NoSuchAlgorithmException,
                       InvalidAlgorithmParameterException, InvalidKeyException,
                       IllegalBlockSizeException, BadPaddingException {
            // ------------------------------------------------------------------------------- files
            final var plainFile = _JavaIoUtils.createTempFileInAndWriteSome(dir);
            final var encryptedFile = File.createTempFile("tmp", "tmp", dir);
            final var decryptedFile = File.createTempFile("tmp", "tmp", dir);
            // ------------------------------------------------------------------------------ cipher
            final var algorithm = "DESede";
            final var mode = "CBC";
            final var padding = "PKCS5Padding";
            final var transformation = algorithm + '/' + mode + '/' + padding;
            final var cipher = Cipher.getInstance(transformation);
            final var blockSize = cipher.getBlockSize();
            assert blockSize == 64 >> 3 : "block size is always 64 regardless of the key size";
            // --------------------------------------------------------------------------------- key
            final var key = generateKey(algorithm, keysize);
            // ------------------------------------------------------------------------------ params
            final AlgorithmParameterSpec params;
            {
                final var iv = new byte[blockSize];
                ThreadLocalRandom.current().nextBytes(iv);
                params = new IvParameterSpec(iv);
            }
            // ----------------------------------------------------------------------------- encrypt
            {
                cipher.init(Cipher.ENCRYPT_MODE, key, params);
                try (var input = new FileInputStream(plainFile);
                     var output = new FileOutputStream(encryptedFile)) {
                    final long bytes = CipherUtils.update(
                            cipher,
                            input,
                            new byte[1024],
                            output
                    );
                    assertThat(bytes).isEqualTo(plainFile.length());
                    output.write(cipher.doFinal());
                    output.flush();
                }
            }
            // ----------------------------------------------------------------------------- decrypt
            {
                cipher.init(Cipher.DECRYPT_MODE, key, params);
                try (var input = new FileInputStream(encryptedFile);
                     var outputStream = new FileOutputStream(decryptedFile)) {
                    final var bytes = CipherUtils.update(
                            cipher,
                            input,
                            new byte[1024],
                            outputStream
                    );
                    assertThat(bytes).isEqualTo(encryptedFile.length());
                    outputStream.write(cipher.doFinal());
                    outputStream.flush();
                }
                assertThat(decryptedFile).hasSize(plainFile.length());
            }
            // ------------------------------------------------------------------------------ verify
            {
                final var plainFileDigest = MessageDigestUtils.getDigest(
                        algorithm,
                        plainFile,
                        new byte[1024]
                );
                final var decryptedFileDigest = MessageDigestUtils.getDigest(
                        algorithm,
                        decryptedFile,
                        new byte[1024]
                );
                assertThat(decryptedFileDigest).isEqualTo(plainFileDigest);
            }
        }

        @MethodSource({"keysizes"})
        @ParameterizedTest(name = "[{index}] keysize: {0}")
        void __(final int keysize, @TempDir final Path dir)
                throws IOException, NoSuchPaddingException, NoSuchAlgorithmException,
                       InvalidAlgorithmParameterException, InvalidKeyException,
                       IllegalBlockSizeException, BadPaddingException {
            // ------------------------------------------------------------------------------- paths
            final var plainPath = JavaNioUtils.createTempFileInAndWriteSome(dir);
            final var encryptedPath = Files.createTempFile(dir, null, null);
            final var decryptedPath = Files.createTempFile(dir, null, null);
            // ------------------------------------------------------------------------------ cipher
            final var algorithm = "DESede";
            final var mode = "CBC";
            final var padding = "PKCS5Padding";
            final var transformation = algorithm + '/' + mode + '/' + padding;
            final var cipher = Cipher.getInstance(transformation);
            final var blockSize = cipher.getBlockSize();
            assert blockSize == 64 >> 3;
            // --------------------------------------------------------------------------------- key
            final var key = generateKey(algorithm, keysize);
            // ------------------------------------------------------------------------------ params
            final AlgorithmParameterSpec params;
            {
                final var iv = new byte[blockSize];
                SecureRandom.getInstanceStrong().nextBytes(iv);
                params = new IvParameterSpec(iv);
            }
            // ----------------------------------------------------------------------------- encrypt
            {
                cipher.init(Cipher.ENCRYPT_MODE, key, params);
                try (var readable = FileChannel.open(plainPath, StandardOpenOption.READ);
                     var writable = FileChannel.open(encryptedPath, StandardOpenOption.WRITE)) {
                    final long bytes = CipherUtils.update(
                            cipher,
                            readable,
                            ByteBuffer.allocate(1024),
                            writable
                    );
                    assertThat(bytes).isEqualTo(Files.size(plainPath));
                    for (var buffer = ByteBuffer.wrap(cipher.doFinal());
                         buffer.hasRemaining(); ) {
                        final var w = writable.write(buffer);
                        assert w >= 0;
                    }
                    writable.force(false);
                }
            }
            // ----------------------------------------------------------------------------- decrypt
            {
                cipher.init(Cipher.DECRYPT_MODE, key, params);
                try (var readable = FileChannel.open(encryptedPath, StandardOpenOption.READ);
                     var writable = FileChannel.open(decryptedPath, StandardOpenOption.WRITE)) {
                    final var bytes = CipherUtils.update(
                            cipher,
                            readable,
                            ByteBuffer.allocate(1024),
                            writable
                    );
                    assertThat(bytes).isEqualTo(Files.size(encryptedPath));
                    for (final var buffer = ByteBuffer.wrap(cipher.doFinal());
                         buffer.hasRemaining(); ) {
                        final var w = writable.write(buffer);
                        assert w >= 0;
                    }
                    writable.force(false);
                }
                assertThat(decryptedPath).hasSize(Files.size(plainPath));
            }
            // ------------------------------------------------------------------------------ verify
            {
                final var plainFileDigest = MessageDigestUtils.getDigest(
                        algorithm,
                        plainPath,
                        ByteBuffer.allocate(1024)
                );
                final byte[] decryptedFileDigest = MessageDigestUtils.getDigest(
                        algorithm,
                        decryptedPath,
                        ByteBuffer.allocate(1024)
                );
                assertThat(decryptedFileDigest).isEqualTo(plainFileDigest);
            }
        }
    }

    // https://www.thalesdocs.com/gphsm/ptk/5.7/docs/Content/PTK-J/Ciphers/DESede.htm
    @DisplayName("DESede/ECB/NoPadding")
    @Nested
    class DesedeEcbNoPaddingTest {

        private static IntStream keysizes() {
            return desKeysizes();
        }

        @MethodSource({"keysizes"})
        @ParameterizedTest(name = "[{index}] keysize: {0}")
        void __(final int keysize, @TempDir final File dir)
                throws IOException, NoSuchPaddingException, NoSuchAlgorithmException,
                       InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
            // ------------------------------------------------------------------------------- files
            final var plainFile = _JavaIoUtils.createTempFileInAndWriteSome(dir);
            final var encryptedFile = File.createTempFile("tmp", "tmp", dir);
            final var decryptedFile = File.createTempFile("tmp", "tmp", dir);
            // ------------------------------------------------------------------------------ cipher
            final var algorithm = "DESede";
            final var mode = "ECB";
            final var padding = "NoPadding";
            final var transformation = algorithm + '/' + mode + '/' + padding;
            final var cipher = Cipher.getInstance(transformation);
            final var blockSize = cipher.getBlockSize();
            assert blockSize == 64 >> 3 : "block size is always 64 regardless of the key size";
            {
                final var required = (int) (blockSize - (plainFile.length() % blockSize));
                try (var stream = new FileOutputStream(plainFile, true)) {
                    stream.write(new byte[required]);
                    stream.flush();
                }
                assertThat(plainFile.length() % blockSize).isZero();
            }
            // --------------------------------------------------------------------------------- key
            final var key = generateKey(algorithm, keysize);
            // ----------------------------------------------------------------------------- encrypt
            {
                cipher.init(Cipher.ENCRYPT_MODE, key);
                try (var input = new FileInputStream(plainFile);
                     var output = new FileOutputStream(encryptedFile)) {
                    final long bytes = CipherUtils.update(
                            cipher,
                            input,
                            new byte[1024],
                            output
                    );
                    assertThat(bytes).isEqualTo(plainFile.length());
                    output.write(cipher.doFinal());
                    output.flush();
                }
            }
            // ----------------------------------------------------------------------------- decrypt
            {
                cipher.init(Cipher.DECRYPT_MODE, key);
                try (var input = new FileInputStream(encryptedFile);
                     var outputStream = new FileOutputStream(decryptedFile)) {
                    final var bytes = CipherUtils.update(
                            cipher,
                            input,
                            new byte[1024],
                            outputStream
                    );
                    assertThat(bytes).isEqualTo(encryptedFile.length());
                    outputStream.write(cipher.doFinal());
                    outputStream.flush();
                }
                assertThat(decryptedFile).hasSize(plainFile.length());
            }
            // ------------------------------------------------------------------------------ verify
            {
                final var plainFileDigest = MessageDigestUtils.getDigest(
                        algorithm,
                        plainFile,
                        new byte[1024]
                );
                final var decryptedFileDigest = MessageDigestUtils.getDigest(
                        algorithm,
                        decryptedFile,
                        new byte[1024]
                );
                assertThat(decryptedFileDigest).isEqualTo(plainFileDigest);
            }
        }

        @MethodSource({"keysizes"})
        @ParameterizedTest(name = "[{index}] keysize: {0}")
        void __(final int keysize, @TempDir final Path dir)
                throws IOException, NoSuchPaddingException, NoSuchAlgorithmException,
                       InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
            // ------------------------------------------------------------------------------- paths
            final var plainPath = JavaNioUtils.createTempFileInAndWriteSome(dir);
            final var encryptedPath = Files.createTempFile(dir, null, null);
            final var decryptedPath = Files.createTempFile(dir, null, null);
            // ------------------------------------------------------------------------------ cipher
            final var algorithm = "DESede";
            final var mode = "ECB";
            final var padding = "NoPadding";
            final var transformation = algorithm + '/' + mode + '/' + padding;
            final var cipher = Cipher.getInstance(transformation);
            final var blockSize = cipher.getBlockSize();
            assert blockSize == 64 >> 3;
            {
                final var required = (int) (blockSize - (Files.size(plainPath) % blockSize));
                try (var channel = FileChannel.open(plainPath, StandardOpenOption.WRITE,
                                                    StandardOpenOption.APPEND)) {
                    for (final var buffer = ByteBuffer.allocate(required);
                         buffer.hasRemaining(); ) {
                        final var w = channel.write(buffer);
                        assert w >= 0;
                    }
                    channel.force(false);
                }
                assertThat(Files.size(plainPath) % blockSize).isZero();
            }
            // --------------------------------------------------------------------------------- key
            final var key = generateKey(algorithm, keysize);
            // ----------------------------------------------------------------------------- encrypt
            {
                cipher.init(Cipher.ENCRYPT_MODE, key);
                try (var readable = FileChannel.open(plainPath, StandardOpenOption.READ);
                     var writable = FileChannel.open(encryptedPath, StandardOpenOption.WRITE)) {
                    final long bytes = CipherUtils.update(
                            cipher,
                            readable,
                            ByteBuffer.allocate(1024),
                            writable
                    );
                    assertThat(bytes).isEqualTo(Files.size(plainPath));
                    for (var buffer = ByteBuffer.wrap(cipher.doFinal());
                         buffer.hasRemaining(); ) {
                        final var w = writable.write(buffer);
                        assert w >= 0;
                    }
                    writable.force(false);
                }
            }
            // ----------------------------------------------------------------------------- decrypt
            {
                cipher.init(Cipher.DECRYPT_MODE, key);
                try (var readable = FileChannel.open(encryptedPath, StandardOpenOption.READ);
                     var writable = FileChannel.open(decryptedPath, StandardOpenOption.WRITE)) {
                    final var bytes = CipherUtils.update(
                            cipher,
                            readable,
                            ByteBuffer.allocate(1024),
                            writable
                    );
                    assertThat(bytes).isEqualTo(Files.size(encryptedPath));
                    for (final var buffer = ByteBuffer.wrap(cipher.doFinal());
                         buffer.hasRemaining(); ) {
                        final var w = writable.write(buffer);
                        assert w >= 0;
                    }
                    writable.force(false);
                }
                assertThat(decryptedPath).hasSize(Files.size(plainPath));
            }
            // ------------------------------------------------------------------------------ verify
            {
                final var plainFileDigest = MessageDigestUtils.getDigest(
                        algorithm,
                        plainPath,
                        ByteBuffer.allocate(1024)
                );
                final var decryptedFileDigest = MessageDigestUtils.getDigest(
                        algorithm,
                        decryptedPath,
                        ByteBuffer.allocate(1024)
                );
                assertThat(decryptedFileDigest).isEqualTo(plainFileDigest);
            }
        }
    }

    // https://www.thalesdocs.com/gphsm/ptk/5.7/docs/Content/PTK-J/Ciphers/DESede.htm
    @DisplayName("DESede/ECB/PKCS5Padding")
    @Nested
    class DesedeEcbPkcs5PaddingTest {

        private static IntStream keysizes() {
            return desKeysizes();
        }

        @MethodSource({"keysizes"})
        @ParameterizedTest(name = "[{index}] keysize: {0}")
        void __(final int keysize, @TempDir final File dir)
                throws IOException, NoSuchPaddingException, NoSuchAlgorithmException,
                       InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
            // ------------------------------------------------------------------------------- files
            final var plainFile = _JavaIoUtils.createTempFileInAndWriteSome(dir);
            final var encryptedFile = File.createTempFile("tmp", "tmp", dir);
            final var decryptedFile = File.createTempFile("tmp", "tmp", dir);
            // ------------------------------------------------------------------------------ cipher
            final var algorithm = "DESede";
            final var mode = "ECB";
            final var padding = "PKCS5Padding";
            final var transformation = algorithm + '/' + mode + '/' + padding;
            final var cipher = Cipher.getInstance(transformation);
            final var blockSize = cipher.getBlockSize();
            assert blockSize == 64 >> 3 : "block size is always 64 regardless of the key size";
            // --------------------------------------------------------------------------------- key
            final var key = generateKey(algorithm, keysize);
            // ----------------------------------------------------------------------------- encrypt
            {
                cipher.init(Cipher.ENCRYPT_MODE, key);
                try (var input = new FileInputStream(plainFile);
                     var output = new FileOutputStream(encryptedFile)) {
                    final long bytes = CipherUtils.update(
                            cipher,
                            input,
                            new byte[1024],
                            output
                    );
                    assertThat(bytes).isEqualTo(plainFile.length());
                    output.write(cipher.doFinal());
                    output.flush();
                }
            }
            // ----------------------------------------------------------------------------- decrypt
            {
                cipher.init(Cipher.DECRYPT_MODE, key);
                try (var input = new FileInputStream(encryptedFile);
                     var outputStream = new FileOutputStream(decryptedFile)) {
                    final var bytes = CipherUtils.update(
                            cipher,
                            input,
                            new byte[1024],
                            outputStream
                    );
                    assertThat(bytes).isEqualTo(encryptedFile.length());
                    outputStream.write(cipher.doFinal());
                    outputStream.flush();
                }
                assertThat(decryptedFile).hasSize(plainFile.length());
            }
            // ------------------------------------------------------------------------------ verify
            {
                final var plainFileDigest = MessageDigestUtils.getDigest(
                        algorithm,
                        plainFile,
                        new byte[1024]
                );
                final var decryptedFileDigest = MessageDigestUtils.getDigest(
                        algorithm,
                        decryptedFile,
                        new byte[1024]
                );
                assertThat(decryptedFileDigest).isEqualTo(plainFileDigest);
            }
        }

        @MethodSource({"keysizes"})
        @ParameterizedTest(name = "[{index}] keysize: {0}")
        void __(final int keysize, @TempDir final Path dir)
                throws IOException, NoSuchPaddingException, NoSuchAlgorithmException,
                       InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
            // ------------------------------------------------------------------------------- paths
            final var plainPath = Files.createTempFile(dir, null, null);
            final var encryptedPath = Files.createTempFile(dir, null, null);
            final var decryptedPath = Files.createTempFile(dir, null, null);
            // ------------------------------------------------------------------------------ cipher
            final var algorithm = "DESede";
            final var mode = "ECB";
            final var padding = "PKCS5Padding";
            final var transformation = algorithm + '/' + mode + '/' + padding;
            final var cipher = Cipher.getInstance(transformation);
            final var blockSize = cipher.getBlockSize();
            assert blockSize == 64 >> 3;
            // --------------------------------------------------------------------------------- key
            final var key = generateKey(algorithm, keysize);
            // ----------------------------------------------------------------------------- encrypt
            {
                PathUtils.writeRandomBytes(
                        plainPath,
                        ThreadLocalRandom.current().nextInt(8192),
                        ByteBuffer.allocate(1024),
                        0L
                );
                log.debug("plainPath.size: {}", Files.size(plainPath));
                cipher.init(Cipher.ENCRYPT_MODE, key);
                try (var readable = FileChannel.open(plainPath, StandardOpenOption.READ);
                     var writable = FileChannel.open(encryptedPath, StandardOpenOption.WRITE)) {
                    final long bytes = CipherUtils.update(
                            cipher,
                            readable,
                            ByteBuffer.allocate(1024),
                            writable
                    );
                    assertThat(bytes).isEqualTo(Files.size(plainPath));
                    for (var buffer = ByteBuffer.wrap(cipher.doFinal());
                         buffer.hasRemaining(); ) {
                        final var w = writable.write(buffer);
                        assert w >= 0;
                    }
                    writable.force(false);
                }
            }
            // ----------------------------------------------------------------------------- decrypt
            {
                cipher.init(Cipher.DECRYPT_MODE, key);
                try (var readable = FileChannel.open(encryptedPath, StandardOpenOption.READ);
                     var writable = FileChannel.open(decryptedPath, StandardOpenOption.WRITE)) {
                    final var bytes = CipherUtils.update(
                            cipher,
                            readable,
                            ByteBuffer.allocate(1024),
                            writable
                    );
                    assertThat(bytes).isEqualTo(Files.size(encryptedPath));
                    for (final var buffer = ByteBuffer.wrap(cipher.doFinal());
                         buffer.hasRemaining(); ) {
                        final var w = writable.write(buffer);
                        assert w >= 0;
                    }
                    writable.force(false);
                }
                log.debug("decryptedPath.size: {}", Files.size(decryptedPath));
                assertThat(decryptedPath).hasSize(Files.size(plainPath));
            }
            // ------------------------------------------------------------------------------ verify
            {
                final var digest = MessageDigest.getInstance("SHA-1");
                final var buffer = ByteBuffer.allocate(1024);
                final var plainPathDigest = MessageDigestUtils.getDigest(
                        algorithm,
                        plainPath,
                        ByteBuffer.allocate(1024)
                );
                log.debug("plainPathDigest: {}", HexFormat.of().formatHex(plainPathDigest));
                final var decryptedFileDigest = MessageDigestUtils.getDigest(
                        algorithm,
                        decryptedPath,
                        ByteBuffer.allocate(1024)
                );
                log.debug("decryptedPathDigest: {}", HexFormat.of().formatHex(decryptedFileDigest));
                assertThat(decryptedFileDigest).isEqualTo(plainPathDigest);
            }
        }
    }

    private static IntStream rsaKeysizes() {
        return IntStream.of(
                1024,
                2048
        );
    }

    @DisplayName("RSA/ECB/PKCS1Padding")
    @Nested
    class RsaEcbPkcs5PaddingTest {

        private static IntStream keysizes() {
            return rsaKeysizes();
        }

        @MethodSource({"keysizes"})
        @ParameterizedTest(name = "[{index}] keysize: {0}")
        void __(final int keySize)
                throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
                       IllegalBlockSizeException, BadPaddingException {
            // ------------------------------------------------------------------------------- files
            // https://mbed-tls.readthedocs.io/en/latest/kb/cryptography/rsa-encryption-maximum-data-size/#:~:text=RSA%20is%20only%20able%20to,5%20padding).
            final var plainBytes = new byte[(keySize >> 3) - 11];
            // ------------------------------------------------------------------------------ cipher
            final var algorithm = "RSA";
            final var mode = "ECB";
            final var padding = "PKCS1Padding";
            final var transformation = algorithm + '/' + mode + '/' + padding;
            final var cipher = Cipher.getInstance(transformation);
            try {
                assert cipher.getBlockSize() == 0;
            } catch (final IllegalStateException ise) {
                // BC
            }
            // ----------------------------------------------------------------------------- keyPair
            final var keyPair = generateKeyPair(algorithm, keySize);
            // --------------------------------------------------------- encrypt with the public key
            final byte[] encryptedBytes;
            {
                cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
                encryptedBytes = cipher.doFinal(plainBytes);
            }
            // -------------------------------------------------------- decrypt with the private key
            final byte[] decryptedBytes;
            {
                cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
                decryptedBytes = cipher.doFinal(encryptedBytes);
            }
            // ------------------------------------------------------------------------------ verify
            {
                assertThat(decryptedBytes)
                        .as("decryptedBytes")
                        .isEqualTo(plainBytes);
            }
        }
    }

    @DisplayName("RSA/ECB/OAEPWithSHA-1AndMGF1Padding")
    @Nested
    class RsaEcbOaepwWithSha_1AndMgf1Padding {

        private static IntStream keysizes() {
            return rsaKeysizes();
        }

        @MethodSource({"keysizes"})
        @ParameterizedTest(name = "[{index}] keysize: {0}")
        void __(final int keysize)
                throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
                       IllegalBlockSizeException, BadPaddingException {
            // ------------------------------------------------------------------------------- files
            // https://mbed-tls.readthedocs.io/en/latest/kb/cryptography/rsa-encryption-maximum-data-size/#:~:text=RSA%20is%20only%20able%20to,5%20padding).
            final var plainBytes = new byte[(keysize >> 3) - 42];
            // ------------------------------------------------------------------------------ cipher
            final var algorithm = "RSA";
            final var mode = "ECB";
            final var padding = "OAEPWithSHA-1AndMGF1Padding";
            final var transformation = algorithm + '/' + mode + '/' + padding;
            final var cipher = Cipher.getInstance(transformation);
            try {
                assert cipher.getBlockSize() == 0;
            } catch (final IllegalStateException ise) {
                // BC
            }
            // ----------------------------------------------------------------------------- keyPair
            final var keyPair = generateKeyPair(algorithm, keysize);
            // --------------------------------------------------------- encrypt with the public key
            final byte[] encryptedBytes;
            {
                cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
                encryptedBytes = cipher.doFinal(plainBytes);
            }
            // -------------------------------------------------------- decrypt with the private key
            final byte[] decryptedBytes;
            {
                cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
                decryptedBytes = cipher.doFinal(encryptedBytes);
            }
            // ------------------------------------------------------------------------------ verify
            {
                assertThat(decryptedBytes)
                        .as("decryptedBytes")
                        .isEqualTo(plainBytes);
            }
        }
    }

    @DisplayName("RSA/ECB/OAEPWithSHA-256AndMGF1Padding")
    @Nested
    class RsaEcbOaepwWithSha_256AndMgf1Padding {

        private static IntStream keysizes() {
            return rsaKeysizes();
        }

        @MethodSource({"keysizes"})
        @ParameterizedTest(name = "[{index}] keysize: {0}")
        void __(final int keysize)
                throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
                       IllegalBlockSizeException, BadPaddingException {
            // ------------------------------------------------------------------------------- files
            // https://mbed-tls.readthedocs.io/en/latest/kb/cryptography/rsa-encryption-maximum-data-size/#:~:text=RSA%20is%20only%20able%20to,5%20padding).
            final var plainBytes = new byte[(keysize >> 3) - 66];
            // ------------------------------------------------------------------------------ cipher
            final var algorithm = "RSA";
            final var mode = "ECB";
            final var padding = "OAEPWithSHA-256AndMGF1Padding";
            final var transformation = algorithm + '/' + mode + '/' + padding;
            final var cipher = Cipher.getInstance(transformation);
            try {
                assert cipher.getBlockSize() == 0;
            } catch (final IllegalStateException ise) {
                // BC
            }
            // ----------------------------------------------------------------------------- keyPair
            final var keyPair = generateKeyPair(algorithm, keysize);
            // --------------------------------------------------------- encrypt with the public key
            final byte[] encryptedBytes;
            {
                cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
                encryptedBytes = cipher.doFinal(plainBytes);
            }
            // -------------------------------------------------------- decrypt with the private key
            final byte[] decryptedBytes;
            {
                cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
                decryptedBytes = cipher.doFinal(encryptedBytes);
            }
            // ------------------------------------------------------------------------------ verify
            {
                assertThat(decryptedBytes)
                        .as("decryptedBytes")
                        .isEqualTo(plainBytes);
            }
        }
    }
}
