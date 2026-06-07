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

import java.util.*;

/**
 * .
 *
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/crypto/KDF.html">javax.crypto.KDF</a>
 * (Java 25)
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/26/docs/api/java.base/javax/crypto/KDF.html">javax.crypto.KDF</a>
 * (Java 26)
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/specs/security/standard-names.html#kdf-algorithms">Java
 * Security Standard Algorithm Names / <code>KDF</code> Algorithms</a> (Java 25)
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/26/docs/specs/security/standard-names.html#kdf-algorithms">Java
 * Security Standard Algorithm Names / <code>KDF</code> Algorithms</a> (Java 26)
 */
public final class _Javax_Crypto_KDF_TestConstants {

    public static final String KDF_SERVICE_TYPE = "KDF";

    public static final List<String> KDF_ALGORITHMS_LATEST_LTS = List.of(
            "HKDF-SHA256",
            "HKDF-SHA384",
            "HKDF-SHA512"
    );

    public static final List<String> KDF_ALGORITHMS_LATEST_JDK = List.of(
            "HKDF-SHA256",
            "HKDF-SHA384",
            "HKDF-SHA512"
    );

    private _Javax_Crypto_KDF_TestConstants() {
        throw new AssertionError("instantiation is not allowed");
    }
}
