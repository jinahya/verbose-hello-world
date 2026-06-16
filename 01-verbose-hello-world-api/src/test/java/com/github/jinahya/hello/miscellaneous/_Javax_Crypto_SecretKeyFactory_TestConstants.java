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

import com.github.jinahya.hello.api.annotations.*;

import java.util.*;

/**
 * Constants for testing {@link javax.crypto.SecretKeyFactory}.
 *
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/crypto/SecretKeyFactory.html">javax.crypto.SecretKeyFactory</a>
 * (Java 25)
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/26/docs/api/java.base/javax/crypto/SecretKeyFactory.html">javax.crypto.SecretKeyFactory</a>
 * (Java 26)
 */
public final class _Javax_Crypto_SecretKeyFactory_TestConstants {

    public static final String SECRET_KEY_FACTORY_SERVICE_TYPE = "SecretKeyFactory";

    /**
     * .
     *
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/crypto/SecretKeyFactory.html">javax.crypto.SecretKeyFactory</a>
     * (Java 25)
     */
    @_LatestLTS
    public static final Map<String, List<Object>> ALGORITHMS_AND_KEYSIZES_LATEST_LTS =
            Map.ofEntries(
                    Map.entry("DESede", List.of())
            );

    /**
     * .
     *
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/26/docs/api/java.base/javax/crypto/SecretKeyFactory.html">javax.crypto.SecretKeyFactory</a>
     * (Java 26)
     */
    @_LatestJDK
    public static final Map<String, List<Object>> ALGORITHMS_AND_KEYSIZES_LATEST_JDK =
            Map.ofEntries(
                    Map.entry("PBEWithHmacSHA256AndAES_128", List.of()),
                    Map.entry("PBEWithHmacSHA256AndAES_256", List.of()),
                    Map.entry("PBKDF2WithHmacSHA256", List.of())
            );

    private _Javax_Crypto_SecretKeyFactory_TestConstants() {
        throw new AssertionError("instantiation is not allowed");
    }
}
