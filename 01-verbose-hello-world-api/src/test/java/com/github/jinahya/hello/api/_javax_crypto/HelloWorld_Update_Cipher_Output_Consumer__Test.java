package com.github.jinahya.hello.api._javax_crypto;

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

import com.github.jinahya.hello.api.*;
import com.github.jinahya.hello.api.annotations.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import javax.crypto.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static com.github.jinahya.hello.miscellaneous._javax_crypto._Javax_Crypto_Cipher_TestUtils.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * A class for exploring
 * {@link HelloWorld#update(Cipher, ByteBuffer, IntConsumer) update(cipher, output,
 * outputLengthConsumer)} method with real {@link Cipher} transformations.
 */
@_NotForPublishing
@DisplayName("update(cipher, output, outputLengthConsumer)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Update_Cipher_Output_Consumer__Test extends HelloWorld__Test {

    @TempDir
    private static Path tempDir;

    // ---------------------------------------------------------------------------------------------
    // parameter: nullable; keysize or curve name
    private static void print(final String transformation, final Object parameter,
                              final ByteBuffer output, final int bytes) {
        final var encrypted = new byte[bytes];
        output.get(output.position() - bytes, encrypted);
        final var base64 = Base64.getEncoder().encodeToString(encrypted);
        final var summary = base64.length() <= 8
                            ? base64
                            :
                            base64.substring(0, 4) + "..." + base64.substring(base64.length() - 4);
        System.out.printf("%40s | %20s | %3d %4d | %s%n",
                          transformation,
                          Optional.ofNullable(parameter).orElse(""),
                          bytes,
                          bytes << 3,
                          summary);
    }

    // ---------------------------------------------------------------------------------------------

    @BeforeEach
    void __stubService() throws ShortBufferException {
        doAnswer(i -> {
            final var cipher = i.getArgument(0, Cipher.class);
            final var output = i.getArgument(1, ByteBuffer.class);
            final var consumer = i.getArgument(2, IntConsumer.class);
            consumer.accept(cipher.update(hello_world_byte_buffer(), output));
            return cipher;
        }).when(service()).update(notNull(), notNull(), notNull());
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies the method works with the {@code AES/CBC/NoPadding} transformation.
     *
     * @param keysize the key size, in bits.
     * @throws Exception if any error occurs.
     */
    @DisplayName("AES/CBC/NoPadding")
    @ValueSource(ints = {128})
    @ParameterizedTest
    void __AES_CBC_NoPadding(final int keysize) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var symmetric = AES_CBC_NoPadding(keysize);
        final var cipher = symmetric.cipher();
        cipher.init(Cipher.ENCRYPT_MODE, symmetric.secretKey(), symmetric.params());
        final var output = ByteBuffer.allocate(cipher.getOutputSize(HelloWorld.BYTES) * 2);
        // ------------------------------------------------------------------------------------ when
        service().update(cipher, output, b -> {
            // pad the input to a block boundary so NoPadding accepts doFinal
            final var pad = ByteBuffer.allocate(
                    cipher.getBlockSize() - HelloWorld.BYTES % cipher.getBlockSize());
            try {
                b += cipher.update(pad, output);
                b += cipher.doFinal(ByteBuffer.allocate(0), output);
                print("AES/CBC/NoPadding", keysize, output, b);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies the method works with the {@code AES/CBC/PKCS5Padding} transformation.
     *
     * @param keysize the key size, in bits.
     * @throws Exception if any error occurs.
     */
    @DisplayName("AES/CBC/PKCS5Padding")
    @ValueSource(ints = {128})
    @ParameterizedTest
    void __AES_CBC_PKCS5Padding(final int keysize) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var symmetric = AES_CBC_PKCS5Padding(keysize);
        final var cipher = symmetric.cipher();
        cipher.init(Cipher.ENCRYPT_MODE, symmetric.secretKey(), symmetric.params());
        final var output = ByteBuffer.allocate(cipher.getOutputSize(HelloWorld.BYTES) * 2);
        // ------------------------------------------------------------------------------------ when
        service().update(cipher, output, b -> {
            try {
                b += cipher.doFinal(ByteBuffer.allocate(0), output);
                print("AES/CBC/PKCS5Padding", keysize, output, b);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies the method works with the {@code AES/ECB/NoPadding} transformation.
     *
     * @param keysize the key size, in bits.
     * @throws Exception if any error occurs.
     */
    @DisplayName("AES/ECB/NoPadding")
    @ValueSource(ints = {128})
    @ParameterizedTest
    void __AES_ECB_NoPadding(final int keysize) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var symmetric = AES_ECB_NoPadding(keysize);
        final var cipher = symmetric.cipher();
        cipher.init(Cipher.ENCRYPT_MODE, symmetric.secretKey());
        final var output = ByteBuffer.allocate(cipher.getOutputSize(HelloWorld.BYTES) * 2);
        // ------------------------------------------------------------------------------------ when
        service().update(cipher, output, b -> {
            // pad the input to a block boundary so NoPadding accepts doFinal
            final var pad = ByteBuffer.allocate(
                    cipher.getBlockSize() - HelloWorld.BYTES % cipher.getBlockSize());
            try {
                b += cipher.update(pad, output);
                b += cipher.doFinal(ByteBuffer.allocate(0), output);
                print("AES/ECB/NoPadding", keysize, output, b);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies the method works with the {@code AES/ECB/PKCS5Padding} transformation.
     *
     * @param keysize the key size, in bits.
     * @throws Exception if any error occurs.
     */
    @DisplayName("AES/ECB/PKCS5Padding")
    @ValueSource(ints = {128})
    @ParameterizedTest
    void __AES_ECB_PKCS5Padding(final int keysize) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var symmetric = AES_ECB_PKCS5Padding(keysize);
        final var cipher = symmetric.cipher();
        cipher.init(Cipher.ENCRYPT_MODE, symmetric.secretKey());
        final var output = ByteBuffer.allocate(cipher.getOutputSize(HelloWorld.BYTES) * 2);
        // ------------------------------------------------------------------------------------ when
        service().update(cipher, output, b -> {
            try {
                b += cipher.doFinal(ByteBuffer.allocate(0), output);
                print("AES/ECB/PKCS5Padding", keysize, output, b);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies the method works with the {@code AES/GCM/NoPadding} transformation.
     *
     * @param keysize the key size, in bits.
     * @throws Exception if any error occurs.
     */
    @DisplayName("AES/GCM/NoPadding")
    @ValueSource(ints = {128, 256})
    @ParameterizedTest
    void __AES_GCM_NoPadding(final int keysize) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var symmetric = AES_GCM_NoPadding(keysize);
        final var cipher = symmetric.cipher();
        cipher.init(Cipher.ENCRYPT_MODE, symmetric.secretKey(), symmetric.params());
        if (symmetric.aad() != null) {
            cipher.updateAAD(symmetric.aad());
        }
        final var output = ByteBuffer.allocate(cipher.getOutputSize(HelloWorld.BYTES) * 2);
        // ------------------------------------------------------------------------------------ when
        service().update(cipher, output, b -> {
            try {
                b += cipher.doFinal(ByteBuffer.allocate(0), output);
                print("AES/GCM/NoPadding", keysize, output, b);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies the method works with the {@code ChaCha20-Poly1305} transformation.
     *
     * @throws Exception if any error occurs.
     */
    @DisplayName("ChaCha20-Poly1305")
    @Test
    void __ChaCha20_Poly1305() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var symmetric = ChaCha20_Poly1305();
        final var cipher = symmetric.cipher();
        cipher.init(Cipher.ENCRYPT_MODE, symmetric.secretKey(), symmetric.params());
        if (symmetric.aad() != null) {
            cipher.updateAAD(symmetric.aad());
        }
        final var output = ByteBuffer.allocate(cipher.getOutputSize(HelloWorld.BYTES) * 2);
        // ------------------------------------------------------------------------------------ when
        service().update(cipher, output, b -> {
            try {
                b += cipher.doFinal(ByteBuffer.allocate(0), output);
                print("ChaCha20-Poly1305", null, output, b);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies the method works with the {@code DESede/CBC/NoPadding} transformation.
     *
     * @param keysize the key size, in bits.
     * @throws Exception if any error occurs.
     */
    @DisplayName("DESede/CBC/NoPadding")
    @ValueSource(ints = {168})
    @ParameterizedTest
    void __DESede_CBC_NoPadding(final int keysize) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var symmetric = DESede_CBC_NoPadding(keysize);
        final var cipher = symmetric.cipher();
        cipher.init(Cipher.ENCRYPT_MODE, symmetric.secretKey(), symmetric.params());
        final var output = ByteBuffer.allocate(cipher.getOutputSize(HelloWorld.BYTES) * 2);
        // ------------------------------------------------------------------------------------ when
        service().update(cipher, output, b -> {
            // pad the input to a block boundary so NoPadding accepts doFinal
            final var pad = ByteBuffer.allocate(
                    cipher.getBlockSize() - HelloWorld.BYTES % cipher.getBlockSize());
            try {
                b += cipher.update(pad, output);
                b += cipher.doFinal(ByteBuffer.allocate(0), output);
                print("DESede/CBC/NoPadding", keysize, output, b);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies the method works with the {@code DESede/CBC/PKCS5Padding} transformation.
     *
     * @param keysize the key size, in bits.
     * @throws Exception if any error occurs.
     */
    @DisplayName("DESede/CBC/PKCS5Padding")
    @ValueSource(ints = {168})
    @ParameterizedTest
    void __DESede_CBC_PKCS5Padding(final int keysize) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var symmetric = DESede_CBC_PKCS5Padding(keysize);
        final var cipher = symmetric.cipher();
        cipher.init(Cipher.ENCRYPT_MODE, symmetric.secretKey(), symmetric.params());
        final var output = ByteBuffer.allocate(cipher.getOutputSize(HelloWorld.BYTES) * 2);
        // ------------------------------------------------------------------------------------ when
        service().update(cipher, output, b -> {
            try {
                b += cipher.doFinal(ByteBuffer.allocate(0), output);
                print("DESede/CBC/PKCS5Padding", keysize, output, b);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies the method works with the {@code DESede/ECB/NoPadding} transformation.
     *
     * @param keysize the key size, in bits.
     * @throws Exception if any error occurs.
     */
    @DisplayName("DESede/ECB/NoPadding")
    @ValueSource(ints = {168})
    @ParameterizedTest
    void __DESede_ECB_NoPadding(final int keysize) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var symmetric = DESede_ECB_NoPadding(keysize);
        final var cipher = symmetric.cipher();
        cipher.init(Cipher.ENCRYPT_MODE, symmetric.secretKey());
        final var output = ByteBuffer.allocate(cipher.getOutputSize(HelloWorld.BYTES) * 2);
        // ------------------------------------------------------------------------------------ when
        service().update(cipher, output, b -> {
            // pad the input to a block boundary so NoPadding accepts doFinal
            final var pad = ByteBuffer.allocate(
                    cipher.getBlockSize() - HelloWorld.BYTES % cipher.getBlockSize());
            try {
                b += cipher.update(pad, output);
                b += cipher.doFinal(ByteBuffer.allocate(0), output);
                print("DESede/ECB/NoPadding", keysize, output, b);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies the method works with the {@code DESede/ECB/PKCS5Padding} transformation.
     *
     * @param keysize the key size, in bits.
     * @throws Exception if any error occurs.
     */
    @DisplayName("DESede/ECB/PKCS5Padding")
    @ValueSource(ints = {168})
    @ParameterizedTest
    void __DESede_ECB_PKCS5Padding(final int keysize) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var symmetric = DESede_ECB_PKCS5Padding(keysize);
        final var cipher = symmetric.cipher();
        cipher.init(Cipher.ENCRYPT_MODE, symmetric.secretKey());
        final var output = ByteBuffer.allocate(cipher.getOutputSize(HelloWorld.BYTES) * 2);
        // ------------------------------------------------------------------------------------ when
        service().update(cipher, output, b -> {
            try {
                b += cipher.doFinal(ByteBuffer.allocate(0), output);
                print("DESede/ECB/PKCS5Padding", keysize, output, b);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies the method works with the {@code PBEWithHmacSHA256AndAES_128} transformation.
     *
     * @throws Exception if any error occurs.
     */
    @DisplayName("PBEWithHmacSHA256AndAES_128")
    @_LatestJDK
    @Test
    void __PBEWithHmacSHA256AndAES_128() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var symmetric = PBEWithHmacSHA256AndAES_128();
        final var cipher = symmetric.cipher();
        cipher.init(Cipher.ENCRYPT_MODE, symmetric.secretKey(), symmetric.params());
        final var output = ByteBuffer.allocate(cipher.getOutputSize(HelloWorld.BYTES) * 2);
        // ------------------------------------------------------------------------------------ when
        service().update(cipher, output, b -> {
            try {
                b += cipher.doFinal(ByteBuffer.allocate(0), output);
                print("PBEWithHmacSHA256AndAES_128", null, output, b);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies the method works with the {@code PBEWithHmacSHA256AndAES_256} transformation.
     *
     * @throws Exception if any error occurs.
     */
    @DisplayName("PBEWithHmacSHA256AndAES_256")
    @_LatestJDK
    @Test
    void __PBEWithHmacSHA256AndAES_256() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var symmetric = PBEWithHmacSHA256AndAES_256();
        final var cipher = symmetric.cipher();
        cipher.init(Cipher.ENCRYPT_MODE, symmetric.secretKey(), symmetric.params());
        final var output = ByteBuffer.allocate(cipher.getOutputSize(HelloWorld.BYTES) * 2);
        // ------------------------------------------------------------------------------------ when
        service().update(cipher, output, b -> {
            try {
                b += cipher.doFinal(ByteBuffer.allocate(0), output);
                print("PBEWithHmacSHA256AndAES_256", null, output, b);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies the method works with the {@code RSA/ECB/PKCS1Padding} transformation.
     *
     * @param keysize the key size, in bits.
     * @throws Exception if any error occurs.
     */
    @DisplayName("RSA/ECB/PKCS1Padding")
    @ValueSource(ints = {1024, 2048})
    @ParameterizedTest
    void __RSA_ECB_PKCS1Padding(final int keysize) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var asymmetric = RSA_ECB_PKCS1Padding(keysize);
        final var cipher = asymmetric.cipher();
        cipher.init(Cipher.ENCRYPT_MODE, asymmetric.keyPair().getPublic());
        final var output = ByteBuffer.allocate(cipher.getOutputSize(HelloWorld.BYTES) * 2);
        // ------------------------------------------------------------------------------------ when
        service().update(cipher, output, b -> {
            try {
                b += cipher.doFinal(ByteBuffer.allocate(0), output);
                print("RSA/ECB/PKCS1Padding", keysize, output, b);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies the method works with the {@code RSA/ECB/OAEPWithSHA-1AndMGF1Padding}
     * transformation.
     *
     * @param keysize the key size, in bits.
     * @throws Exception if any error occurs.
     */
    @DisplayName("RSA/ECB/OAEPWithSHA-1AndMGF1Padding")
    @ValueSource(ints = {1024, 2048})
    @ParameterizedTest
    void __RSA_ECB_OAEPWithSHA_1AndMGF1Padding(final int keysize) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var asymmetric = RSA_ECB_OAEPWithSHA_1AndMGF1Padding(keysize);
        final var cipher = asymmetric.cipher();
        cipher.init(Cipher.ENCRYPT_MODE, asymmetric.keyPair().getPublic());
        final var output = ByteBuffer.allocate(cipher.getOutputSize(HelloWorld.BYTES) * 2);
        // ------------------------------------------------------------------------------------ when
        service().update(cipher, output, b -> {
            try {
                b += cipher.doFinal(ByteBuffer.allocate(0), output);
                print("RSA/ECB/OAEPWithSHA-1AndMGF1Padding", keysize, output, b);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies the method works with the {@code RSA/ECB/OAEPWithSHA-256AndMGF1Padding}
     * transformation.
     *
     * @param keysize the key size, in bits.
     * @throws Exception if any error occurs.
     */
    @DisplayName("RSA/ECB/OAEPWithSHA-256AndMGF1Padding")
    @ValueSource(ints = {1024, 2048})
    @ParameterizedTest
    void __RSA_ECB_OAEPWithSHA_256AndMGF1Padding(final int keysize) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var asymmetric = RSA_ECB_OAEPWithSHA_256AndMGF1Padding(keysize);
        final var cipher = asymmetric.cipher();
        cipher.init(Cipher.ENCRYPT_MODE, asymmetric.keyPair().getPublic());
        final var output = ByteBuffer.allocate(cipher.getOutputSize(HelloWorld.BYTES) * 2);
        // ------------------------------------------------------------------------------------ when
        service().update(cipher, output, b -> {
            try {
                b += cipher.doFinal(ByteBuffer.allocate(0), output);
                print("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", keysize, output, b);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
