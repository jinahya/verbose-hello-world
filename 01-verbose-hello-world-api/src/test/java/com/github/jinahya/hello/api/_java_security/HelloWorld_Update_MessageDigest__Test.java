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
import com.github.jinahya.hello.miscellaneous.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.apache.commons.io.*;
import org.bouncycastle.crypto.digests.*;
import org.bouncycastle.jce.provider.*;
import org.jspecify.annotations.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.io.*;
import java.nio.charset.*;
import java.security.*;
import java.util.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static com.github.jinahya.hello.miscellaneous._Java_Security_MessageDigest_TestConstants.*;
import static com.github.jinahya.hello.miscellaneous._Java_Security_MessageDigest_TestUtils.*;
import static com.github.jinahya.hello.miscellaneous._Java_Security_Security_TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * A class for exploring
 * {@link com.github.jinahya.hello.api.HelloWorld#update(MessageDigest) update(digest)} method with
 * real {@link MessageDigest} implementations from JDK and BouncyCastle providers.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/security/MessageDigest.html">java.security.MessageDigest</a>
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/specs/security/standard-names.html#messagedigest-algorithms">MessageDigest
 * Algorithms</a>
 */
@DisplayName("update(digest)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Update_MessageDigest__Test extends HelloWorld__Test {

    @TempDir
    private static File tempDir;

    /**
     * 파라미터화 테스트에 넘길 {@link MessageDigest} 알고리즘 이름 목록을 돌려준다.
     *
     * @return {@link _Java_Security_TestUtils#MESSAGE_DIGEST_ALGORITHMS} 와 같은 목록.
     */
    static List<String> algorithms() {
        return _Java_Security_TestUtils.MESSAGE_DIGEST_ALGORITHMS;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * 각 테스트 직전에 {@link #service()} 의 {@code set(byte[])} 가 실제 {@code "hello, world"} 바이트로 배열을 채우도록
     * 스텁한다. 통합 테스트에서는 mock 동작이 아닌 진짜 12바이트가 다이제스트에 흘러 들어가야 의미 있는 해시 값이 나오기 때문이다.
     */
    @BeforeEach
    void __stubService() throws IOException {
        write_outputstream_writes_hello_world_bytes(service());
        set_array_sets_hello_world_bytes(service());
    }

    // ---------------------------------------------------------------------------------------------
    @Test
    void __() {
        _Java_Security_MessageDigest_TestConstants.MESSAGE_DIGEST_ALGORITHMS.forEach(algorithm -> {
            securityProviders(MESSAGE_DIGEST_SERVICE_TYPE).forEach(provider -> {
                try {
                    final var digest = MessageDigest.getInstance(algorithm, provider);
                    service().update(digest);
                    printMessageDigest(digest);
                } catch (final Exception _) {
                }
            });
        });
    }

    /**
     * 지정한 {@code algorithm} (과 선택적으로 {@code provider}) 으로 {@link MessageDigest} 를 받아,
     * {@link com.github.jinahya.hello.api.HelloWorld#update(MessageDigest)
     * service().update(digest)} 로 12바이트를 흘려 넣은 뒤 {@link MessageDigest#digest()} 결과를 16진 문자열로 찍는다.
     *
     * @param algorithm 알고리즘 이름. ({@code "SHA-256"} 등.)
     * @param provider  공급자 이름. {@code null} 이면 기본 공급자({@code SUN}) 를 쓴다.
     */
    void __(final String algorithm, final @Nullable String provider)
            throws GeneralSecurityException {
        final var digest = provider != null
                           ? MessageDigest.getInstance(algorithm, provider)
                           : MessageDigest.getInstance(algorithm);
        final var digested = service().update(digest).digest();
        printMessageDigest(digest, digested);
//        final var encoded = Base64.getEncoder().encodeToString(digested);
//        System.out.printf("%12s %10s %d %s%n", algorithm, digest.getProvider().getName(),
//                          digested.length << 3, encoded);
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("should update a <real digest> without specifying provider")
    @MethodSource({"algorithms"})
    @ParameterizedTest
    void __(final String algorithm) throws GeneralSecurityException {
        __(algorithm, null);
    }

    /**
     * Verifies that the
     * {@link com.github.jinahya.hello.api.HelloWorld#update(MessageDigest) update(digest)} method
     * updates a real {@link MessageDigest} obtained from the {@code SUN} provider for the given
     * algorithm.
     *
     * @param algorithm an algorithm name supplied by {@link #algorithms()}.
     */
    @DisplayName("should update a <real digest> through the <SUN> provider")
    @MethodSource({"algorithms"})
    @ParameterizedTest
    void __SUN(final String algorithm) throws GeneralSecurityException {
        __(algorithm, "SUN");
    }

    /**
     * Verifies that the
     * {@link com.github.jinahya.hello.api.HelloWorld#update(MessageDigest) update(digest)} method
     * updates a real {@link MessageDigest} obtained from the {@code BouncyCastle} provider for the
     * given algorithm.
     *
     * @param algorithm an algorithm name supplied by {@link #algorithms()}.
     */
    @DisplayName("should update a <real digest> through the <BouncyCastle> provider")
    @MethodSource({"algorithms"})
    @ParameterizedTest
    void __BC(final String algorithm) throws GeneralSecurityException {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
        __(algorithm, BouncyCastleProvider.PROVIDER_NAME);
    }

    @ValueSource(strings = {
            "SHA3-224",
            "SHA3-256",
            "SHA3-384",
            "SHA3-512"
    })
    @ParameterizedTest
    void __SHA3(final String algorithm) throws GeneralSecurityException {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
        __(algorithm, BouncyCastleProvider.PROVIDER_NAME);
    }

    @Nested
    class SHAKE_Test {

        @ValueSource(strings = {
                "SHAKE128-256",
                "SHAKE256-512"
        })
        @ParameterizedTest
        void __SHAKE(final String algorithm) throws GeneralSecurityException {
            if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
                Security.addProvider(new BouncyCastleProvider());
            }
            __(algorithm, BouncyCastleProvider.PROVIDER_NAME);
        }

        @ValueSource(ints = {128, 256})
        @ParameterizedTest
        void __SHAKE_prefix(final int bitStrength) throws GeneralSecurityException {
            if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
                Security.addProvider(new BouncyCastleProvider());
            }
            // --------------------------------------------------- long output via JCA MessageDigest
            final var fixedName = "SHAKE" + bitStrength + "-" + (bitStrength << 1);
            final var longBytes = service().update(MessageDigest.getInstance(
                    fixedName, BouncyCastleProvider.PROVIDER_NAME)).digest();
            // --------------------------------------------------- short output via BC low-level XOF
            final var array = new byte[HelloWorld.BYTES];
            service().set(array);
            final var xof = new SHAKEDigest(bitStrength);
            xof.update(array, 0, array.length);
            final var shortLen = longBytes.length >> 1;
            final var shortBytes = new byte[shortLen];
            xof.doOutput(shortBytes, 0, shortLen);
            // -------------------------------------------------------------------------------- then
            assertArrayEquals(shortBytes, Arrays.copyOf(longBytes, shortLen));
            final var shortName = "SHAKE" + bitStrength + "-" + bitStrength;
            System.out.printf("%12s %10s %d %s%n", shortName, "lw", shortLen << 3,
                              Base64.getEncoder().encodeToString(shortBytes));
            System.out.printf("%12s %10s %d %s%n", fixedName,
                              BouncyCastleProvider.PROVIDER_NAME, longBytes.length << 3,
                              Base64.getEncoder().encodeToString(longBytes));
        }
    }

    /**
     * Verifies that {@code SHA-256} exhibits the avalanche effect by hashing two inputs that differ
     * in a single character and printing the number of flipped bits.
     */
    @DisplayName("should demonstrate the <avalanche effect> of <SHA-256>")
    @Test
    void avalanche_effect__() throws NoSuchAlgorithmException {
        final var a = "hello, world".getBytes(StandardCharsets.US_ASCII);
        final var b = "hello, worle".getBytes(StandardCharsets.US_ASCII);
        final var ha = MessageDigest.getInstance("SHA-256").digest(a);
        final var hb = MessageDigest.getInstance("SHA-256").digest(b);
        int diff = 0;
        for (var i = 0; i < ha.length; i++) {
            diff += Integer.bitCount((ha[i] ^ hb[i]) & 0xFF);
        }
        System.out.printf("hello, world: %s%n", Base64.getEncoder().encodeToString(ha));
        System.out.printf("hello, worle: %s%n", Base64.getEncoder().encodeToString(hb));
        System.out.printf("flipped bits: %d / %d (%.1f%%)%n",
                          diff, ha.length * 8, diff * 100.0 / (ha.length * 8));
    }

    /**
     * Verifies that hashing common weak passwords with {@code SHA-1} produces deterministic digests
     * that can be reversed by a rainbow-table lookup.
     *
     * @param password a weak password supplied by {@link ValueSource}.
     */
    @DisplayName("should demonstrate a <rainbow attack> on <SHA-1>")
    @ValueSource(strings = {
            "iloveyou",
            "iloveyou!",
            "test1234",
            "test1234!"
    })
    @ParameterizedTest
    void rainbow_attack__(final String password) throws NoSuchAlgorithmException {
        final var digested = MessageDigest.getInstance("SHA-1")
                .digest(password.getBytes(StandardCharsets.US_ASCII));
        System.out.printf("%10s %s%n", password, Base64.getEncoder().encodeToString(digested));
    }

    @Nested
    class DigestOutputStream_Test {

        static List<String> algorithms() {
            return HelloWorld_Update_MessageDigest__Test.algorithms();
        }

        @MethodSource({"algorithms"})
        @ParameterizedTest
        void __(final String algorithm) throws IOException, NoSuchAlgorithmException {
            try (var dos = new DigestOutputStream(OutputStream.nullOutputStream(),
                                                  MessageDigest.getInstance(algorithm))) {
                service().write(dos).flush();
                try (var dis = new DigestInputStream(hello_world_inputstream(),
                                                     MessageDigest.getInstance(algorithm))) {
                    IOUtils.consume(dis);
                    assertArrayEquals(dos.getMessageDigest().digest(),
                                      dis.getMessageDigest().digest());
                }
            }
        }
    }
}
