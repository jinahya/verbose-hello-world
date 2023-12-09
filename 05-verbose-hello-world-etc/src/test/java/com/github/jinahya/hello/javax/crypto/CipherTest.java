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

import com.github.jinahya.hello.java.security._SecurityTestUtils;
import com.github.jinahya.hello.util.JavaIoUtils;
import com.github.jinahya.hello.util.JavaNioUtils;
import com.github.jinahya.hello.util.java.security.MessageDigestUtils;
import com.github.jinahya.hello.util.javax.crypto.CipherUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
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
import java.security.Provider;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
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

    private static Stream<Provider> providers() {
        final var type = Cipher.class.getSimpleName();
        return _SecurityTestUtils.providers()
                .filter(p -> p.getServices().stream().anyMatch(s -> s.getType().equals(type)));
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

    private static Stream<String> requiredToBeSupportedTransformations() {
        return requiredToBeSupportedTransformationsAnsKeysizss()
                .map(a -> (String) a.get()[0]);
    }

    private static Stream<Arguments> providersAndRequiredToBeSupportedTransformations() {
        return providers()
                .flatMap(p -> requiredToBeSupportedTransformations().map(t -> arguments(p, t)));
    }

    @MethodSource({"providersAndRequiredToBeSupportedTransformations"})
    @ParameterizedTest
    void requiredToBeSupportedTransformations__(final Provider provider,
                                                final String transformation) {
        assertThatCode(() -> {
            Cipher.getInstance(transformation, provider);
        }).doesNotThrowAnyException();
    }

    @DisplayName("AES/CBC/NoPadding")
    @Nested
    class AesCbcNoPaddingTest {

        @ValueSource(ints = {
                128,
                192,
                256
        })
        @ParameterizedTest
        void __(final int keySize, @TempDir final File dir)
                throws IOException, NoSuchPaddingException, NoSuchAlgorithmException,
                       InvalidKeyException, InvalidAlgorithmParameterException,
                       IllegalBlockSizeException, BadPaddingException {
            // ------------------------------------------------------------------------------- files
            final var plainFile = JavaIoUtils.createTempFileInAndWriteSome(dir);
            final var encryptedFile = File.createTempFile("tmp", "tmp", dir);
            final var decryptedFile = File.createTempFile("tmp", "tmp", dir);
            // ------------------------------------------------------------------------------ cipher
            final String algorithm = "AES";
            final String mode = "CBC";
            final String padding = "NoPadding";
            final var cipher = Cipher.getInstance(algorithm + '/' + mode + '/' + padding);
            final var blockSize = cipher.getBlockSize();
            assert blockSize == 128 >> 3 : "block size is always 128 regardless of the key size";
            {
                final var required = (int) (blockSize - (plainFile.length() % blockSize));
                try (var stream = new FileOutputStream(plainFile, true)) {
                    stream.write(new byte[required]);
                    stream.flush();
                }
                assertThat(plainFile.length() % blockSize).isZero();
            }
            // --------------------------------------------------------------------------------- key
            final Key key;
            {
                final var key_ = new byte[keySize >> 3];
                SecureRandom.getInstanceStrong().nextBytes(key_);
                key = new SecretKeySpec(key_, algorithm);
            }
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
                final var digest = MessageDigest.getInstance("SHA-1");
                final var buffer = new byte[1024];
                final byte[] plainFileDigest;
                {
                    digest.reset();
                    plainFileDigest = MessageDigestUtils.digest(
                            digest,
                            plainFile,
                            buffer
                    );
                }
                final byte[] decryptedFileDigest;
                {
                    digest.reset();
                    decryptedFileDigest = MessageDigestUtils.digest(
                            digest,
                            decryptedFile,
                            buffer
                    );
                }
                assertThat(decryptedFileDigest).isEqualTo(plainFileDigest);
            }
        }

        @ValueSource(ints = {
                128,
                192,
                256
        })
        @ParameterizedTest
        void __(final int keySize, @TempDir final Path dir)
                throws IOException, NoSuchPaddingException, NoSuchAlgorithmException,
                       InvalidKeyException, InvalidAlgorithmParameterException,
                       IllegalBlockSizeException, BadPaddingException {
            // ------------------------------------------------------------------------------- paths
            final var plainPath = JavaNioUtils.createTempFileInAndWriteSome(dir);
            final var encryptedPath = Files.createTempFile(dir, null, null);
            final var decryptedPath = Files.createTempFile(dir, null, null);
            // ------------------------------------------------------------------------------ cipher
            final String algorithm = "AES";
            final String mode = "CBC";
            final String padding = "NoPadding";
            final var cipher = Cipher.getInstance(algorithm + '/' + mode + '/' + padding);
            final var blockSize = cipher.getBlockSize();
            assert blockSize
                   == 128 >> 3 : "block size is always 128 regardless of the key size";
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
            final Key key;
            {
                final var key_ = new byte[keySize >> 3];
                SecureRandom.getInstanceStrong().nextBytes(key_);
                key = new SecretKeySpec(key_, algorithm);
            }
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
                final var digest = MessageDigest.getInstance("SHA-1");
                final var buffer = ByteBuffer.allocate(1024);
                final byte[] plainFileDigest;
                {
                    digest.reset();
                    plainFileDigest = MessageDigestUtils.digest(
                            digest,
                            plainPath,
                            buffer
                    );
                }
                final byte[] decryptedFileDigest;
                {
                    digest.reset();
                    decryptedFileDigest = MessageDigestUtils.digest(
                            digest,
                            decryptedPath,
                            buffer
                    );
                }
                assertThat(decryptedFileDigest).isEqualTo(plainFileDigest);
            }
        }
    }

    @DisplayName("AES/CBC/PKCS5Padding")
    @Nested
    class AesCbcPkcs5PaddingTest {

        @ValueSource(ints = {
                128,
                192,
                256
        })
        @ParameterizedTest
        void __(final int keySize, @TempDir final File dir)
                throws IOException, NoSuchPaddingException, NoSuchAlgorithmException,
                       InvalidKeyException, InvalidAlgorithmParameterException,
                       IllegalBlockSizeException, BadPaddingException {
            // ------------------------------------------------------------------------------- files
            final var plainFile = JavaIoUtils.createTempFileInAndWriteSome(dir);
            final var encryptedFile = File.createTempFile("tmp", "tmp", dir);
            final var decryptedFile = File.createTempFile("tmp", "tmp", dir);
            // ------------------------------------------------------------------------------ cipher
            final String algorithm = "AES";
            final String mode = "CBC";
            final String padding = "PKCS5Padding";
            final var cipher = Cipher.getInstance(algorithm + '/' + mode + '/' + padding);
            final var blockSize = cipher.getBlockSize();
            assert blockSize == 128 >> 3 : "block size is always 128 regardless of the key size";
            // --------------------------------------------------------------------------------- key
            final Key key;
            {
                final var key_ = new byte[keySize >> 3];
                SecureRandom.getInstanceStrong().nextBytes(key_);
                key = new SecretKeySpec(key_, algorithm);
            }
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
                final var digest = MessageDigest.getInstance("SHA-1");
                final var buffer = new byte[1024];
                final byte[] plainFileDigest;
                {
                    digest.reset();
                    plainFileDigest = MessageDigestUtils.digest(
                            digest,
                            plainFile,
                            buffer
                    );
                }
                final byte[] decryptedFileDigest;
                {
                    digest.reset();
                    decryptedFileDigest = MessageDigestUtils.digest(
                            digest,
                            decryptedFile,
                            buffer
                    );
                }
                assertThat(decryptedFileDigest).isEqualTo(plainFileDigest);
            }
        }

        @ValueSource(ints = {
                128,
                192,
                256
        })
        @ParameterizedTest
        void __(final int keySize, @TempDir final Path dir)
                throws IOException, NoSuchPaddingException, NoSuchAlgorithmException,
                       InvalidKeyException, InvalidAlgorithmParameterException,
                       IllegalBlockSizeException, BadPaddingException {
            // ------------------------------------------------------------------------------- paths
            final var plainPath = JavaNioUtils.createTempFileInAndWriteSome(dir);
            final var encryptedPath = Files.createTempFile(dir, null, null);
            final var decryptedPath = Files.createTempFile(dir, null, null);
            // ------------------------------------------------------------------------------ cipher
            final String algorithm = "AES";
            final String mode = "CBC";
            final String padding = "PKCS5Padding";
            final var cipher = Cipher.getInstance(algorithm + '/' + mode + '/' + padding);
            final var blockSize = cipher.getBlockSize();
            assert blockSize == 128 >> 3 : "block size is always 128 regardless of the key size";
            // --------------------------------------------------------------------------------- key
            final Key key;
            {
                final var key_ = new byte[keySize >> 3];
                SecureRandom.getInstanceStrong().nextBytes(key_);
                key = new SecretKeySpec(key_, algorithm);
            }
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
                    for (final var b = ByteBuffer.wrap(cipher.doFinal()); b.hasRemaining(); ) {
                        final var w = writable.write(b);
                        assert w >= 0;
                    }
                    writable.force(false);
                }
                assertThat(decryptedPath).hasSize(Files.size(plainPath));
            }
            // ------------------------------------------------------------------------------ verify
            {
                final var digest = MessageDigest.getInstance("SHA-1");
                final var buffer = ByteBuffer.allocate(1024);
                final byte[] plainFileDigest;
                {
                    digest.reset();
                    plainFileDigest = MessageDigestUtils.digest(
                            digest,
                            plainPath,
                            buffer
                    );
                }
                final byte[] decryptedFileDigest;
                {
                    digest.reset();
                    decryptedFileDigest = MessageDigestUtils.digest(
                            digest,
                            decryptedPath,
                            buffer
                    );
                }
                assertThat(decryptedFileDigest).isEqualTo(plainFileDigest);
            }
        }
    }

    @DisplayName("AES/ECB/NoPadding")
    @Nested
    class AesEcbNoPaddingTest {

        @ValueSource(ints = {
                128,
                192,
                256
        })
        @ParameterizedTest
        void __(final int keySize, @TempDir final File dir)
                throws IOException, NoSuchPaddingException, NoSuchAlgorithmException,
                       InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
            // ------------------------------------------------------------------------------- files
            final var plainFile = JavaIoUtils.createTempFileInAndWriteSome(dir);
            final var encryptedFile = File.createTempFile("tmp", "tmp", dir);
            final var decryptedFile = File.createTempFile("tmp", "tmp", dir);
            // ------------------------------------------------------------------------------ cipher
            final String algorithm = "AES";
            final String mode = "ECB";
            final String padding = "NoPadding";
            final var cipher = Cipher.getInstance(algorithm + '/' + mode + '/' + padding);
            final var blockSize = cipher.getBlockSize();
            assert blockSize
                   == 128 >> 3 : "block size is always 128 regardless of the key size";
            {
                final var required = (int) (blockSize - (plainFile.length() % blockSize));
                try (var stream = new FileOutputStream(plainFile, true)) {
                    stream.write(new byte[required]);
                    stream.flush();
                }
                assertThat(plainFile.length() % blockSize).isZero();
            }
            // --------------------------------------------------------------------------------- key
            final Key key;
            {
                final var key_ = new byte[keySize >> 3];
                SecureRandom.getInstanceStrong().nextBytes(key_);
                key = new SecretKeySpec(key_, algorithm);
            }
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
                final var digest = MessageDigest.getInstance("SHA-1");
                final var buffer = new byte[1024];
                final byte[] plainFileDigest;
                {
                    digest.reset();
                    plainFileDigest = MessageDigestUtils.digest(
                            digest,
                            plainFile,
                            buffer
                    );
                }
                final byte[] decryptedFileDigest;
                {
                    digest.reset();
                    decryptedFileDigest = MessageDigestUtils.digest(
                            digest,
                            decryptedFile,
                            buffer
                    );
                }
                assertThat(decryptedFileDigest).isEqualTo(plainFileDigest);
            }
        }

        @ValueSource(ints = {
                128,
                192,
                256
        })
        @ParameterizedTest
        void __(final int keySize, @TempDir final Path dir)
                throws IOException, NoSuchPaddingException, NoSuchAlgorithmException,
                       InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
            // ------------------------------------------------------------------------------- paths
            final var plainPath = JavaNioUtils.createTempFileInAndWriteSome(dir);
            final var encryptedPath = Files.createTempFile(dir, null, null);
            final var decryptedPath = Files.createTempFile(dir, null, null);
            // ------------------------------------------------------------------------------ cipher
            final String algorithm = "AES";
            final String mode = "ECB";
            final String padding = "NoPadding";
            final var cipher = Cipher.getInstance(algorithm + '/' + mode + '/' + padding);
            final var blockSize = cipher.getBlockSize();
            assert blockSize == 128 >> 3 : "block size is always 128 regardless of the key size";
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
            final Key key;
            {
                final var key_ = new byte[keySize >> 3];
                SecureRandom.getInstanceStrong().nextBytes(key_);
                key = new SecretKeySpec(key_, algorithm);
            }
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
                final var digest = MessageDigest.getInstance("SHA-1");
                final var buffer = ByteBuffer.allocate(1024);
                final byte[] plainFileDigest;
                {
                    digest.reset();
                    plainFileDigest = MessageDigestUtils.digest(
                            digest,
                            plainPath,
                            buffer
                    );
                }
                final byte[] decryptedFileDigest;
                {
                    digest.reset();
                    decryptedFileDigest = MessageDigestUtils.digest(
                            digest,
                            decryptedPath,
                            buffer
                    );
                }
                assertThat(decryptedFileDigest).isEqualTo(plainFileDigest);
            }
        }
    }

    @DisplayName("AES/ECB/PKCS5Padding")
    @Nested
    class AesEcbPkcs5PaddingTest {

        @ValueSource(ints = {
                128,
                192,
                256
        })
        @ParameterizedTest
        void __(final int keySize, @TempDir final File dir)
                throws IOException, NoSuchPaddingException, NoSuchAlgorithmException,
                       InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
            // ------------------------------------------------------------------------------- files
            final var plainFile = JavaIoUtils.createTempFileInAndWriteSome(dir);
            final var encryptedFile = File.createTempFile("tmp", "tmp", dir);
            final var decryptedFile = File.createTempFile("tmp", "tmp", dir);
            // ------------------------------------------------------------------------------ cipher
            final String algorithm = "AES";
            final String mode = "ECB";
            final String padding = "PKCS5Padding";
            final var cipher = Cipher.getInstance(algorithm + '/' + mode + '/' + padding);
            final var blockSize = cipher.getBlockSize();
            assert blockSize
                   == 128 >> 3 : "block size is always 128 regardless of the key size";
            // --------------------------------------------------------------------------------- key
            final Key key;
            {
                final var key_ = new byte[keySize >> 3];
                SecureRandom.getInstanceStrong().nextBytes(key_);
                key = new SecretKeySpec(key_, algorithm);
            }
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
                final var digest = MessageDigest.getInstance("SHA-1");
                final var buffer = new byte[1024];
                final byte[] plainFileDigest;
                {
                    digest.reset();
                    plainFileDigest = MessageDigestUtils.digest(
                            digest,
                            plainFile,
                            buffer
                    );
                }
                final byte[] decryptedFileDigest;
                {
                    digest.reset();
                    decryptedFileDigest = MessageDigestUtils.digest(
                            digest,
                            decryptedFile,
                            buffer
                    );
                }
                assertThat(decryptedFileDigest).isEqualTo(plainFileDigest);
            }
        }

        @ValueSource(ints = {
                128,
                192,
                256
        })
        @ParameterizedTest
        void __(final int keySize, @TempDir final Path dir)
                throws IOException, NoSuchPaddingException, NoSuchAlgorithmException,
                       InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
            // ------------------------------------------------------------------------------- paths
            final var plainPath = JavaNioUtils.createTempFileInAndWriteSome(dir);
            final var encryptedPath = Files.createTempFile(dir, null, null);
            final var decryptedPath = Files.createTempFile(dir, null, null);
            // ------------------------------------------------------------------------------ cipher
            final String algorithm = "AES";
            final String mode = "ECB";
            final String padding = "PKCS5Padding";
            final var cipher = Cipher.getInstance(algorithm + '/' + mode + '/' + padding);
            final var blockSize = cipher.getBlockSize();
            assert blockSize == 128 >> 3 : "block size is always 128 regardless of the key size";
            // --------------------------------------------------------------------------------- key
            final Key key;
            {
                final var key_ = new byte[keySize >> 3];
                SecureRandom.getInstanceStrong().nextBytes(key_);
                key = new SecretKeySpec(key_, algorithm);
            }
            // ----------------------------------------------------------------------------- encrypt
            {
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
                    for (final var b = ByteBuffer.wrap(cipher.doFinal()); b.hasRemaining(); ) {
                        final var w = writable.write(b);
                        assert w >= 0;
                    }
                    writable.force(false);
                }
                assertThat(decryptedPath).hasSize(Files.size(plainPath));
            }
            // ------------------------------------------------------------------------------ verify
            {
                final var digest = MessageDigest.getInstance("SHA-1");
                final var buffer = ByteBuffer.allocate(1024);
                final byte[] plainFileDigest;
                {
                    digest.reset();
                    plainFileDigest = MessageDigestUtils.digest(
                            digest,
                            plainPath,
                            buffer
                    );
                }
                final byte[] decryptedFileDigest;
                {
                    digest.reset();
                    decryptedFileDigest = MessageDigestUtils.digest(
                            digest,
                            decryptedPath,
                            buffer
                    );
                }
                assertThat(decryptedFileDigest).isEqualTo(plainFileDigest);
            }
        }
    }

    @DisplayName("AES/GCM/NoPadding")
    @Nested
    class AesGcmNoPaddingTest {

        @ValueSource(ints = {
                128,
                192,
                256
        })
        @ParameterizedTest
        void __(final int keySize, @TempDir final File dir)
                throws IOException, NoSuchPaddingException, NoSuchAlgorithmException,
                       InvalidKeyException, InvalidAlgorithmParameterException,
                       IllegalBlockSizeException, BadPaddingException {
            // ------------------------------------------------------------------------------- files
            final var plainFile = JavaIoUtils.createTempFileInAndWriteSome(dir);
            final var encryptedFile = File.createTempFile("tmp", "tmp", dir);
            final var decryptedFile = File.createTempFile("tmp", "tmp", dir);
            // ------------------------------------------------------------------------------ cipher
            final String algorithm = "AES";
            final String mode = "GCM";
            final String padding = "NoPadding";
            final var cipher = Cipher.getInstance(algorithm + '/' + mode + '/' + padding);
            final var blockSize = cipher.getBlockSize();
            assert blockSize == 128 >> 3;
            {
                final var required = (int) (blockSize - (plainFile.length() % blockSize));
                try (var stream = new FileOutputStream(plainFile, true)) {
                    stream.write(new byte[required]);
                    stream.flush();
                }
                assertThat(plainFile.length() % blockSize).isZero();
            }
            // --------------------------------------------------------------------------------- key
            final Key key;
            {
                final var key_ = new byte[keySize >> 3];
                SecureRandom.getInstanceStrong().nextBytes(key_);
                key = new SecretKeySpec(key_, algorithm);
            }
            // ------------------------------------------------------------------------------ params
            final AlgorithmParameterSpec params;
            {
                final var tLens = new int[] {128, 120, 112, 104, 96};
                final var tLen = tLens[ThreadLocalRandom.current().nextInt(tLens.length)];
                final var src = new byte[blockSize];
                ThreadLocalRandom.current().nextBytes(src);
                params = new GCMParameterSpec(tLen, src);
            }
            // --------------------------------------------------------------------------------- aad
            final byte[] aad = new byte[blockSize];
            SecureRandom.getInstanceStrong().nextBytes(aad);
            // ----------------------------------------------------------------------------- encrypt
            {
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
            }
            // ----------------------------------------------------------------------------- decrypt
            {
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
                assertThat(decryptedFile).hasSize(plainFile.length());
            }
            // ------------------------------------------------------------------------------ verify
            {
                final var digest = MessageDigest.getInstance("SHA-1");
                final var buffer = new byte[1024];
                final byte[] plainFileDigest;
                {
                    digest.reset();
                    plainFileDigest = MessageDigestUtils.digest(
                            digest,
                            plainFile,
                            buffer
                    );
                }
                final byte[] decryptedFileDigest;
                {
                    digest.reset();
                    decryptedFileDigest = MessageDigestUtils.digest(
                            digest,
                            decryptedFile,
                            buffer
                    );
                }
                assertThat(decryptedFileDigest).isEqualTo(plainFileDigest);
            }
        }

        @ValueSource(ints = {
                128,
                192,
                256
        })
        @ParameterizedTest
        void __(final int keySize, @TempDir final Path dir)
                throws IOException, NoSuchPaddingException, NoSuchAlgorithmException,
                       InvalidKeyException, InvalidAlgorithmParameterException,
                       IllegalBlockSizeException, BadPaddingException {
            // ------------------------------------------------------------------------------- paths
            final var plainPath = JavaNioUtils.createTempFileInAndWriteSome(dir);
            final var encryptedPath = Files.createTempFile(dir, null, null);
            final var decryptedPath = Files.createTempFile(dir, null, null);
            // ------------------------------------------------------------------------------ cipher
            final String algorithm = "AES";
            final String mode = "GCM";
            final String padding = "NoPadding";
            final var cipher = Cipher.getInstance(algorithm + '/' + mode + '/' + padding);
            final var blockSize = cipher.getBlockSize();
            assert blockSize
                   == 128 >> 3 : "block size is always 128 regardless of the key size";
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
            final Key key;
            {
                final var key_ = new byte[keySize >> 3];
                SecureRandom.getInstanceStrong().nextBytes(key_);
                key = new SecretKeySpec(key_, algorithm);
            }
            // ------------------------------------------------------------------------------ params
            final AlgorithmParameterSpec params;
            {
                final var tLens = new int[] {128, 120, 112, 104, 96};
                final var tLen = tLens[ThreadLocalRandom.current().nextInt(tLens.length)];
                final var src = new byte[blockSize];
                ThreadLocalRandom.current().nextBytes(src);
                params = new GCMParameterSpec(tLen, src);
            }
            // --------------------------------------------------------------------------------- aad
            final byte[] aad = new byte[blockSize];
            SecureRandom.getInstanceStrong().nextBytes(aad);
            // ----------------------------------------------------------------------------- encrypt
            {
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
            }
            // ----------------------------------------------------------------------------- decrypt
            {
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
                assertThat(decryptedPath).hasSize(Files.size(plainPath));
            }
            // ------------------------------------------------------------------------------ verify
            {
                final var digest = MessageDigest.getInstance("SHA-1");
                final var buffer = ByteBuffer.allocate(1024);
                final byte[] plainFileDigest;
                {
                    digest.reset();
                    plainFileDigest = MessageDigestUtils.digest(
                            digest,
                            plainPath,
                            buffer
                    );
                }
                final byte[] decryptedFileDigest;
                {
                    digest.reset();
                    decryptedFileDigest = MessageDigestUtils.digest(
                            digest,
                            decryptedPath,
                            buffer
                    );
                }
                assertThat(decryptedFileDigest).isEqualTo(plainFileDigest);
            }
        }
    }

    // https://www.thalesdocs.com/gphsm/ptk/5.7/docs/Content/PTK-J/Ciphers/DESede.htm
    @DisplayName("DESede/CBC/NoPadding")
    @Nested
    class DesedeCbcNoPaddingTest {

        @ValueSource(ints = {
//                128,
                168,
//                192,
//                256
        })
        @ParameterizedTest
        void __(final int keySize, @TempDir final File dir)
                throws IOException, NoSuchPaddingException, NoSuchAlgorithmException,
                       InvalidKeyException, InvalidAlgorithmParameterException,
                       IllegalBlockSizeException, BadPaddingException {
            // ------------------------------------------------------------------------------- files
            final var plainFile = JavaIoUtils.createTempFileInAndWriteSome(dir);
            final var encryptedFile = File.createTempFile("tmp", "tmp", dir);
            final var decryptedFile = File.createTempFile("tmp", "tmp", dir);
            // ------------------------------------------------------------------------------ cipher
            final String algorithm = "DESede";
            final String mode = "CBC";
            final String padding = "NoPadding";
            final var cipher = Cipher.getInstance(algorithm + '/' + mode + '/' + padding);
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
            final Key key;
            {
                final var generator = KeyGenerator.getInstance(algorithm);
                generator.init(keySize);
                key = generator.generateKey();
            }
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
                final var digest = MessageDigest.getInstance("SHA-1");
                final var buffer = new byte[1024];
                final byte[] plainFileDigest;
                {
                    digest.reset();
                    plainFileDigest = MessageDigestUtils.digest(
                            digest,
                            plainFile,
                            buffer
                    );
                }
                final byte[] decryptedFileDigest;
                {
                    digest.reset();
                    decryptedFileDigest = MessageDigestUtils.digest(
                            digest,
                            decryptedFile,
                            buffer
                    );
                }
                assertThat(decryptedFileDigest).isEqualTo(plainFileDigest);
            }
        }

        @ValueSource(ints = {
//                128,
                168,
//                192,
//                256
        })
        @ParameterizedTest
        void __(final int keySize, @TempDir final Path dir)
                throws IOException, NoSuchPaddingException, NoSuchAlgorithmException,
                       InvalidKeyException, InvalidAlgorithmParameterException,
                       IllegalBlockSizeException, BadPaddingException {
            // ------------------------------------------------------------------------------- paths
            final var plainPath = JavaNioUtils.createTempFileInAndWriteSome(dir);
            final var encryptedPath = Files.createTempFile(dir, null, null);
            final var decryptedPath = Files.createTempFile(dir, null, null);
            // ------------------------------------------------------------------------------ cipher
            final String algorithm = "DESede";
            final String mode = "CBC";
            final String padding = "NoPadding";
            final var cipher = Cipher.getInstance(algorithm + '/' + mode + '/' + padding);
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
            final Key key;
            {
                final var generator = KeyGenerator.getInstance(algorithm);
                generator.init(keySize);
                key = generator.generateKey();
            }
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
                final var digest = MessageDigest.getInstance("SHA-1");
                final var buffer = ByteBuffer.allocate(1024);
                final byte[] plainFileDigest;
                {
                    digest.reset();
                    plainFileDigest = MessageDigestUtils.digest(
                            digest,
                            plainPath,
                            buffer
                    );
                }
                final byte[] decryptedFileDigest;
                {
                    digest.reset();
                    decryptedFileDigest = MessageDigestUtils.digest(
                            digest,
                            decryptedPath,
                            buffer
                    );
                }
                assertThat(decryptedFileDigest).isEqualTo(plainFileDigest);
            }
        }
    }

    // https://www.thalesdocs.com/gphsm/ptk/5.7/docs/Content/PTK-J/Ciphers/DESede.htm
    @DisplayName("DESede/CBC/PKCS5Padding")
    @Nested
    class DesedeCbcPkcs5PaddingTest {

        @ValueSource(ints = {
                168
        })
        @ParameterizedTest
        void __(final int keySize, @TempDir final File dir)
                throws IOException, NoSuchPaddingException, NoSuchAlgorithmException,
                       InvalidKeyException, InvalidAlgorithmParameterException,
                       IllegalBlockSizeException, BadPaddingException {
            // ------------------------------------------------------------------------------- files
            final var plainFile = JavaIoUtils.createTempFileInAndWriteSome(dir);
            final var encryptedFile = File.createTempFile("tmp", "tmp", dir);
            final var decryptedFile = File.createTempFile("tmp", "tmp", dir);
            // ------------------------------------------------------------------------------ cipher
            final String algorithm = "DESede";
            final String mode = "CBC";
            final String padding = "PKCS5Padding";
            final var cipher = Cipher.getInstance(algorithm + '/' + mode + '/' + padding);
            final var blockSize = cipher.getBlockSize();
            assert blockSize == 64 >> 3 : "block size is always 64 regardless of the key size";
            // --------------------------------------------------------------------------------- key
            final Key key;
            {
                final var generator = KeyGenerator.getInstance(algorithm);
                generator.init(keySize);
                key = generator.generateKey();
            }
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
                final var digest = MessageDigest.getInstance("SHA-1");
                final var buffer = new byte[1024];
                final byte[] plainFileDigest;
                {
                    digest.reset();
                    plainFileDigest = MessageDigestUtils.digest(
                            digest,
                            plainFile,
                            buffer
                    );
                }
                final byte[] decryptedFileDigest;
                {
                    digest.reset();
                    decryptedFileDigest = MessageDigestUtils.digest(
                            digest,
                            decryptedFile,
                            buffer
                    );
                }
                assertThat(decryptedFileDigest).isEqualTo(plainFileDigest);
            }
        }

        @ValueSource(ints = {
                168
        })
        @ParameterizedTest
        void __(final int keySize, @TempDir final Path dir)
                throws IOException, NoSuchPaddingException, NoSuchAlgorithmException,
                       InvalidKeyException, InvalidAlgorithmParameterException,
                       IllegalBlockSizeException, BadPaddingException {
            // ------------------------------------------------------------------------------- paths
            final var plainPath = JavaNioUtils.createTempFileInAndWriteSome(dir);
            final var encryptedPath = Files.createTempFile(dir, null, null);
            final var decryptedPath = Files.createTempFile(dir, null, null);
            // ------------------------------------------------------------------------------ cipher
            final String algorithm = "DESede";
            final String mode = "CBC";
            final String padding = "PKCS5Padding";
            final var cipher = Cipher.getInstance(algorithm + '/' + mode + '/' + padding);
            final var blockSize = cipher.getBlockSize();
            assert blockSize == 64 >> 3;
            // --------------------------------------------------------------------------------- key
            final Key key;
            {
                final var generator = KeyGenerator.getInstance(algorithm);
                generator.init(keySize);
                key = generator.generateKey();
            }
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
                final var digest = MessageDigest.getInstance("SHA-1");
                final var buffer = ByteBuffer.allocate(1024);
                final byte[] plainFileDigest;
                {
                    digest.reset();
                    plainFileDigest = MessageDigestUtils.digest(
                            digest,
                            plainPath,
                            buffer
                    );
                }
                final byte[] decryptedFileDigest;
                {
                    digest.reset();
                    decryptedFileDigest = MessageDigestUtils.digest(
                            digest,
                            decryptedPath,
                            buffer
                    );
                }
                assertThat(decryptedFileDigest).isEqualTo(plainFileDigest);
            }
        }
    }

    // https://www.thalesdocs.com/gphsm/ptk/5.7/docs/Content/PTK-J/Ciphers/DESede.htm
    @DisplayName("DESede/ECB/NoPadding")
    @Nested
    class DesedeEcbNoPaddingTest {

        @ValueSource(ints = {
//                128,
                168,
//                192,
//                256
        })
        @ParameterizedTest
        void __(final int keySize, @TempDir final File dir)
                throws IOException, NoSuchPaddingException, NoSuchAlgorithmException,
                       InvalidKeyException, InvalidAlgorithmParameterException,
                       IllegalBlockSizeException, BadPaddingException {
            // ------------------------------------------------------------------------------- files
            final var plainFile = JavaIoUtils.createTempFileInAndWriteSome(dir);
            final var encryptedFile = File.createTempFile("tmp", "tmp", dir);
            final var decryptedFile = File.createTempFile("tmp", "tmp", dir);
            // ------------------------------------------------------------------------------ cipher
            final String algorithm = "DESede";
            final String mode = "ECB";
            final String padding = "NoPadding";
            final var cipher = Cipher.getInstance(algorithm + '/' + mode + '/' + padding);
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
            final Key key;
            {
                final var generator = KeyGenerator.getInstance(algorithm);
                generator.init(keySize);
                key = generator.generateKey();
            }
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
                final var digest = MessageDigest.getInstance("SHA-1");
                final var buffer = new byte[1024];
                final byte[] plainFileDigest;
                {
                    digest.reset();
                    plainFileDigest = MessageDigestUtils.digest(
                            digest,
                            plainFile,
                            buffer
                    );
                }
                final byte[] decryptedFileDigest;
                {
                    digest.reset();
                    decryptedFileDigest = MessageDigestUtils.digest(
                            digest,
                            decryptedFile,
                            buffer
                    );
                }
                assertThat(decryptedFileDigest).isEqualTo(plainFileDigest);
            }
        }

        @ValueSource(ints = {
//                128,
                168,
//                192,
//                256
        })
        @ParameterizedTest
        void __(final int keySize, @TempDir final Path dir)
                throws IOException, NoSuchPaddingException, NoSuchAlgorithmException,
                       InvalidKeyException, InvalidAlgorithmParameterException,
                       IllegalBlockSizeException, BadPaddingException {
            // ------------------------------------------------------------------------------- paths
            final var plainPath = JavaNioUtils.createTempFileInAndWriteSome(dir);
            final var encryptedPath = Files.createTempFile(dir, null, null);
            final var decryptedPath = Files.createTempFile(dir, null, null);
            // ------------------------------------------------------------------------------ cipher
            final String algorithm = "DESede";
            final String mode = "ECB";
            final String padding = "NoPadding";
            final var cipher = Cipher.getInstance(algorithm + '/' + mode + '/' + padding);
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
            final Key key;
            {
                final var generator = KeyGenerator.getInstance(algorithm);
                generator.init(keySize);
                key = generator.generateKey();
            }
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
                final var digest = MessageDigest.getInstance("SHA-1");
                final var buffer = ByteBuffer.allocate(1024);
                final byte[] plainFileDigest;
                {
                    digest.reset();
                    plainFileDigest = MessageDigestUtils.digest(
                            digest,
                            plainPath,
                            buffer
                    );
                }
                final byte[] decryptedFileDigest;
                {
                    digest.reset();
                    decryptedFileDigest = MessageDigestUtils.digest(
                            digest,
                            decryptedPath,
                            buffer
                    );
                }
                assertThat(decryptedFileDigest).isEqualTo(plainFileDigest);
            }
        }
    }

    // https://www.thalesdocs.com/gphsm/ptk/5.7/docs/Content/PTK-J/Ciphers/DESede.htm
    @DisplayName("DESede/ECB/PKCS5Padding")
    @Nested
    class DesedeEcbPkcs5PaddingTest {

        @ValueSource(ints = {
                168
        })
        @ParameterizedTest
        void __(final int keySize, @TempDir final File dir)
                throws IOException, NoSuchPaddingException, NoSuchAlgorithmException,
                       InvalidKeyException, InvalidAlgorithmParameterException,
                       IllegalBlockSizeException, BadPaddingException {
            // ------------------------------------------------------------------------------- files
            final var plainFile = JavaIoUtils.createTempFileInAndWriteSome(dir);
            final var encryptedFile = File.createTempFile("tmp", "tmp", dir);
            final var decryptedFile = File.createTempFile("tmp", "tmp", dir);
            // ------------------------------------------------------------------------------ cipher
            final String algorithm = "DESede";
            final String mode = "ECB";
            final String padding = "PKCS5Padding";
            final var cipher = Cipher.getInstance(algorithm + '/' + mode + '/' + padding);
            final var blockSize = cipher.getBlockSize();
            assert blockSize == 64 >> 3 : "block size is always 64 regardless of the key size";
            // --------------------------------------------------------------------------------- key
            final Key key;
            {
                final var generator = KeyGenerator.getInstance(algorithm);
                generator.init(keySize);
                key = generator.generateKey();
            }
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
                final var digest = MessageDigest.getInstance("SHA-1");
                final var buffer = new byte[1024];
                final byte[] plainFileDigest;
                {
                    digest.reset();
                    plainFileDigest = MessageDigestUtils.digest(
                            digest,
                            plainFile,
                            buffer
                    );
                }
                final byte[] decryptedFileDigest;
                {
                    digest.reset();
                    decryptedFileDigest = MessageDigestUtils.digest(
                            digest,
                            decryptedFile,
                            buffer
                    );
                }
                assertThat(decryptedFileDigest).isEqualTo(plainFileDigest);
            }
        }

        @ValueSource(ints = {
                168
        })
        @ParameterizedTest
        void __(final int keySize, @TempDir final Path dir)
                throws IOException, NoSuchPaddingException, NoSuchAlgorithmException,
                       InvalidKeyException, InvalidAlgorithmParameterException,
                       IllegalBlockSizeException, BadPaddingException {
            // ------------------------------------------------------------------------------- paths
            final var plainPath = JavaNioUtils.createTempFileInAndWriteSome(dir);
            final var encryptedPath = Files.createTempFile(dir, null, null);
            final var decryptedPath = Files.createTempFile(dir, null, null);
            // ------------------------------------------------------------------------------ cipher
            final String algorithm = "DESede";
            final String mode = "ECB";
            final String padding = "PKCS5Padding";
            final var cipher = Cipher.getInstance(algorithm + '/' + mode + '/' + padding);
            final var blockSize = cipher.getBlockSize();
            assert blockSize == 64 >> 3;
            // --------------------------------------------------------------------------------- key
            final Key key;
            {
                final var generator = KeyGenerator.getInstance(algorithm);
                generator.init(keySize);
                key = generator.generateKey();
            }
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
                final var digest = MessageDigest.getInstance("SHA-1");
                final var buffer = ByteBuffer.allocate(1024);
                final byte[] plainFileDigest;
                {
                    digest.reset();
                    plainFileDigest = MessageDigestUtils.digest(
                            digest,
                            plainPath,
                            buffer
                    );
                }
                final byte[] decryptedFileDigest;
                {
                    digest.reset();
                    decryptedFileDigest = MessageDigestUtils.digest(
                            digest,
                            decryptedPath,
                            buffer
                    );
                }
                assertThat(decryptedFileDigest).isEqualTo(plainFileDigest);
            }
        }
    }

    @DisplayName("RSA/ECB/PKCS1Padding")
    @Nested
    class RsaEcbPkcs5PaddingTest {

        @ValueSource(ints = {
                1024,
                2048
        })
        @ParameterizedTest
        void __(final int keySize)
                throws NoSuchPaddingException, NoSuchAlgorithmException,
                       InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
            // ------------------------------------------------------------------------------- files
            // https://mbed-tls.readthedocs.io/en/latest/kb/cryptography/rsa-encryption-maximum-data-size/#:~:text=RSA%20is%20only%20able%20to,5%20padding).
            final var plainBytes = new byte[(keySize >> 3) - 11];
            // ------------------------------------------------------------------------------ cipher
            final String algorithm = "RSA";
            final String mode = "ECB";
            final String padding = "PKCS1Padding";
            final var cipher = Cipher.getInstance(algorithm + '/' + mode + '/' + padding);
            assert cipher.getBlockSize() == 0;
            // ----------------------------------------------------------------------------- keyPair
            final KeyPair keyPair;
            {
                final var generator = KeyPairGenerator.getInstance(algorithm);
                generator.initialize(keySize);
                keyPair = generator.generateKeyPair();
            }
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
                assertThat(decryptedBytes).isEqualTo(plainBytes);
            }
        }
    }

    @DisplayName("RSA/ECB/OAEPWithSHA-1AndMGF1Padding")
    @Nested
    class RsaEcbOaepwWithSha_1AndMgf1Padding {

        @ValueSource(ints = {
                1024,
                2048
        })
        @ParameterizedTest
        void __(final int keySize)
                throws NoSuchPaddingException, NoSuchAlgorithmException,
                       InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
            // ------------------------------------------------------------------------------- files
            // https://mbed-tls.readthedocs.io/en/latest/kb/cryptography/rsa-encryption-maximum-data-size/#:~:text=RSA%20is%20only%20able%20to,5%20padding).
            final var plainBytes = new byte[(keySize >> 3) - 42];
            // ------------------------------------------------------------------------------ cipher
            final String algorithm = "RSA";
            final String mode = "ECB";
            final String padding = "OAEPWithSHA-1AndMGF1Padding";
            final var cipher = Cipher.getInstance(algorithm + '/' + mode + '/' + padding);
            assert cipher.getBlockSize() == 0;
            // ----------------------------------------------------------------------------- keyPair
            final KeyPair keyPair;
            {
                final var generator = KeyPairGenerator.getInstance(algorithm);
                generator.initialize(keySize);
                keyPair = generator.generateKeyPair();
            }
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
                assertThat(decryptedBytes).isEqualTo(plainBytes);
            }
        }
    }

    @DisplayName("RSA/ECB/OAEPWithSHA-256AndMGF1Padding")
    @Nested
    class RsaEcbOaepwWithSha_256AndMgf1Padding {

        @ValueSource(ints = {
                1024,
                2048
        })
        @ParameterizedTest
        void __(final int keySize)
                throws NoSuchPaddingException, NoSuchAlgorithmException,
                       InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
            // ------------------------------------------------------------------------------- files
            // https://mbed-tls.readthedocs.io/en/latest/kb/cryptography/rsa-encryption-maximum-data-size/#:~:text=RSA%20is%20only%20able%20to,5%20padding).
            final var plainBytes = new byte[(keySize >> 3) - 66];
            // ------------------------------------------------------------------------------ cipher
            final String algorithm = "RSA";
            final String mode = "ECB";
            final String padding = "OAEPWithSHA-256AndMGF1Padding";
            final var cipher = Cipher.getInstance(algorithm + '/' + mode + '/' + padding);
            assert cipher.getBlockSize() == 0;
            // ----------------------------------------------------------------------------- keyPair
            final KeyPair keyPair;
            {
                final var generator = KeyPairGenerator.getInstance(algorithm);
                generator.initialize(keySize);
                keyPair = generator.generateKeyPair();
            }
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
                assertThat(decryptedBytes).isEqualTo(plainBytes);
            }
        }
    }
}
