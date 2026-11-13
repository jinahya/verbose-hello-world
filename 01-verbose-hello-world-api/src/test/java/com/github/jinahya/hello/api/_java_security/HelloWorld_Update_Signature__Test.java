package com.github.jinahya.hello.api._java_security;

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
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.io.*;
import java.security.*;
import java.security.spec.*;
import java.util.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static com.github.jinahya.hello.miscellaneous._java_security._Java_Security_Signature_TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

// https://docs.oracle.com/en/java/javase/27/security/oracle-providers.html

/**
 * A class for exploring
 * {@link com.github.jinahya.hello.api.HelloWorld#update(Signature) update(signature)} method with
 * real {@link Signature} algorithms provided by JDK.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://docs.oracle.com/en/java/javase/27/security/oracle-providers.html">Oracle
 * Providers Documentation</a>
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/27/docs/specs/security/standard-names.html#signature-algorithms">Signature
 * Algorithms</a>
 */
@_HideNameFromPublishing
@DisplayName("update(signature)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Update_Signature__Test extends HelloWorld__Test {

    @TempDir
    private static File tempDir;

    /**
     * Prints a line of the signing result: the signature algorithm, the key-pair generation
     * parameter (key length or curve name), the length of the signature, and the head and tail of
     * its Base64 encoding.
     *
     * @param algorithm        the signature algorithm.
     * @param keyPairParameter the key-pair generation parameter (key length, curve name, etc.).
     * @param signature        the signature bytes returned by {@link Signature#sign()}.
     */
    private static void printf(final String algorithm, final Object keyPairParameter,
                               final byte[] signature) {
        final var encoded = Base64.getEncoder().encodeToString(signature);
        System.out.printf("%24s | %9s | %3d | %s...%s%n", algorithm, keyPairParameter,
                          signature.length, encoded.substring(0, 4),
                          encoded.substring(encoded.length() - 4));
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Stubs {@link #service()} so that
     * {@link com.github.jinahya.hello.api.HelloWorld#update(Signature) update(signature)} updates
     * the given signature with the real {@code "hello, world"} 12 bytes before each test. The
     * integration tests need the real 12 bytes flowing through signing and verification — not the
     * mock's default behavior — for verification to be meaningful.
     */
    @BeforeEach
    void __() throws SignatureException {
        update_signature_updates_hello_world_bytes(service());
    }

    // ---------------------------------------------------------------------------------------------
    @_LatestLTS
    @_LatestJDK
    @DisplayName("SHA1withDSA")
    @ValueSource(ints = {1024})
    @ParameterizedTest
    void __SHA1withDSA(final int keysize) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var signing = SHA1withDSA(keysize);
        final var signature = signing.signature();
        // ------------------------------------------------------------------------------------ sign
        signature.initSign(signing.keyPair().getPrivate());
        if (signing.params() != null) {
            signature.setParameter(signing.params());
        }
        service().update(signature);
        final var signed = signature.sign();
        printf(signature.getAlgorithm(), keysize, signed);
        // ---------------------------------------------------------------------------------- verify
        signature.initVerify(signing.keyPair().getPublic());
        if (signing.params() != null) {
            signature.setParameter(signing.params());
        }
        service().update(signature);
        final var verified = signature.verify(signed);
        // ------------------------------------------------------------------------------------ then
        assertTrue(verified);
    }

    // ---------------------------------------------------------------------------------------------
    @_LatestLTS
    @_LatestJDK
    @DisplayName("SHA256withDSA")
    @ValueSource(ints = {2048})
    @ParameterizedTest
    void __SHA256withDSA(final int keysize) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var signing = SHA256withDSA(keysize);
        final var signature = signing.signature();
        // ------------------------------------------------------------------------------------ sign
        signature.initSign(signing.keyPair().getPrivate());
        if (signing.params() != null) {
            signature.setParameter(signing.params());
        }
        service().update(signature);
        final var signed = signature.sign();
        printf(signature.getAlgorithm(), keysize, signed);
        // ---------------------------------------------------------------------------------- verify
        signature.initVerify(signing.keyPair().getPublic());
        if (signing.params() != null) {
            signature.setParameter(signing.params());
        }
        service().update(signature);
        final var verified = signature.verify(signed);
        // ------------------------------------------------------------------------------------ then
        assertTrue(verified);
    }

    // ---------------------------------------------------------------------------------------------
    @_LatestLTS
    @_LatestJDK
    @DisplayName("SHA256withECDSA")
    @Test
    void __SHA256withECDSA() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var signing = SHA256withECDSA();
        final var signature = signing.signature();
        // ------------------------------------------------------------------------------------ sign
        signature.initSign(signing.keyPair().getPrivate());
        if (signing.params() != null) {
            signature.setParameter(signing.params());
        }
        service().update(signature);
        final var signed = signature.sign();
        printf(signature.getAlgorithm(), "secp256r1", signed);
        // ---------------------------------------------------------------------------------- verify
        signature.initVerify(signing.keyPair().getPublic());
        if (signing.params() != null) {
            signature.setParameter(signing.params());
        }
        service().update(signature);
        final var verified = signature.verify(signed);
        // ------------------------------------------------------------------------------------ then
        assertTrue(verified);
    }

    // ---------------------------------------------------------------------------------------------
    @_LatestLTS
    @_LatestJDK
    @DisplayName("SHA384withECDSA")
    @Test
    void __SHA384withECDSA() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var signing = SHA384withECDSA();
        final var signature = signing.signature();
        // ------------------------------------------------------------------------------------ sign
        signature.initSign(signing.keyPair().getPrivate());
        if (signing.params() != null) {
            signature.setParameter(signing.params());
        }
        service().update(signature);
        final var signed = signature.sign();
        printf(signature.getAlgorithm(), "secp384r1", signed);
        // ---------------------------------------------------------------------------------- verify
        signature.initVerify(signing.keyPair().getPublic());
        if (signing.params() != null) {
            signature.setParameter(signing.params());
        }
        service().update(signature);
        final var verified = signature.verify(signed);
        // ------------------------------------------------------------------------------------ then
        assertTrue(verified);
    }

    // ---------------------------------------------------------------------------------------------
    @_LatestLTS
    @_LatestJDK
    @DisplayName("SHA1withRSA")
    @ValueSource(ints = {2048})
    @ParameterizedTest
    void __SHA1withRSA(final int keysize) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var signing = SHA1withRSA(keysize);
        final var signature = signing.signature();
        // ------------------------------------------------------------------------------------ sign
        signature.initSign(signing.keyPair().getPrivate());
        if (signing.params() != null) {
            signature.setParameter(signing.params());
        }
        service().update(signature);
        final var signed = signature.sign();
        printf(signature.getAlgorithm(), keysize, signed);
        // ---------------------------------------------------------------------------------- verify
        signature.initVerify(signing.keyPair().getPublic());
        if (signing.params() != null) {
            signature.setParameter(signing.params());
        }
        service().update(signature);
        final var verified = signature.verify(signed);
        // ------------------------------------------------------------------------------------ then
        assertTrue(verified);
    }

    // ---------------------------------------------------------------------------------------------
    @_LatestLTS
    @_LatestJDK
    @DisplayName("SHA256withRSA")
    @ValueSource(ints = {2048})
    @ParameterizedTest
    void __SHA256withRSA(final int keysize) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var signing = SHA256withRSA(keysize);
        final var signature = signing.signature();
        // ------------------------------------------------------------------------------------ sign
        signature.initSign(signing.keyPair().getPrivate());
        if (signing.params() != null) {
            signature.setParameter(signing.params());
        }
        service().update(signature);
        final var signed = signature.sign();
        printf(signature.getAlgorithm(), keysize, signed);
        // ---------------------------------------------------------------------------------- verify
        signature.initVerify(signing.keyPair().getPublic());
        if (signing.params() != null) {
            signature.setParameter(signing.params());
        }
        service().update(signature);
        final var verified = signature.verify(signed);
        // ------------------------------------------------------------------------------------ then
        assertTrue(verified);
    }

    // ---------------------------------------------------------------------------------------------
    @_LatestLTS
    @_LatestJDK
    @DisplayName("SHA384withRSA")
    @ValueSource(ints = {2048})
    @ParameterizedTest
    void __SHA384withRSA(final int keysize) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var signing = SHA384withRSA(keysize);
        final var signature = signing.signature();
        // ------------------------------------------------------------------------------------ sign
        signature.initSign(signing.keyPair().getPrivate());
        if (signing.params() != null) {
            signature.setParameter(signing.params());
        }
        service().update(signature);
        final var signed = signature.sign();
        printf(signature.getAlgorithm(), keysize, signed);
        // ---------------------------------------------------------------------------------- verify
        signature.initVerify(signing.keyPair().getPublic());
        if (signing.params() != null) {
            signature.setParameter(signing.params());
        }
        service().update(signature);
        final var verified = signature.verify(signed);
        // ------------------------------------------------------------------------------------ then
        assertTrue(verified);
    }

    // ---------------------------------------------------------------------------------------------
    @_LatestLTS
    @_LatestJDK
    @DisplayName("RSASSA-PSS with SHA-256")
    @ValueSource(ints = {2048})
    @ParameterizedTest
    void __RSASSA_PSS_SHA_256(final int keysize) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var signing = RSASSA_PSS_SHA_256(keysize);
        final var signature = signing.signature();
        // ------------------------------------------------------------------------------------ sign
        signature.initSign(signing.keyPair().getPrivate());
        if (signing.params() != null) {
            signature.setParameter(signing.params());
        }
        service().update(signature);
        final var signed = signature.sign();
        printf("RSASSA-PSS (SHA-256)", keysize, signed);
        // ---------------------------------------------------------------------------------- verify
        signature.initVerify(signing.keyPair().getPublic());
        if (signing.params() != null) {
            signature.setParameter(signing.params());
        }
        service().update(signature);
        final var verified = signature.verify(signed);
        // ------------------------------------------------------------------------------------ then
        assertTrue(verified);
    }

    // ---------------------------------------------------------------------------------------------
    @_LatestLTS
    @_LatestJDK
    @DisplayName("RSASSA-PSS with SHA-384")
    @ValueSource(ints = {2048})
    @ParameterizedTest
    void __RSASSA_PSS_SHA_384(final int keysize) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var signing = RSASSA_PSS_SHA_384(keysize);
        final var signature = signing.signature();
        // ------------------------------------------------------------------------------------ sign
        signature.initSign(signing.keyPair().getPrivate());
        if (signing.params() != null) {
            signature.setParameter(signing.params());
        }
        service().update(signature);
        final var signed = signature.sign();
        printf("RSASSA-PSS (SHA-384)", keysize, signed);
        // ---------------------------------------------------------------------------------- verify
        signature.initVerify(signing.keyPair().getPublic());
        if (signing.params() != null) {
            signature.setParameter(signing.params());
        }
        service().update(signature);
        final var verified = signature.verify(signed);
        // ------------------------------------------------------------------------------------ then
        assertTrue(verified);
    }
}
