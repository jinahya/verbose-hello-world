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

import java.security.*;
import java.security.spec.*;

@Slf4j
public class _Java_Security_KeyPair_TestUtils {

    static void printKeyPair(final KeyPair pair) {
        _Java_Security_Key_TestUtils.printKey(pair.getPrivate());
        _Java_Security_Key_TestUtils.printKey(pair.getPublic());
    }

    public static KeyPair generateKeyPair(final String algorithm) throws NoSuchAlgorithmException {
        final var generator = KeyPairGenerator.getInstance(algorithm);
        return generator.generateKeyPair();
    }

    public static KeyPair generateKeyPair(final String algorithm, final int keysize)
            throws NoSuchAlgorithmException {
        final var generator = KeyPairGenerator.getInstance(algorithm);
        generator.initialize(keysize);
        return generator.generateKeyPair();
    }

    public static KeyPair generateKeyPair(final String algorithm, final AlgorithmParameterSpec spec)
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        final var generator = KeyPairGenerator.getInstance(algorithm);
        generator.initialize(spec);
        return generator.generateKeyPair();
    }

    private _Java_Security_KeyPair_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
