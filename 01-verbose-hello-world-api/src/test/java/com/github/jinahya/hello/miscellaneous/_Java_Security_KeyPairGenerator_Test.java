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
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.security.*;
import java.security.spec.*;

@DisplayName("KeyPairGenerator")
@Slf4j
class _Java_Security_KeyPairGenerator_Test {

    @DisplayName("should generate an <RSA> key pair at <4096> bits")
    @Test
    void __RSA() throws NoSuchAlgorithmException {
        final var generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(4096);
        final var generated = generator.generateKeyPair();
        _Java_Security_KeyPair_TestUtils.printKeyPair(generated);
    }

    @DisplayName("should generate an <EC> key pair over the given <stdName> curve")
    @ValueSource(strings = {
            "secp256r1", "secp384r1"
    })
    @ParameterizedTest
    void __EC(final String stdName)
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        final var generator = KeyPairGenerator.getInstance("EC");
        final var spec = new ECGenParameterSpec(stdName);
        generator.initialize(spec);
        final var generated = generator.generateKeyPair();
        _Java_Security_KeyPair_TestUtils.printKeyPair(generated);
    }
}
