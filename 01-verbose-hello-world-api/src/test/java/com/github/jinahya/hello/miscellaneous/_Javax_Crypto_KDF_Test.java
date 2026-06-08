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

import static com.github.jinahya.hello.miscellaneous._Javax_Crypto_KDF_TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * A class for testing {@link javax.crypto.KDF} with the JCA-mandatory {@code HKDF} algorithms.
 *
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/crypto/KDF.html">javax.crypto.KDF</a>
 * (Java 25)
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/26/docs/api/java.base/javax/crypto/KDF.html">javax.crypto.KDF</a>
 * (Java 26)
 */
@DisplayName("KDF")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
class _Javax_Crypto_KDF_Test {

    // picked; output length L bound by L ≤ 255 × HashLen (RFC 5869 §2.3); 32 = AES-256 / SHA-256
    private static final int OUTPUT_BYTES = 32;

    // picked; algorithm name passed to KDF.deriveKey(String, AlgorithmParameterSpec)
    private static final String KEY_ALGORITHM = "AES";

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code HKDF-SHA256} derives deterministic data and keys from the same
     * {@code spec}.
     *
     * @throws Exception if any error occurs.
     */
    @DisplayName("HKDF-SHA256")
    @Test
    void __HKDF_SHA256() throws Exception {
        final var bundle = HKDF_SHA256(OUTPUT_BYTES);
        // ------------------------------------------------------------------------------ deriveData
        {
            final var data1 = bundle.kdf().deriveData(bundle.spec());
            final var data2 = bundle.kdf().deriveData(bundle.spec());
            assertEquals(OUTPUT_BYTES, data1.length);
            assertArrayEquals(data1, data2);
        }
        // ------------------------------------------------------------------------------- deriveKey
        {
            final var key1 = bundle.kdf().deriveKey(KEY_ALGORITHM, bundle.spec());
            final var key2 = bundle.kdf().deriveKey(KEY_ALGORITHM, bundle.spec());
            assertEquals(KEY_ALGORITHM, key1.getAlgorithm());
            assertEquals(OUTPUT_BYTES, key1.getEncoded().length);
            assertArrayEquals(key1.getEncoded(), key2.getEncoded());
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code HKDF-SHA384} derives deterministic data and keys from the same
     * {@code spec}.
     *
     * @throws Exception if any error occurs.
     */
    @DisplayName("HKDF-SHA384")
    @Test
    void __HKDF_SHA384() throws Exception {
        final var bundle = HKDF_SHA384(OUTPUT_BYTES);
        // ------------------------------------------------------------------------------ deriveData
        {
            final var data1 = bundle.kdf().deriveData(bundle.spec());
            final var data2 = bundle.kdf().deriveData(bundle.spec());
            assertEquals(OUTPUT_BYTES, data1.length);
            assertArrayEquals(data1, data2);
        }
        // ------------------------------------------------------------------------------- deriveKey
        {
            final var key1 = bundle.kdf().deriveKey(KEY_ALGORITHM, bundle.spec());
            final var key2 = bundle.kdf().deriveKey(KEY_ALGORITHM, bundle.spec());
            assertEquals(KEY_ALGORITHM, key1.getAlgorithm());
            assertEquals(OUTPUT_BYTES, key1.getEncoded().length);
            assertArrayEquals(key1.getEncoded(), key2.getEncoded());
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code HKDF-SHA512} derives deterministic data and keys from the same
     * {@code spec}.
     *
     * @throws Exception if any error occurs.
     */
    @DisplayName("HKDF-SHA512")
    @Test
    void __HKDF_SHA512() throws Exception {
        final var bundle = HKDF_SHA512(OUTPUT_BYTES);
        // ------------------------------------------------------------------------------ deriveData
        {
            final var data1 = bundle.kdf().deriveData(bundle.spec());
            final var data2 = bundle.kdf().deriveData(bundle.spec());
            assertEquals(OUTPUT_BYTES, data1.length);
            assertArrayEquals(data1, data2);
        }
        // ------------------------------------------------------------------------------- deriveKey
        {
            final var key1 = bundle.kdf().deriveKey(KEY_ALGORITHM, bundle.spec());
            final var key2 = bundle.kdf().deriveKey(KEY_ALGORITHM, bundle.spec());
            assertEquals(KEY_ALGORITHM, key1.getAlgorithm());
            assertEquals(OUTPUT_BYTES, key1.getEncoded().length);
            assertArrayEquals(key1.getEncoded(), key2.getEncoded());
        }
    }
}
