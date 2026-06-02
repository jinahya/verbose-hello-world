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
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.*;
import org.mockito.*;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.nio.file.*;
import java.security.spec.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * An integration test class for
 * {@link com.github.jinahya.hello.api.HelloWorld#update(Mac) HelloWorld.update(mac)} method that
 * exercises real {@link Mac} algorithms &mdash; HMAC and PBE-MAC.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see com.github.jinahya.hello.api.HelloWorld#update(Mac)
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc2104">RFC 2104 &mdash; HMAC: Keyed-Hashing
 * for Message Authentication</a>
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc4231">RFC 4231 &mdash; Identifiers and
 * Test Vectors for HMAC-SHA-224, -256, -384, -512</a>
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc6234">RFC 6234 &mdash; US Secure Hash
 * Algorithms (SHA, SHA-based HMAC, HKDF)</a>
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc8018">RFC 8018 &mdash; PKCS #5:
 * Password-Based Cryptography Specification Version 2.1 (PBE)</a>
 */
@DisplayName("update(mac)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Update_Mac__Test extends HelloWorld__Test {

    @TempDir
    private static Path temDir;

    /**
     * Prints a one-line summary of a MAC tag or derived hash &mdash; algorithm, optional parameter,
     * byte length, and Base64-encoded first/last four characters &mdash; to {@link System#out}.
     *
     * @param algorithm the algorithm name to display.
     * @param parameter an optional parameter to display (may be {@code null}).
     * @param tag       the bytes to summarize.
     */
    private static void printf(final String algorithm, final Object parameter, final byte[] tag) {
        final var encoded = Base64.getEncoder().encodeToString(tag);
        System.out.printf("%30s %20s (%4d) %s...%s%n", algorithm,
                          Optional.ofNullable(parameter).orElse(""),
                          tag.length,
                          encoded.substring(0, 4),
                          encoded.substring(encoded.length() - 4));
    }

    /**
     * Stubs {@link com.github.jinahya.hello.api.HelloWorld#update(Mac) service().update(mac)} so
     * that, when invoked with any non-{@code null} {@link Mac}, it forwards the
     * {@link HelloWorld__TestUtils#hello_world_byte_array() hello-world bytes} to the mac's
     * {@link Mac#update(byte[])} method and returns the mac.
     */
    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void __() {
        Mockito.doAnswer(i -> {
            final var mac = i.getArgument(0, Mac.class);
            mac.update(HelloWorld__TestUtils.hello_world_byte_array());
            return mac;
        }).when(service()).update(ArgumentMatchers.<Mac>notNull());
    }

    /**
     * A nested test class for {@code HmacSHA1}: authenticates the hello-world bytes with one
     * {@link Mac} and recomputes the tag with another mac initialized with the same key, then
     * asserts the two tags are byte-identical.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc2104">RFC 2104 &mdash; HMAC</a>
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc6234">RFC 6234 &mdash; US Secure Hash
     * Algorithms</a>
     */
    // ---------------------------------------------------------------------------------------------
    @DisplayName("HmacSHA1")
    @Nested
    class HmacSha1_Test {

        // JCE std name (HMAC: RFC 2104; SHA-1: FIPS 180-4)
        private static final String ALGORITHM = "HmacSHA1";

        /**
         * Verifies that authenticating the hello-world bytes twice with two
         * {@link Mac#getInstance(String) HmacSHA1} instances initialized with the same key produces
         * byte-identical tags.
         */
        @DisplayName("should produce byte-identical tags through a <real HmacSHA1> mac")
        @Test
        void __() throws Exception {
            // ------------------------------------------------------------------------------- given
            final var generator = KeyGenerator.getInstance(ALGORITHM);
            final var key = generator.generateKey();
            // -------------------------------------------------------------------------------------
            final byte[] tag1;
            // ------------------------------------------------------------------------ authenticate
            {
                final var mac = Mac.getInstance(ALGORITHM);
                mac.init(key);
                tag1 = service().update(mac).doFinal();
                printf(ALGORITHM, null, tag1);
            }
            // ------------------------------------------------------------------------------ verify
            {
                final var mac = Mac.getInstance(ALGORITHM);
                mac.init(key);
                final var tag2 = service().update(mac).doFinal();
                Assertions.assertArrayEquals(tag1, tag2);
            }
        }
    }

    /**
     * A nested test class for {@code HmacSHA256}: authenticates the hello-world bytes with one
     * {@link Mac} and recomputes the tag with another mac initialized with the same key, then
     * asserts the two tags are byte-identical.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc2104">RFC 2104 &mdash; HMAC</a>
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc4231">RFC 4231 &mdash; HMAC-SHA Test
     * Vectors</a>
     */
    @DisplayName("HmacSHA256")
    @Nested
    class HmacSHA256_Test {

        // JCE std name (HMAC: RFC 2104; SHA-256: RFC 6234)
        private static final String ALGORITHM = "HmacSHA256";

        /**
         * Verifies that authenticating the hello-world bytes twice with two
         * {@link Mac#getInstance(String) HmacSHA256} instances initialized with the same key
         * produces byte-identical tags.
         */
        @DisplayName("should produce byte-identical tags through a <real HmacSHA256> mac")
        @Test
        void __() throws Exception {
            // ------------------------------------------------------------------------------- given
            final var generator = KeyGenerator.getInstance(ALGORITHM);
            final var key = generator.generateKey();
            // -------------------------------------------------------------------------------------
            final byte[] tag1;
            // ------------------------------------------------------------------------ authenticate
            {
                final var mac = Mac.getInstance(ALGORITHM);
                mac.init(key);
                tag1 = service().update(mac).doFinal();
                printf(ALGORITHM, null, tag1);
            }
            // ------------------------------------------------------------------------------ verify
            {
                final var mac = Mac.getInstance(ALGORITHM);
                mac.init(key);
                final var tag2 = service().update(mac).doFinal();
                Assertions.assertArrayEquals(tag1, tag2);
            }
        }
    }

    /**
     * A nested test class for {@code PBEWithHmacSHA256}: derives an HMAC key from a passphrase via
     * {@link SecretKeyFactory} (PKCS#5 PBE), authenticates the hello-world bytes twice with the
     * derived key, and asserts the tags match.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc8018">RFC 8018 &mdash; PKCS #5
     * v2.1</a>
     */
    @DisplayName("PBEWithHmacSHA256")
    @Nested
    class PBEWithHmacSHA256_Test {

        // JCE PBE-MAC name (PKCS#5 v2.1 / RFC 8018)
        private static final String ALGORITHM = "PBEWithHmacSHA256";

        // JCE SecretKeyFactory name (PKCS#5 PBES2)
        private static final String KEY_FACTORY_ALGORITHM = ALGORITHM + "AndAES_256";

        /**
         * Verifies that authenticating the hello-world bytes twice with two
         * {@code PBEWithHmacSHA256} instances initialized with the same password-derived key and
         * parameters produces byte-identical tags.
         */
        @DisplayName("should produce byte-identical tags through a <real PBEWithHmacSHA256> mac")
        @Test
        void __() throws Exception {
            // ------------------------------------------------------------------------------- given
            final var password = "password".toCharArray();
            final SecretKey key;
            {
                final var keySpec = new PBEKeySpec(password);
                final var factory = SecretKeyFactory.getInstance(KEY_FACTORY_ALGORITHM);
                key = factory.generateSecret(keySpec);
            }
            final var salt = new byte[16];
            {
                ThreadLocalRandom.current().nextBytes(salt);
            }
            final var iterationCount = 1000;
            final AlgorithmParameterSpec params;
            {
                params = new PBEParameterSpec(salt, iterationCount);
            }
            // -------------------------------------------------------------------------------------
            final byte[] tag1;
            // ------------------------------------------------------------------------ authenticate
            {
                final var mac = Mac.getInstance(ALGORITHM);
                mac.init(key, params);
                tag1 = service().update(mac).doFinal();
                printf(ALGORITHM, iterationCount, tag1);
            }
            // ------------------------------------------------------------------------------ verify
            {
                final var mac = Mac.getInstance(ALGORITHM);
                mac.init(key, params);
                final var tag2 = service().update(mac).doFinal();
                Assertions.assertArrayEquals(tag1, tag2);
            }
        }
    }
}
