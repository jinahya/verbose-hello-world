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

/**
 * {@link com.github.jinahya.hello.api.HelloWorld#update(MessageDigest) update(digest)} 메서드를
 * 실제 JDK / BouncyCastle 의 {@link MessageDigest} 구현과 함께 돌려 보는 통합 테스트 클래스. 같은
 * 패키지의 단위 테스트({@link HelloWorld_Update_MessageDigest_Test})와 달리, 여기서는 mock 이 아니라
 * {@link MessageDigest#getInstance(String) MessageDigest.getInstance(...)} 로 받아온 진짜 구현을
 * 사용한다. 추가로 SHA 의 쇄도 효과(avalanche effect)와 약한 알고리즘에 대한 무지개 공격(rainbow
 * attack)을 보여 주는 데모 테스트도 함께 둔다.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/security/MessageDigest.html">java.security.MessageDigest</a>
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/specs/security/standard-names.html#messagedigest-algorithms">MessageDigest
 * Algorithms</a>
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Update_MessageDigest__Test extends HelloWorldTest {

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
     * 각 테스트 직전에 {@link #service()} 의 {@code set(byte[])} 가 실제 {@code "hello, world"} 바이트로
     * 배열을 채우도록 스텁한다. 통합 테스트에서는 mock 동작이 아닌 진짜 12바이트가 다이제스트에 흘러
     * 들어가야 의미 있는 해시 값이 나오기 때문이다.
     */
    @BeforeEach
    void __() {
        HelloWorldTestUtils.set_array_sets_actual_hello_world_bytes(service());
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * 지정한 {@code algorithm} (과 선택적으로 {@code provider}) 으로 {@link MessageDigest} 를 받아,
     * {@link com.github.jinahya.hello.api.HelloWorld#update(MessageDigest) service().update(digest)}
     * 로 12바이트를 흘려 넣은 뒤 {@link MessageDigest#digest()} 결과를 16진 문자열로 찍는다.
     *
     * @param algorithm 알고리즘 이름. ({@code "SHA-256"} 등.)
     * @param provider  공급자 이름. {@code null} 이면 기본 공급자({@code SUN}) 를 쓴다.
     */
    void __(final String algorithm, final @Nullable String provider) {
        final var digest = Optional.ofNullable(provider)
                .map(v -> {
                    try {
                        return MessageDigest.getInstance(algorithm, v);
                    } catch (final NoSuchAlgorithmException nsae) {
                        throw new RuntimeException(nsae);
                    } catch (final NoSuchProviderException nspe) {
                        throw new RuntimeException(nspe);
                    }
                })
                .orElseGet(() -> {
                    try {
                        return MessageDigest.getInstance(algorithm);
                    } catch (final NoSuchAlgorithmException nsae) {
                        throw new RuntimeException(nsae);
                    }
                });
        final var digested = service().update(digest).digest();
        System.out.printf("%10s %10s: %s (%d)%n", digest.getProvider().getName(), algorithm,
                          HexFormat.of().formatHex(digested), digested.length);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * 기본 공급자({@code SUN}) 로 각 알고리즘에 대해 다이제스트 결과를 얻는지 확인한다.
     *
     * @param algorithm {@link #algorithms()} 가 제공하는 알고리즘 이름.
     */
    @MethodSource({"algorithms"})
    @ParameterizedTest
    void __SUN(final String algorithm) {
        __(algorithm, null);
    }

    /**
     * BouncyCastle 공급자가 등록되어 있지 않으면 등록한 뒤, 각 알고리즘에 대해 다이제스트 결과를
     * 얻는지 확인한다.
     *
     * @param algorithm {@link #algorithms()} 가 제공하는 알고리즘 이름.
     */
    @MethodSource({"algorithms"})
    @ParameterizedTest
    void __BC(final String algorithm) {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
        __(algorithm, BouncyCastleProvider.PROVIDER_NAME);
    }

    /**
     * SHA-256 의 쇄도 효과(avalanche effect) 를 시각적으로 보여 준다. 입력 한 글자만 다른 두 문자열
     * ({@code "hello, world"} 와 {@code "hello, worle"}) 의 해시 차이를 비트 단위로 세서, 전체 256
     * 비트 가운데 약 절반이 뒤집힘을 표준 출력에 찍는다. 입력의 작은 변화가 출력 전체를 골고루 흔드는
     * 좋은 해시의 성질을 확인하기 위한 데모이며, 어서션은 두지 않는다.
     */
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
        System.out.printf("hello, world: %s%n", HexFormat.of().formatHex(ha));
        System.out.printf("hello, worle: %s%n", HexFormat.of().formatHex(hb));
        System.out.printf("flipped bits: %d / %d (%.1f%%)%n",
                          diff, ha.length * 8, diff * 100.0 / (ha.length * 8));
    }

    /**
     * 자주 쓰이는 약한 비밀번호 몇 개에 대해 SHA-1 해시를 찍어, 무지개 표(rainbow table) 로 충분히
     * 역추적할 수 있음을 보여 주는 데모. 같은 비밀번호는 항상 같은 해시를 만들기 때문에, 솔트
     * (salt) 없이 해시만 저장해 두면 사전 공격에 그대로 노출된다. 어서션 없이 표준 출력으로만
     * 결과를 보여 준다.
     *
     * @param password {@link ValueSource} 가 공급하는 약한 비밀번호.
     */
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
        System.out.printf("%10s %s%n", password, HexFormat.of().formatHex(digested));
    }
}
