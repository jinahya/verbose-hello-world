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

import lombok.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.nio.charset.*;
import java.security.spec.*;
import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A test class exploring {@link KDF} (the JEP 478 key-derivation engine class introduced in Java
 * 24) for HKDF derivations, bound explicitly to the BouncyCastle JCE provider so the assertions
 * don't depend on which JDK distribution provides {@code HKDF-SHA*}.
 *
 * <p>Each test parameterises over the three HMAC digests BouncyCastle registers under the new
 * {@link KDF} service ({@code HKDF-SHA256}, {@code HKDF-SHA384}, {@code HKDF-SHA512}) and asserts
 * that {@link KDF#deriveData(AlgorithmParameterSpec)} is deterministic for the same IKM / salt /
 * info / length (re-deriving twice yields byte-identical output), the property that makes HKDF
 * usable as the "spread the shared secret into independent subkeys" step after a key agreement or
 * KEM.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://openjdk.org/jeps/478">JEP 478 &mdash; Key Derivation Function API
 * (Preview)</a>
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc5869">RFC 5869 &mdash; HMAC-based
 * Extract-and-Expand Key Derivation Function (HKDF)</a>
 */
@Disabled
@DisplayName("KDF")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
class _Javax_Crypto_KDF__Test {

    static {
        _org_bouncycastle_jce_provider__TestUtils.addBouncyCastleProvider();
    }

    /**
     * Prints a one-line summary of a derivation &mdash; algorithm / provider label, output byte
     * length, and Base64-encoded first / last four characters &mdash; to {@link System#out}.
     */
    private static void printf(final String label, final byte[] output) {
        final var encoded = Base64.getEncoder().encodeToString(output);
        System.out.printf("%-22s (%4d) %s...%s%n",
                          label,
                          output.length,
                          encoded.substring(0, 4),
                          encoded.substring(encoded.length() - 4));
    }

    /**
     * A nested test class iterating over every {@code HKDF-SHA*} algorithm BouncyCastle registers
     * for {@link KDF} (SHA-256 / SHA-384 / SHA-512) and asserting that re-deriving with the same
     * {@link HKDFParameterSpec} yields a byte-identical output.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc5869">RFC 5869 &mdash; HMAC-based
     * Extract-and-Expand Key Derivation Function (HKDF)</a>
     */
    @DisplayName("HKDF-SHA* (extract + expand)")
    @Nested
    class HKDF_Test {

        // 32 bytes — typical high-entropy IKM length (e.g., an ECDH shared secret would be 32 bytes
        // for P-256)
        private static final int IKM_BYTES = 32;

        // 16 bytes — typical salt; HKDF allows any salt length, including empty
        private static final int SALT_BYTES = 16;

        // 32 bytes — derive an AES-256 sized key
        private static final int OUTPUT_BYTES = 32;

        /**
         * Verifies that {@link KDF#deriveData(AlgorithmParameterSpec)} called twice with the same
         * {@link HKDFParameterSpec} (same IKM, salt, info, length) yields byte-identical output,
         * and that the output length matches the requested {@value #OUTPUT_BYTES} bytes.
         *
         * @param algorithm a JCE standard {@code HKDF-SHA*} algorithm name.
         */
        @DisplayName("should derive deterministic bytes via the given <HKDF-SHA*>")
        @ValueSource(strings = {
                "HKDF-SHA256",
                "HKDF-SHA384",
                "HKDF-SHA512"
        })
        @ParameterizedTest
        void __(final String algorithm) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var kdf = KDF.getInstance(
                    algorithm,
                    org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
            final var ikm = new byte[IKM_BYTES];
            ThreadLocalRandom.current().nextBytes(ikm);
            final var salt = new byte[SALT_BYTES];
            ThreadLocalRandom.current().nextBytes(salt);
            final var info = "hello, world".getBytes(StandardCharsets.UTF_8);
            // -------------------------------------------------------------------------------- when
            final var spec = HKDFParameterSpec.ofExtract()
                    .addIKM(ikm).addSalt(salt).thenExpand(info, OUTPUT_BYTES);
            final var out1 = kdf.deriveData(spec);
            final var out2 = kdf.deriveData(spec);
            // -------------------------------------------------------------------------------- then
            printf(algorithm + "/BC", out1);
            assertEquals(OUTPUT_BYTES, out1.length);
            assertArrayEquals(out1, out2);
        }

        /**
         * Verifies that {@link KDF#deriveKey(String, AlgorithmParameterSpec)} produces a
         * {@link SecretKey} whose encoded bytes are byte-identical to the
         * {@link KDF#deriveData(AlgorithmParameterSpec)} output for the same spec, and that the
         * key's algorithm name matches the requested {@code "AES"}.
         *
         * @param algorithm a JCE standard {@code HKDF-SHA*} algorithm name.
         */
        @DisplayName(
                "should derive a SecretKey whose bytes equal deriveData(...) via the given <HKDF-SHA*>")
        @ValueSource(strings = {
                "HKDF-SHA256",
                "HKDF-SHA384",
                "HKDF-SHA512"
        })
        @ParameterizedTest
        void __asKey(final String algorithm) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var kdf = KDF.getInstance(
                    algorithm,
                    org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
            final var ikm = new byte[IKM_BYTES];
            ThreadLocalRandom.current().nextBytes(ikm);
            final var salt = new byte[SALT_BYTES];
            ThreadLocalRandom.current().nextBytes(salt);
            final var info = "hello, world".getBytes(StandardCharsets.UTF_8);
            // -------------------------------------------------------------------------------- when
            final var spec = HKDFParameterSpec.ofExtract()
                    .addIKM(ikm).addSalt(salt).thenExpand(info, OUTPUT_BYTES);
            final var dataBytes = kdf.deriveData(spec);
            final SecretKey key = kdf.deriveKey("AES", spec);
            // -------------------------------------------------------------------------------- then
            printf(algorithm + "/BC/AES", key.getEncoded());
            assertEquals("AES", key.getAlgorithm());
            assertArrayEquals(dataBytes, key.getEncoded());
        }
    }
}
