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

import com.github.jinahya.hello.util.java.io._JavaIoUtils;
import com.github.jinahya.hello.util.java.nio.JavaNioUtils;
import com.github.jinahya.hello.util.java.nio.file.PathUtils;
import com.github.jinahya.hello.util.java.security.MessageDigestConstants;
import com.github.jinahya.hello.util.java.security.MessageDigestUtils;
import com.github.jinahya.hello.util.javax.crypto.CipherUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
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
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HexFormat;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

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
 * Algorithm Paddings</a>
 */
@DisplayName("javax.crypto.Cipher")
@Slf4j
class Cipher_DESede_Test {

    static {
//        Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    static Key generateKey(final String algorithm, final int keysize)
            throws NoSuchAlgorithmException {
        final var generator = KeyGenerator.getInstance(algorithm);
        generator.init(keysize);
        return generator.generateKey();
    }

    private static IntStream desKeysizes() {
        return IntStream.of(
                168
        );
    }

    // https://www.thalesdocs.com/gphsm/ptk/5.7/docs/Content/PTK-J/Ciphers/DESede.htm
    @DisplayName("DESede/CBC/NoPadding")
    @Nested
    class DESede_CBC_NoPadding_Test {

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
            final var messageDigestAlgorithm = MessageDigestConstants.ALGORITHM_MD5;
            final var plainFileDigest = MessageDigestUtils.getDigest(
                    messageDigestAlgorithm,
                    plainFile,
                    new byte[1024]
            );
            final var decryptedFileDigest = MessageDigestUtils.getDigest(
                    messageDigestAlgorithm,
                    decryptedFile,
                    new byte[1024]
            );
            assertThat(decryptedFileDigest).isEqualTo(plainFileDigest);
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
            final var messageDigestAlgorithm = MessageDigestConstants.ALGORITHM_SHA_1;
            final var plainFileDigest = MessageDigestUtils.getDigest(
                    messageDigestAlgorithm,
                    plainPath,
                    ByteBuffer.allocate(1024)
            );
            final var decryptedFileDigest = MessageDigestUtils.getDigest(
                    messageDigestAlgorithm,
                    decryptedPath,
                    ByteBuffer.allocate(1024)
            );
            assertThat(decryptedFileDigest).isEqualTo(plainFileDigest);
        }
    }

    // https://www.thalesdocs.com/gphsm/ptk/5.7/docs/Content/PTK-J/Ciphers/DESede.htm
    @DisplayName("DESede/CBC/PKCS5Padding")
    @Nested
    class DESede_CBC_PKCS5Padding_Test {

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
                final var messageDigestAlgorithm = MessageDigestConstants.ALGORITHM_MD5;
                final var plainFileDigest = MessageDigestUtils.getDigest(
                        messageDigestAlgorithm,
                        plainFile,
                        new byte[1024]
                );
                final var decryptedFileDigest = MessageDigestUtils.getDigest(
                        messageDigestAlgorithm,
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
            final var messageDigestAlgorithm = MessageDigestConstants.ALGORITHM_SHA_1;
            final var plainFileDigest = MessageDigestUtils.getDigest(
                    messageDigestAlgorithm,
                    plainPath,
                    ByteBuffer.allocate(1024)
            );
            final var decryptedFileDigest = MessageDigestUtils.getDigest(
                    messageDigestAlgorithm,
                    decryptedPath,
                    ByteBuffer.allocate(1024)
            );
            assertThat(decryptedFileDigest).isEqualTo(plainFileDigest);
        }
    }

    // https://www.thalesdocs.com/gphsm/ptk/5.7/docs/Content/PTK-J/Ciphers/DESede.htm
    @DisplayName("DESede/ECB/NoPadding")
    @Nested
    class DESede_ECB_NoPadding_Test {

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
                final var messageDigestAlgorithm = MessageDigestConstants.ALGORITHM_MD5;
                final var plainFileDigest = MessageDigestUtils.getDigest(
                        messageDigestAlgorithm,
                        plainFile,
                        new byte[1024]
                );
                final var decryptedFileDigest = MessageDigestUtils.getDigest(
                        messageDigestAlgorithm,
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
                final var messageDigestAlgorithm = MessageDigestConstants.ALGORITHM_SHA_1;
                final var plainFileDigest = MessageDigestUtils.getDigest(
                        messageDigestAlgorithm,
                        plainPath,
                        ByteBuffer.allocate(1024)
                );
                final var decryptedFileDigest = MessageDigestUtils.getDigest(
                        messageDigestAlgorithm,
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
    class DESede_ECB_PKCS5Padding_Test {

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
            final var messageDigestAlgorithm = MessageDigestConstants.ALGORITHM_MD5;
            final var plainFileDigest = MessageDigestUtils.getDigest(
                    messageDigestAlgorithm,
                    plainFile,
                    new byte[1024]
            );
            final var decryptedFileDigest = MessageDigestUtils.getDigest(
                    messageDigestAlgorithm,
                    decryptedFile,
                    new byte[1024]
            );
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
                assertThat(decryptedPath)
                        .as("size of the decrypted path")
                        .hasSize(Files.size(plainPath));
            }
            // ------------------------------------------------------------------------------ verify
            {
                final var messageDigestAlgorithm = MessageDigestConstants.ALGORITHM_SHA_1;
                final var plainPathDigest = MessageDigestUtils.getDigest(
                        messageDigestAlgorithm,
                        plainPath,
                        ByteBuffer.allocate(1024)
                );
                log.debug("plainPathDigest: {}", HexFormat.of().formatHex(plainPathDigest));
                final var decryptedPathDigest = MessageDigestUtils.getDigest(
                        messageDigestAlgorithm,
                        decryptedPath,
                        ByteBuffer.allocate(1024)
                );
                log.debug("decryptedPathDigest: {}", HexFormat.of().formatHex(decryptedPathDigest));
                assertThat(decryptedPathDigest).isEqualTo(plainPathDigest);
            }
        }
    }
}
