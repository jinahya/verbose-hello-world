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

import com.github.jinahya.hello.api.*;
import lombok.*;
import org.junit.jupiter.api.*;

import static com.github.jinahya.hello.miscellaneous._Javax_Crypto_SecretKeyFactory_TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * .
 *
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/crypto/SecretKeyFactory.html">javax.crypto.SecretKeyFactory</a>
 * (Java 25)
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/26/docs/api/java.base/javax/crypto/SecretKeyFactory.html">javax.crypto.SecretKeyFactory</a>
 * (Java 26)
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
class _Javax_Crypto_SecretKeyFactory_Test {

    // ---------------------------------------------------------------------------------------------
    @LatestLTS
    @DisplayName("DESede")
    @Test
    void __DESede() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var bundle = DESede();
        // ------------------------------------------------------------------------------------ when
        final var key1 = bundle.factory().generateSecret(bundle.spec()).getEncoded();
        final var key2 = bundle.factory().generateSecret(bundle.spec()).getEncoded();
        // ------------------------------------------------------------------------------------ then
        assertArrayEquals(key1, key2);
    }

    // ---------------------------------------------------------------------------------------------
    @LatestJDK
    @DisplayName("PBEWithHmacSHA256AndAES_128")
    @Test
    void __PBEWithHmacSHA256AndAES_128() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var bundle = PBEWithHmacSHA256AndAES_128();
        // ------------------------------------------------------------------------------------ when
        final var key1 = bundle.factory().generateSecret(bundle.spec()).getEncoded();
        final var key2 = bundle.factory().generateSecret(bundle.spec()).getEncoded();
        // ------------------------------------------------------------------------------------ then
        assertArrayEquals(key1, key2);
    }

    // ---------------------------------------------------------------------------------------------
    @LatestJDK
    @DisplayName("PBEWithHmacSHA256AndAES_256")
    @Test
    void __PBEWithHmacSHA256AndAES_256() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var bundle = PBEWithHmacSHA256AndAES_256();
        // ------------------------------------------------------------------------------------ when
        final var key1 = bundle.factory().generateSecret(bundle.spec()).getEncoded();
        final var key2 = bundle.factory().generateSecret(bundle.spec()).getEncoded();
        // ------------------------------------------------------------------------------------ then
        assertArrayEquals(key1, key2);
    }

    // ---------------------------------------------------------------------------------------------
    @LatestJDK
    @DisplayName("PBKDF2WithHmacSHA256")
    @Test
    void __PBKDF2WithHmacSHA256() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var bundle = PBKDF2WithHmacSHA256(256);
        // ------------------------------------------------------------------------------------ when
        final var key1 = bundle.factory().generateSecret(bundle.spec()).getEncoded();
        final var key2 = bundle.factory().generateSecret(bundle.spec()).getEncoded();
        // ------------------------------------------------------------------------------------ then
        assertArrayEquals(key1, key2);
    }
}
