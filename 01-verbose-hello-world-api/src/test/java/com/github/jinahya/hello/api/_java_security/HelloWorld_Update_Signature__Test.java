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
import org.opentest4j.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.file.*;
import java.security.*;
import java.security.spec.*;
import java.util.*;
import java.util.stream.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

// https://docs.oracle.com/en/java/javase/25/security/oracle-providers.html

/**
 * {@link com.github.jinahya.hello.api.HelloWorld#update(Signature) update(signature)} 메서드를 실제 JDK 가
 * 제공하는 {@link Signature} 알고리즘들과 함께 돌려 보는 통합 테스트 클래스. 같은 패키지의 단위
 * 테스트({@link HelloWorld_Update_Signature_Test})와 달리, 여기서는 mock 대신
 * {@link Signature#getInstance(String) Signature.getInstance(...)} 로 받아온 진짜 구현으로 키쌍 생성·서명·검증의 한 묶음을
 * 끝까지 돌린다. 알고리즘별 동작은 각 {@link Nested} 클래스에서 따로 다룬다.
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

    /**
     * {@code RSASSA-PSS} 알고리즘으로 서명·검증의 한 묶음을 끝까지 돌려 보는 {@link Nested} 테스트. RSA 키 길이와 MGF1
     * 다이제스트(SHA-256 / SHA-384)를 조합해서 여러 파라미터로 같은 흐름을 반복한다.
     */
    @DisplayName("RSASSA-PSS")
    @Nested
    class RSASSA_PSS_Test {

        private static final String KEY_PAIR_ALGORITHM = "RSASSA-PSS";

        private static final String MGF1_ALGORITHM = "MGF1";

        private static final String SIGNATURE_ALGORITHM = "RSASSA-PSS";

        /**
         * {@link #__(int, MGF1ParameterSpec, int)} 에 넘길 RSA 키 길이와 MGF1 파라미터의 조합을 만들어 준다.
         *
         * @return 키 길이({@code 2048 / 3072 / 4096}) × MGF1({@code SHA-256 / SHA-384}) 의 모든 조합을 담은
         * {@link Arguments} 스트림.
         */
        private static Stream<Arguments> pssTestProvider() {
            return Stream.of(2048, 3072, 4096)
                    .flatMap(k -> Stream.of(
                            Arguments.of(k, MGF1ParameterSpec.SHA256, 256 >> 3),
                            Arguments.of(k, MGF1ParameterSpec.SHA384, 384 >> 3)
                    ));
        }

        /**
         * 주어진 RSA 키 길이와 MGF1 파라미터로 키쌍을 생성하고,
         * {@link com.github.jinahya.hello.api.HelloWorld#update(Signature)
         * service().update(instance)} 로 12바이트를 흘려 넣어 서명한 뒤, 공개 키로 다시 검증해서 같은 결과가 나오는지 두 번 확인한다.
         *
         * @param keysize 키 길이(비트). ({@code 2048 / 3072 / 4096}.)
         * @param mgfSpec MGF1 의 다이제스트 파라미터.
         * @param saltLen 솔트 길이(바이트).
         */
        @DisplayName(
                "should sign and verify the hello-world bytes with a <real RSASSA-PSS> signature")
        @ParameterizedTest(name = "{0}-bit RSA with {1}")
        @MethodSource({"pssTestProvider"})
        void __(int keysize, final MGF1ParameterSpec mgfSpec, final int saltLen) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var keyPair = _Java_Security_TestUtils.generateKeyPair(
                    KEY_PAIR_ALGORITHM,
                    keysize
            );
            final var pssSpec = new PSSParameterSpec(
                    mgfSpec.getDigestAlgorithm(),     // <mdName>
                    MGF1_ALGORITHM,                   // <mgfName>
                    mgfSpec,                          // <mgfSpec>
                    saltLen,                          // <saltLen>
                    PSSParameterSpec.TRAILER_FIELD_BC // <trailerField>
            );
            final var instance = Signature.getInstance(SIGNATURE_ALGORITHM);
            for (int i = 0; i < 2; i++) {
                // ----------------------------------------------------------- sign with private key
                try {
                    instance.initSign(keyPair.getPrivate());
                } catch (final InvalidKeyException ike) {
                    throw new TestAbortedException(ike.getMessage(), ike);
                }
                instance.setParameter(pssSpec);
                service().update(instance);
                final var signature = instance.sign();
                printf(keysize, pssSpec, i, signature);
                // ---------------------------------------------------------- verify with public key
                instance.initVerify(keyPair.getPublic());
                instance.setParameter(pssSpec);
                service().update(instance);
                final var verified = instance.verify(signature);
                // ---------------------------------------------------------------------------- then
                assertTrue(verified);
            }
        }

        /**
         * 12바이트짜리 데모가 아닌 임시 파일을 대상으로 {@code RSASSA-PSS} 서명·검증 한 묶음을 끝까지 돌려 본다. 서명할 때는
         * {@link FileInputStream} 으로 파일을 읽어 {@link Signature#update(byte[], int, int)} 에 흘려 넣고, 검증할
         * 때는 같은 파일을 {@link FileChannel} 로 읽어 {@link Signature#update(ByteBuffer)} 에 흘려 넣어 두 경로 모두
         * 같은 서명을 만들고 검증함을 확인한다.
         */
        @DisplayName("should sign a <file> with <FileInputStream> and verify it with <FileChannel>")
        @Test
        void __file() throws Exception {
            final var file = writeSome(File.createTempFile("tmp", null, tempDir));
            // -------------------------------------------------------------------------------------
            final var mgfSpec = MGF1ParameterSpec.SHA384;
            final var pssSpec = new PSSParameterSpec(
                    mgfSpec.getDigestAlgorithm(),
                    MGF1_ALGORITHM,
                    mgfSpec,
                    384 >> 3,
                    PSSParameterSpec.TRAILER_FIELD_BC
            );
            final PublicKey publicKey;
            final byte[] signature;
            {
                final var keyPair = _Java_Security_TestUtils.generateKeyPair(
                        KEY_PAIR_ALGORITHM,
                        4096
                );
                publicKey = keyPair.getPublic();
                final var instance = Signature.getInstance(SIGNATURE_ALGORITHM);
                instance.initSign(keyPair.getPrivate());
                instance.setParameter(pssSpec);
                try (var stream = new FileInputStream(file)) {
                    final var b = new byte[128];
                    for (int r; (r = stream.read(b)) != -1; ) {
                        instance.update(b, 0, r);
                    }
                }
                signature = instance.sign();
            }
            {
                final var instance = Signature.getInstance(SIGNATURE_ALGORITHM);
                instance.initVerify(publicKey);
                instance.setParameter(pssSpec);
                try (var channel = FileChannel.open(file.toPath(), StandardOpenOption.READ)) {
                    for (final var b = ByteBuffer.allocate(128); channel.read(b.clear()) != -1; ) {
                        instance.update(b.flip());
                    }
                }
                final var verified = instance.verify(signature);
                assertTrue(verified);
            }
        }
    }

    // https://docs.oracle.com/en/java/javase/25/security/oracle-providers.html

    /**
     * {@code DSA} 키쌍과 {@code SHA1withDSA} 알고리즘으로 서명·검증의 한 묶음을 끝까지 돌려 보는 {@link Nested} 테스트.
     */
    @DisplayName("SHA1withDSA")
    @Nested
    class SHA1WithDSATest {

        private static final String KEY_PAIR_ALGORITHM = "DSA";

        private static final String SIGNATURE_ALGORITHM = "SHA1withDSA";

        /**
         * 주어진 키 길이로 DSA 키쌍을 생성하고,
         * {@link com.github.jinahya.hello.api.HelloWorld#update(Signature)
         * service().update(instance)} 로 12바이트를 흘려 넣어 서명한 뒤, 공개 키로 다시 검증해서 같은 결과가 나오는지 두 번 확인한다.
         *
         * @param keysize DSA 키 길이(비트). ({@code 1024 / 2048}.)
         */
        @DisplayName(
                "should sign and verify the hello-world bytes with a <real SHA1withDSA> signature")
        @ValueSource(ints = {
                1024, 2048
        })
        @ParameterizedTest
        void __(final int keysize) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var keyPair = _Java_Security_TestUtils.generateKeyPair(
                    KEY_PAIR_ALGORITHM,
                    keysize
            );
            final var instance = Signature.getInstance(SIGNATURE_ALGORITHM);
            for (int i = 0; i < 2; i++) {
                // ----------------------------------------------------------- sign with private key
                try {
                    instance.initSign(keyPair.getPrivate());
                } catch (final InvalidKeyException ike) {
                    throw new TestAbortedException(ike.getMessage(), ike);
                }
                service().update(instance);
                final var signature = instance.sign();
                printf(keysize, null, i, signature);
                // ---------------------------------------------------------- verify with public key
                instance.initVerify(keyPair.getPublic());
                service().update(instance);
                final var verified = instance.verify(signature);
                // ---------------------------------------------------------------------------- then
                assertTrue(verified);
            }
        }
    }

    /**
     * {@code DSA} 키쌍과 {@code SHA256withDSA} 알고리즘으로 서명·검증의 한 묶음을 끝까지 돌려 보는 {@link Nested} 테스트.
     */
    @DisplayName("SHA256withDSA")
    @Nested
    class SHA256WithDSATest {

        private static final String KEY_PAIR_ALGORITHM = "DSA";

        private static final String SIGNATURE_ALGORITHM = "SHA256withDSA";

        /**
         * 주어진 키 길이로 DSA 키쌍을 생성하고,
         * {@link com.github.jinahya.hello.api.HelloWorld#update(Signature)
         * service().update(instance)} 로 12바이트를 흘려 넣어 서명한 뒤, 공개 키로 다시 검증해서 같은 결과가 나오는지 두 번 확인한다.
         *
         * @param keysize DSA 키 길이(비트). ({@code 1024 / 2048}.)
         */
        @DisplayName("""
                should sign and verify the hello-world bytes
                with a <real SHA256withDSA> signature""")
        @ValueSource(ints = {
                1024, 2048
        })
        @ParameterizedTest
        void __(final int keysize) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var keyPair = _Java_Security_TestUtils.generateKeyPair(
                    KEY_PAIR_ALGORITHM,
                    keysize
            );
            final var instance = Signature.getInstance(SIGNATURE_ALGORITHM);
            for (int i = 0; i < 2; i++) {
                // ----------------------------------------------------------- sign with private key
                try {
                    instance.initSign(keyPair.getPrivate());
                } catch (final InvalidKeyException ike) {
                    throw new TestAbortedException(ike.getMessage(), ike);
                }
                service().update(instance);
                final var signature = instance.sign();
                printf(keysize, null, i, signature);
                // ---------------------------------------------------------- verify with public key
                instance.initVerify(keyPair.getPublic());
                service().update(instance);
                final var verified = instance.verify(signature);
                // ---------------------------------------------------------------------------- then
                assertTrue(verified);
            }
        }
    }

    /**
     * {@code EC} 키쌍({@code secp256r1} 곡선)과 {@code SHA256withECDSA} 알고리즘으로 서명·검증의 한 묶음을 끝까지 돌려 보는
     * {@link Nested} 테스트.
     */
    @DisplayName("SHA256withECDSA")
    @Nested
    class SHA256WithECDSA_Test {

        private static final String KEY_PAIR_ALGORITHM = "EC";

        private static final String SIGNATURE_ALGORITHM = "SHA256withECDSA";

        private static final String CURVE_NAME = "secp256r1"; // Standard for P-256

        /**
         * {@link ECGenParameterSpec} 로 {@code secp256r1} EC 키쌍을 생성하고,
         * {@link com.github.jinahya.hello.api.HelloWorld#update(Signature)
         * service().update(instance)} 로 12바이트를 흘려 넣어 서명한 뒤, 공개 키로 다시 검증해서 같은 결과가 나오는지 두 번 확인한다.
         */
        @DisplayName("""
                should sign and verify the hello-world bytes
                with a <real SHA256withECDSA> signature on <secp256r1>""")
        @Test
        void __() throws Exception {
            // ------------------------------------------------------------------------------- given
            final var keyPairSpec = new ECGenParameterSpec(CURVE_NAME);
            final var keyPair = _Java_Security_TestUtils.generateKeyPair(
                    KEY_PAIR_ALGORITHM,
                    keyPairSpec
            );
            final var instance = Signature.getInstance(SIGNATURE_ALGORITHM);
            for (int i = 0; i < 2; i++) {
                // ----------------------------------------------------------- sign with private key
                instance.initSign(keyPair.getPrivate());
                service().update(instance);
                final var signature = instance.sign();
                printf(CURVE_NAME, null, i, signature);
                // ---------------------------------------------------------- verify with public key
                instance.initVerify(keyPair.getPublic());
                service().update(instance);
                final var verified = instance.verify(signature);
                // ---------------------------------------------------------------------------- then
                assertTrue(verified);
            }
        }
    }

    /**
     * {@code EC} 키쌍({@code secp384r1} 곡선)과 {@code SHA384withECDSA} 알고리즘으로 서명·검증의 한 묶음을 끝까지 돌려 보는
     * {@link Nested} 테스트.
     */
    @DisplayName("SHA384withECDSA")
    @Nested
    class SHA384withECDSA_Test {

        private static final String KEY_PAIR_ALGORITHM = "EC";

        private static final String SIGNATURE_ALGORITHM = "SHA384withECDSA";

        private static final String CURVE_NAME = "secp384r1";

        /**
         * {@link ECGenParameterSpec} 로 {@code secp384r1} EC 키쌍을 생성하고,
         * {@link com.github.jinahya.hello.api.HelloWorld#update(Signature)
         * service().update(instance)} 로 12바이트를 흘려 넣어 서명한 뒤, 공개 키로 다시 검증해서 같은 결과가 나오는지 두 번 확인한다.
         */
        @DisplayName("""
                should sign and verify the hello-world bytes
                with a <real SHA384withECDSA> signature on <secp384r1>""")
        @Test
        void __() throws Exception {
            // ------------------------------------------------------------------------------- given
            final var keyPairSpec = new ECGenParameterSpec(CURVE_NAME);
            final var keyPair = _Java_Security_TestUtils.generateKeyPair(
                    KEY_PAIR_ALGORITHM,
                    keyPairSpec
            );
            final var instance = Signature.getInstance(SIGNATURE_ALGORITHM);
            for (int i = 0; i < 2; i++) {
                // --------------------------------------------------------------- sign with private key
                instance.initSign(keyPair.getPrivate());
                service().update(instance);
                final var signature = instance.sign();
                printf(CURVE_NAME, null, i, signature);
                // -------------------------------------------------------------- verify with public key
                instance.initVerify(keyPair.getPublic());
                service().update(instance);
                final var verified = instance.verify(signature);
                // -------------------------------------------------------------------------------- then
                assertTrue(verified);
            }
        }

        /**
         * 12바이트짜리 데모가 아닌 임시 파일을 대상으로 {@code SHA384withECDSA} 서명·검증 한 묶음을 끝까지 돌려 본다. 서명할 때는
         * {@link FileChannel} 로 파일을 읽어 {@link Signature#update(ByteBuffer)} 에 흘려 넣고, 검증할 때는 같은 파일을
         * {@link FileInputStream} 으로 읽어 {@link Signature#update(byte[], int, int)} 에 흘려 넣어 두 경로 모두
         * 같은 서명을 만들고 검증함을 확인한다.
         */
        @DisplayName("should sign a <file> with <FileChannel> and verify it with <FileInputStream>")
        @Test
        void __file() throws Exception {
            final var file = writeSome(
                    File.createTempFile("tmp", null, tempDir)
            );
            // -------------------------------------------------------------------------------------
            final var keyPairSpec = new ECGenParameterSpec(CURVE_NAME);
            final PublicKey publicKey;
            final byte[] signature;
            {
                final var keyPair = _Java_Security_TestUtils.generateKeyPair(
                        KEY_PAIR_ALGORITHM,
                        keyPairSpec
                );
                publicKey = keyPair.getPublic();
                final var instance = Signature.getInstance(SIGNATURE_ALGORITHM);
                instance.initSign(keyPair.getPrivate());
                try (var channel = FileChannel.open(file.toPath(), StandardOpenOption.READ)) {
                    for (final var b = ByteBuffer.allocate(128); channel.read(b.clear()) != -1; ) {
                        instance.update(b.flip());
                    }
                }
                signature = instance.sign();
            }
            {
                final var instance = Signature.getInstance(SIGNATURE_ALGORITHM);
                instance.initVerify(publicKey);
                try (var stream = new FileInputStream(file)) {
                    final var b = new byte[128];
                    for (int r; (r = stream.read(b)) != -1; ) {
                        instance.update(b, 0, r);
                    }
                }
                final var verified = instance.verify(signature);
                assertTrue(verified);
            }
        }
    }

    /**
     * {@code RSA} 키쌍과 {@code SHA1withRSA} 알고리즘으로 서명·검증의 한 묶음을 끝까지 돌려 보는 {@link Nested} 테스트.
     */
    @DisplayName("SHA1withRSA")
    @Nested
    class SHA1withRSA_Test {

        private static final String KEY_PAIR_ALGORITHM = "RSA";

        private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";

        /**
         * 주어진 키 길이로 RSA 키쌍을 생성하고,
         * {@link com.github.jinahya.hello.api.HelloWorld#update(Signature)
         * service().update(instance)} 로 12바이트를 흘려 넣어 {@code SHA1withRSA} 서명을 만든 뒤, 공개 키로 다시 검증해서 같은
         * 결과가 나오는지 두 번 확인한다.
         *
         * @param keysize RSA 키 길이(비트). ({@code 1024 / 2048 / 3072 / 4096}.)
         */
        @DisplayName(
                "should sign and verify the hello-world bytes with a <real SHA1withRSA> signature")
        @ValueSource(ints = {
                1024, 2048, 3072, 4096
        })
        @ParameterizedTest
        void __(final int keysize) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var keyPair = _Java_Security_TestUtils.generateKeyPair(
                    KEY_PAIR_ALGORITHM,
                    keysize
            );
            final var instance = Signature.getInstance(SIGNATURE_ALGORITHM);
            for (int i = 0; i < 2; i++) {
                // ----------------------------------------------------------- sign with private key
                try {
                    instance.initSign(keyPair.getPrivate());
                } catch (final InvalidKeyException ike) {
                    throw new TestAbortedException(ike.getMessage(), ike);
                }
                service().update(instance);
                final var signature = instance.sign();
                printf(keysize, null, i, signature);
                // ---------------------------------------------------------- verify with public key
                instance.initVerify(keyPair.getPublic());
                service().update(instance);
                final var verified = instance.verify(signature);
                // ---------------------------------------------------------------------------- then
                assertTrue(verified);
            }
        }
    }

    /**
     * {@code RSA} 키쌍과 {@code SHA256withRSA} 알고리즘으로 서명·검증의 한 묶음을 끝까지 돌려 보는 {@link Nested} 테스트.
     */
    @DisplayName("SHA256withRSA")
    @Nested
    class SHA256withRSA_Test {

        private static final String KEY_PAIR_ALGORITHM = "RSA";

        private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

        /**
         * 주어진 키 길이로 RSA 키쌍을 생성하고,
         * {@link com.github.jinahya.hello.api.HelloWorld#update(Signature)
         * service().update(instance)} 로 12바이트를 흘려 넣어 {@code SHA256withRSA} 서명을 만든 뒤, 공개 키로 다시 검증해서
         * 같은 결과가 나오는지 두 번 확인한다.
         *
         * @param keysize RSA 키 길이(비트). ({@code 1024 / 2048 / 3072 / 4096}.)
         */
        @DisplayName("""
                should sign and verify the hello-world bytes
                with a <real SHA256withRSA> signature""")
        @ValueSource(ints = {
                1024, 2048, 3072, 4096
        })
        @ParameterizedTest
        void __(final int keysize) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var keyPair = _Java_Security_TestUtils.generateKeyPair(
                    KEY_PAIR_ALGORITHM,
                    keysize
            );
            final var instance = Signature.getInstance(SIGNATURE_ALGORITHM);
            for (int i = 0; i < 2; i++) {
                // ----------------------------------------------------------- sign with private key
                try {
                    instance.initSign(keyPair.getPrivate());
                } catch (final InvalidKeyException ike) {
                    throw new TestAbortedException(ike.getMessage(), ike);
                }
                service().update(instance);
                final var signature = instance.sign();
                printf(keysize, null, i, signature);
                // ---------------------------------------------------------- verify with public key
                instance.initVerify(keyPair.getPublic());
                service().update(instance);
                final var verified = instance.verify(signature);
                // ---------------------------------------------------------------------------- then
                assertTrue(verified);
            }
        }
    }

    /**
     * {@code RSA} 키쌍과 {@code SHA384withRSA} 알고리즘으로 서명·검증의 한 묶음을 끝까지 돌려 보는 {@link Nested} 테스트.
     */
    @DisplayName("SHA384withRSA")
    @Nested
    class SHA384withRSA_Test {

        private static final String KEY_PAIR_ALGORITHM = "RSA";

        private static final String SIGNATURE_ALGORITHM = "SHA384withRSA";

        /**
         * 주어진 키 길이로 RSA 키쌍을 생성하고,
         * {@link com.github.jinahya.hello.api.HelloWorld#update(Signature)
         * service().update(instance)} 로 12바이트를 흘려 넣어 {@code SHA384withRSA} 서명을 만든 뒤, 공개 키로 다시 검증해서
         * 같은 결과가 나오는지 두 번 확인한다.
         *
         * @param keysize RSA 키 길이(비트). ({@code 1024 / 2048 / 3072 / 4096}.)
         */
        @DisplayName("""
                should sign and verify the hello-world bytes
                with a <real SHA384withRSA> signature""")
        @ValueSource(ints = {
                1024, 2048, 3072, 4096
        })
        @ParameterizedTest
        void __(final int keysize) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var keyPair = _Java_Security_TestUtils.generateKeyPair(
                    KEY_PAIR_ALGORITHM,
                    keysize
            );
            // -------------------------------------------------------------------------------- when
            final var instance = Signature.getInstance(SIGNATURE_ALGORITHM);
            for (int i = 0; i < 2; i++) {
                // ----------------------------------------------------------- sign with private key
                try {
                    instance.initSign(keyPair.getPrivate());
                } catch (final InvalidKeyException ike) {
                    throw new TestAbortedException(ike.getMessage(), ike);
                }
                service().update(instance);
                final var signature = instance.sign();
                printf(keysize, null, i, signature);
                // ---------------------------------------------------------- verify with public key
                instance.initVerify(keyPair.getPublic());
                service().update(instance);
                final var verified = instance.verify(signature);
                // ---------------------------------------------------------------------------- then
                assertTrue(verified);
            }
        }
    }
}
