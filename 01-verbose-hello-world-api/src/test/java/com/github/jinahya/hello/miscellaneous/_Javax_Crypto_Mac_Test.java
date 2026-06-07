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

import java.util.concurrent.*;

import static com.github.jinahya.hello.miscellaneous._Javax_Crypto_Mac_TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * .
 *
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/crypto/Mac.html">javax.crypto.Mac</a>
 * (Java 25)
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/26/docs/api/java.base/javax/crypto/Mac.html">javax.crypto.Mac</a>
 * (Java 26)
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
class _Javax_Crypto_Mac_Test {

    // ---------------------------------------------------------------------------------------------
    @LatestLTS
    @LatestJDK
    @DisplayName("HmacSHA1")
    @Test
    void __HmacSHA1() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var data = new byte[ThreadLocalRandom.current().nextInt(1024)];
        ThreadLocalRandom.current().nextBytes(data);
        final var maccing = HmacSHA1();
        final var mac = maccing.mac();
        // ----------------------------------------------------------------------------------- mac1
        if (maccing.params() != null) {
            mac.init(maccing.secretKey(), maccing.params());
        } else {
            mac.init(maccing.secretKey());
        }
        mac.update(data);
        final var tag1 = mac.doFinal();
        // ----------------------------------------------------------------------------------- mac2
        if (maccing.params() != null) {
            mac.init(maccing.secretKey(), maccing.params());
        } else {
            mac.init(maccing.secretKey());
        }
        mac.update(data);
        final var tag2 = mac.doFinal();
        // ------------------------------------------------------------------------------------ then
        assertArrayEquals(tag1, tag2);
    }

    // ---------------------------------------------------------------------------------------------
    @LatestLTS
    @LatestJDK
    @DisplayName("HmacSHA224")
    @Test
    void __HmacSHA224() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var data = new byte[ThreadLocalRandom.current().nextInt(1024)];
        ThreadLocalRandom.current().nextBytes(data);
        final var maccing = HmacSHA224();
        final var mac = maccing.mac();
        // ----------------------------------------------------------------------------------- mac1
        if (maccing.params() != null) {
            mac.init(maccing.secretKey(), maccing.params());
        } else {
            mac.init(maccing.secretKey());
        }
        mac.update(data);
        final var tag1 = mac.doFinal();
        // ----------------------------------------------------------------------------------- mac2
        if (maccing.params() != null) {
            mac.init(maccing.secretKey(), maccing.params());
        } else {
            mac.init(maccing.secretKey());
        }
        mac.update(data);
        final var tag2 = mac.doFinal();
        // ------------------------------------------------------------------------------------ then
        assertArrayEquals(tag1, tag2);
    }

    // ---------------------------------------------------------------------------------------------
    @LatestLTS
    @LatestJDK
    @DisplayName("HmacSHA256")
    @Test
    void __HmacSHA256() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var data = new byte[ThreadLocalRandom.current().nextInt(1024)];
        ThreadLocalRandom.current().nextBytes(data);
        final var maccing = HmacSHA256();
        final var mac = maccing.mac();
        // ----------------------------------------------------------------------------------- mac1
        if (maccing.params() != null) {
            mac.init(maccing.secretKey(), maccing.params());
        } else {
            mac.init(maccing.secretKey());
        }
        mac.update(data);
        final var tag1 = mac.doFinal();
        // ----------------------------------------------------------------------------------- mac2
        if (maccing.params() != null) {
            mac.init(maccing.secretKey(), maccing.params());
        } else {
            mac.init(maccing.secretKey());
        }
        mac.update(data);
        final var tag2 = mac.doFinal();
        // ------------------------------------------------------------------------------------ then
        assertArrayEquals(tag1, tag2);
    }

    // ---------------------------------------------------------------------------------------------
    @LatestLTS
    @LatestJDK
    @DisplayName("HmacSHA384")
    @Test
    void __HmacSHA384() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var data = new byte[ThreadLocalRandom.current().nextInt(1024)];
        ThreadLocalRandom.current().nextBytes(data);
        final var maccing = HmacSHA384();
        final var mac = maccing.mac();
        // ----------------------------------------------------------------------------------- mac1
        if (maccing.params() != null) {
            mac.init(maccing.secretKey(), maccing.params());
        } else {
            mac.init(maccing.secretKey());
        }
        mac.update(data);
        final var tag1 = mac.doFinal();
        // ----------------------------------------------------------------------------------- mac2
        if (maccing.params() != null) {
            mac.init(maccing.secretKey(), maccing.params());
        } else {
            mac.init(maccing.secretKey());
        }
        mac.update(data);
        final var tag2 = mac.doFinal();
        // ------------------------------------------------------------------------------------ then
        assertArrayEquals(tag1, tag2);
    }

    // ---------------------------------------------------------------------------------------------
    @LatestLTS
    @LatestJDK
    @DisplayName("HmacSHA512")
    @Test
    void __HmacSHA512() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var data = new byte[ThreadLocalRandom.current().nextInt(1024)];
        ThreadLocalRandom.current().nextBytes(data);
        final var maccing = HmacSHA512();
        final var mac = maccing.mac();
        // ----------------------------------------------------------------------------------- mac1
        if (maccing.params() != null) {
            mac.init(maccing.secretKey(), maccing.params());
        } else {
            mac.init(maccing.secretKey());
        }
        mac.update(data);
        final var tag1 = mac.doFinal();
        // ----------------------------------------------------------------------------------- mac2
        if (maccing.params() != null) {
            mac.init(maccing.secretKey(), maccing.params());
        } else {
            mac.init(maccing.secretKey());
        }
        mac.update(data);
        final var tag2 = mac.doFinal();
        // ------------------------------------------------------------------------------------ then
        assertArrayEquals(tag1, tag2);
    }
}
