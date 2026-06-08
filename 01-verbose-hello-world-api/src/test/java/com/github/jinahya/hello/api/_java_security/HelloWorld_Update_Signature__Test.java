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
import lombok.*;
import lombok.extern.slf4j.*;
import org.jspecify.annotations.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.io.*;
import java.security.*;
import java.security.spec.*;
import java.util.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static com.github.jinahya.hello.miscellaneous._Java_Security_Signature_TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

// https://docs.oracle.com/en/java/javase/25/security/oracle-providers.html

/**
 * A class for exploring
 * {@link com.github.jinahya.hello.api.HelloWorld#update(Signature) update(signature)} method with
 * real {@link Signature} algorithms provided by JDK.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://docs.oracle.com/en/java/javase/25/security/oracle-providers.html">Oracle
 * Providers Documentation</a>
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/specs/security/standard-names.html#signature-algorithms">Signature
 * Algorithms</a>
 */
@DisplayName("update(signature)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Update_Signature__Test extends HelloWorld__Test {

    @TempDir
    private static File tempDir;

    /**
     * 서명 바이트 한 번 분량을 짧게 요약해서 표준 출력에 한 줄로 찍는다. 키쌍·서명 파라미터, 반복 횟수, 서명 길이, 그리고 Base64 인코딩 결과의 앞·뒤 12자를
     * 보여 준다.
     *
     * @param keyPairParameter   키쌍 생성 파라미터. (키 길이 또는 곡선 이름 등.)
     * @param signatureParameter 서명 파라미터. ({@link PSSParameterSpec} 등. {@code null} 이면 빈 문자열로
     *                           표시한다.)
     * @param iteration          같은 키쌍으로 반복 서명할 때의 반복 번호.
     * @param signature          {@link Signature#sign()} 가 돌려준 서명 바이트.
     */
    private static void printf(final Object keyPairParameter,
                               final @Nullable Object signatureParameter,
                               final int iteration,
                               final byte[] signature) {
        final var encoded = Base64.getEncoder().encodeToString(signature);
        System.out.printf("%10s %20s #%d (%4d) %s...%s%n", keyPairParameter,
                          Optional.ofNullable(signatureParameter).orElse(""),
                          iteration, signature.length,
                          encoded.substring(0, 12),
                          encoded.substring(encoded.length() - 12));
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * 각 테스트 직전에 {@link #service()} 의
     * {@link com.github.jinahya.hello.api.HelloWorld#update(Signature) update(signature)} 호출이 실제
     * {@code "hello, world"} 12바이트로 서명을 갱신하도록 스텁한다. 통합 테스트에서는 mock 동작이 아닌 진짜 12바이트가 서명·검증에 흘러 들어가야
     * 검증이 의미를 가진다.
     */
    @BeforeEach
    void __() throws SignatureException {
        update_signature_updates_hello_world_bytes(service());
    }

    // ---------------------------------------------------------------------------------------------
    @LatestLTS
    @LatestJDK
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
    @LatestLTS
    @LatestJDK
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
    @LatestLTS
    @LatestJDK
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
    @LatestLTS
    @LatestJDK
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
    @LatestLTS
    @LatestJDK
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
    @LatestLTS
    @LatestJDK
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
    @LatestLTS
    @LatestJDK
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
    @LatestLTS
    @LatestJDK
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
    @LatestLTS
    @LatestJDK
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
