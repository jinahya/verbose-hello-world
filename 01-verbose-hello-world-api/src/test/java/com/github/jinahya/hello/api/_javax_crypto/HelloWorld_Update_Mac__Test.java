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
import java.nio.file.*;
import java.util.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static com.github.jinahya.hello.miscellaneous._javax_crypto._Javax_Crypto_Mac_TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
@_NotForPublishing
@DisplayName("update(mac)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Update_Mac__Test extends HelloWorld__Test {

    @TempDir
    private static Path tempDir;

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
        doAnswer(i -> {
            final var mac = i.getArgument(0, Mac.class);
            mac.update(hello_world_byte_array());
            return mac;
        }).when(service()).<Mac>update(notNull());
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies the method works with the {@code HmacSHA1} algorithm.
     *
     * @throws Exception if any error occurs.
     */
    @_LatestLTS
    @_LatestJDK
    @DisplayName("HmacSHA1")
    @Test
    void __HmacSHA1() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var maccing = HmacSHA1();
        final var mac = maccing.mac();
        // ----------------------------------------------------------------------------------- mac1
        if (maccing.params() != null) {
            mac.init(maccing.secretKey(), maccing.params());
        } else {
            mac.init(maccing.secretKey());
        }
        final var tag1 = service().update(mac).doFinal();
        printf("HmacSHA1", null, tag1);
        // ----------------------------------------------------------------------------------- mac2
        if (maccing.params() != null) {
            mac.init(maccing.secretKey(), maccing.params());
        } else {
            mac.init(maccing.secretKey());
        }
        final var tag2 = service().update(mac).doFinal();
        // ------------------------------------------------------------------------------------ then
        assertArrayEquals(tag1, tag2);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies the method works with the {@code HmacSHA224} algorithm.
     *
     * @throws Exception if any error occurs.
     */
    @_LatestLTS
    @_LatestJDK
    @DisplayName("HmacSHA224")
    @Test
    void __HmacSHA224() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var maccing = HmacSHA224();
        final var mac = maccing.mac();
        // ----------------------------------------------------------------------------------- mac1
        if (maccing.params() != null) {
            mac.init(maccing.secretKey(), maccing.params());
        } else {
            mac.init(maccing.secretKey());
        }
        final var tag1 = service().update(mac).doFinal();
        printf("HmacSHA224", null, tag1);
        // ----------------------------------------------------------------------------------- mac2
        if (maccing.params() != null) {
            mac.init(maccing.secretKey(), maccing.params());
        } else {
            mac.init(maccing.secretKey());
        }
        final var tag2 = service().update(mac).doFinal();
        // ------------------------------------------------------------------------------------ then
        assertArrayEquals(tag1, tag2);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies the method works with the {@code HmacSHA256} algorithm.
     *
     * @throws Exception if any error occurs.
     */
    @_LatestLTS
    @_LatestJDK
    @DisplayName("HmacSHA256")
    @Test
    void __HmacSHA256() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var maccing = HmacSHA256();
        final var mac = maccing.mac();
        // ----------------------------------------------------------------------------------- mac1
        if (maccing.params() != null) {
            mac.init(maccing.secretKey(), maccing.params());
        } else {
            mac.init(maccing.secretKey());
        }
        final var tag1 = service().update(mac).doFinal();
        printf("HmacSHA256", null, tag1);
        // ----------------------------------------------------------------------------------- mac2
        if (maccing.params() != null) {
            mac.init(maccing.secretKey(), maccing.params());
        } else {
            mac.init(maccing.secretKey());
        }
        final var tag2 = service().update(mac).doFinal();
        // ------------------------------------------------------------------------------------ then
        assertArrayEquals(tag1, tag2);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies the method works with the {@code HmacSHA384} algorithm.
     *
     * @throws Exception if any error occurs.
     */
    @_LatestLTS
    @_LatestJDK
    @DisplayName("HmacSHA384")
    @Test
    void __HmacSHA384() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var maccing = HmacSHA384();
        final var mac = maccing.mac();
        // ----------------------------------------------------------------------------------- mac1
        if (maccing.params() != null) {
            mac.init(maccing.secretKey(), maccing.params());
        } else {
            mac.init(maccing.secretKey());
        }
        final var tag1 = service().update(mac).doFinal();
        printf("HmacSHA384", null, tag1);
        // ----------------------------------------------------------------------------------- mac2
        if (maccing.params() != null) {
            mac.init(maccing.secretKey(), maccing.params());
        } else {
            mac.init(maccing.secretKey());
        }
        final var tag2 = service().update(mac).doFinal();
        // ------------------------------------------------------------------------------------ then
        assertArrayEquals(tag1, tag2);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies the method works with the {@code HmacSHA512} algorithm.
     *
     * @throws Exception if any error occurs.
     */
    @_LatestLTS
    @_LatestJDK
    @DisplayName("HmacSHA512")
    @Test
    void __HmacSHA512() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var maccing = HmacSHA512();
        final var mac = maccing.mac();
        // ----------------------------------------------------------------------------------- mac1
        if (maccing.params() != null) {
            mac.init(maccing.secretKey(), maccing.params());
        } else {
            mac.init(maccing.secretKey());
        }
        final var tag1 = service().update(mac).doFinal();
        printf("HmacSHA512", null, tag1);
        // ----------------------------------------------------------------------------------- mac2
        if (maccing.params() != null) {
            mac.init(maccing.secretKey(), maccing.params());
        } else {
            mac.init(maccing.secretKey());
        }
        final var tag2 = service().update(mac).doFinal();
        // ------------------------------------------------------------------------------------ then
        assertArrayEquals(tag1, tag2);
    }
}
