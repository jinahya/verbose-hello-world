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

import javax.crypto.*;
import java.io.*;
import java.nio.file.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static com.github.jinahya.hello.miscellaneous._java_security._Java_Security_KeyPair_TestUtils.*;
import static com.github.jinahya.hello.miscellaneous._javax_crypto._Javax_Crypto_TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * A class for exploring {@link HelloWorld#write(OutputStream) write(stream)} method with real
 * {@link CipherOutputStream} / {@link CipherInputStream} round-trips.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@_NotForPublishing
@DisplayName("javax.crypto.Cipher")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Cipher__Test extends HelloWorld__Test {

    @TempDir
    private static Path tempDir;

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    @SuppressWarnings({"unchecked"})
    void __subService() throws IOException {
        write_outputstream_writes_hello_world_bytes(service());
//        doAnswer(i -> {
//            final var cipher = i.getArgument(0, Cipher.class);
//            final var consumer = i.getArgument(1, Consumer.class);
//            consumer.accept(cipher.update(hello_world_byte_array()));
//            return cipher;
//        }).when(service()).update(any(), any());
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("CipherOutputStream")
    @Disabled
    @Nested
    class CipherOutputStream_Test {

        /**
         * Verifies that the {@code "hello, world"} bytes round-trip through
         * {@link CipherOutputStream} / {@link CipherInputStream} under
         * {@code AES/ECB/PKCS5Padding}.
         */
        @DisplayName("AES/ECB/PKCS5Padding")
        @Test
        void __AES_ECB_PKCS5Padding() throws Exception {
            final var key = generateSecretKey("AES", 128);
            final byte[] encrypted;
            try (var baos = new ByteArrayOutputStream()) {
                final var cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, key);
                try (var cos = new CipherOutputStream(baos, cipher)) {
                    service().write(cos).flush();
                }
                encrypted = baos.toByteArray();
            }
            final byte[] decrypted;
            try (var bais = new ByteArrayInputStream(encrypted)) {
                final var cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, key);
                try (var cis = new CipherInputStream(bais, cipher)) {
                    decrypted = cis.readAllBytes();
                }
            }
            assertArrayEquals(hello_world_byte_array(), decrypted);
        }

        /**
         * Verifies that the {@code "hello, world"} bytes round-trip through
         * {@link CipherOutputStream} / {@link CipherInputStream} under
         * {@code RSA/ECB/PKCS1Padding}.
         */
        @DisplayName("RSA/ECB/PKCS1Padding")
        @Test
        void __RSA_ECB_PKCS1Padding() throws Exception {
            final var keyPair = generateKeyPair("RSA", 1024);
            final byte[] encrypted;
            try (var baos = new ByteArrayOutputStream()) {
                final var cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
                try (var cos = new CipherOutputStream(baos, cipher)) {
                    service().write(cos).flush();
                }
                encrypted = baos.toByteArray();
            }
            final byte[] decrypted;
            try (var bais = new ByteArrayInputStream(encrypted)) {
                final var cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
                try (var cis = new CipherInputStream(bais, cipher)) {
                    decrypted = cis.readAllBytes();
                }
            }
            assertArrayEquals(hello_world_byte_array(), decrypted);
        }
    }
}
