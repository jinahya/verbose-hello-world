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

@畵蛇添足
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Update_MessageDigest_畵蛇添足_Test
        extends HelloWorldTest {

    @TempDir
    private static File tempDir;

    static List<String> algorithms() {
        return _Java_Security_TestUtils.MESSAGE_DIGEST_ALGORITHMS;
    }

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void __() {
        HelloWorldTestUtils.set_array_sets_actual_hello_world_bytes(service());
    }

    // ---------------------------------------------------------------------------------------------
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
    @MethodSource({"algorithms"})
    @ParameterizedTest
    void __(final String algorithm) {
        __(algorithm, null);
    }

    @MethodSource({"algorithms"})
    @ParameterizedTest
    void __BC(final String algorithm) {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
        __(algorithm, BouncyCastleProvider.PROVIDER_NAME);
    }

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
