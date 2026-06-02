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

import java.util.*;

/**
 * .
 *
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/security/Signature.html">java.security.Signature</a>
 * (Java 25)
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/security/Signature.html">java.security.Signature</a>
 * (Java 26)
 */
public final class _Java_Security_Signature_TestConstants {

    /**
     * .
     *
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/security/Signature.html">java.security.Signature</a>
     * (Java 25)
     */
    @LatestLTS
    public static final Map<String, List<Object>> ALGORITHMS_AND_PARAMETERS_LATEST_LTS =
            Map.ofEntries(
                    Map.entry("RSASSA-PSS", List.of("SHA-256", "SHA-384")),
                    Map.entry("SHA1withDSA", List.of()),
                    Map.entry("SHA256withDSA", List.of()),
                    Map.entry("SHA256withECDSA", List.of("secp256r1")),
                    Map.entry("SHA384withECDSA", List.of("secp384r1")),
                    Map.entry("SHA1withRSA", List.of()),
                    Map.entry("SHA256withRSA", List.of()),
                    Map.entry("SHA384withRSA", List.of())
            );

    /**
     * .
     *
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/security/Signature.html">java.security.Signature</a>
     * (Java 26)
     */
    @LatestJDK
    public static final Map<String, List<Object>> ALGORITHMS_AND_PARAMETERS_LATEST_JDK =
            Map.ofEntries(
                    Map.entry("RSASSA-PSS", List.of("SHA-256", "SHA-384")),
                    Map.entry("SHA1withDSA", List.of()),
                    Map.entry("SHA256withDSA", List.of()),
                    Map.entry("SHA256withECDSA", List.of("secp256r1")),
                    Map.entry("SHA384withECDSA", List.of("secp384r1")),
                    Map.entry("SHA1withRSA", List.of()),
                    Map.entry("SHA256withRSA", List.of()),
                    Map.entry("SHA384withRSA", List.of())
            );

    private _Java_Security_Signature_TestConstants() {
        throw new AssertionError("instantiation is not allowed");
    }
}
