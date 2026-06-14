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

import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;

import javax.crypto.*;
import java.security.*;

/**
 * A class for testing {@link KeyGenerator}.
 */
@DisplayName("javax.crypto.KeyGenerator")
@Slf4j
class _Javax_Crypto_KeyGenerator_Test {

    /**
     * Verifies that a {@link javax.crypto.SecretKey} can be generated for every registered
     * {@link KeyGenerator} algorithm and keysize.
     *
     * @throws NoSuchAlgorithmException if any algorithm is not available.
     */
    // ELSIE PREPARE TO MEET THY GOD
    @DisplayName("all algorithms / all keysizes")
    @Test
    void __() throws NoSuchAlgorithmException {
        for (var e : _Javax_Crypto_TestUtils.KEY_GENERATOR_ALGORITHMS_AND_KEYSIZES.entrySet()) {
            final var algorithm = e.getKey();
            final var keysizes = e.getValue();
            final var generator = KeyGenerator.getInstance(algorithm);
            if (keysizes.isEmpty()) {
                final var generated = generator.generateKey();
                _Java_Security_Key_Test.__(generated);
            } else {
                for (var keysize : keysizes) {
                    generator.init(keysize);
                    final var generated = generator.generateKey();
                    _Java_Security_Key_Test.__(generated);
                }
            }
        }
    }
}
