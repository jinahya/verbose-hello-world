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
 * Constants for testing {@link javax.crypto.Mac}.
 *
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/crypto/Mac.html">javax.crypto.Mac</a>
 * (Java 25)
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/26/docs/api/java.base/javax/crypto/Mac.html">javax.crypto.Mac</a>
 * (Java 26)
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/specs/security/standard-names.html#mac-algorithms">Java
 * Security Standard Algorithm Names / <code>Mac</code> Algorithms</a>
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/26/docs/specs/security/standard-names.html#mac-algorithms">Java
 * Security Standard Algorithm Names / <code>Mac</code> Algorithms</a>
 */
public final class _Javax_Crypto_Mac_TestConstants {

    public static final String MAC_SERVICE_TYPE = "Mac";

    /**
     * The {@code Mac} algorithms every Java 25 (latest LTS) implementation is required to support,
     * per the {@code javax.crypto.Mac} class javadoc.
     *
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/crypto/Mac.html">javax.crypto.Mac</a>
     * (Java 25)
     */
    @_LatestLTS
    public static final List<String> REQUIRED_ALGORITHMS_LATEST_LTS = List.of(
            "HmacSHA1",
            "HmacSHA256"
    );

    /**
     * The {@code Mac} algorithms every Java 26 (latest JDK) implementation is required to support,
     * per the {@code javax.crypto.Mac} class javadoc.
     *
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/26/docs/api/java.base/javax/crypto/Mac.html">javax.crypto.Mac</a>
     * (Java 26)
     */
    @_LatestJDK
    public static final List<String> REQUIRED_ALGORITHMS_LATEST_JDK = List.of(
            "HmacSHA1",
            "HmacSHA256",
            "PBEWithHmacSHA256"
    );

    // https://docs.oracle.com/en/java/javase/25/docs/specs/security/standard-names.html#mac-algorithms
    public static final List<String> ALGORITHM_NAMES_LATEST_LTS = List.of(
            "HmacMD5",
            "HmacSHA1",
            "HmacSHA224",
            "HmacSHA256",
            "HmacSHA384",
            "HmacSHA512",
            "HmacSHA512/224",
            "HmacSHA512/256",
            "HmacSHA3-224",
            "HmacSHA3-256",
            "HmacSHA3-384",
            "HmacSHA3-512",
            "HmacPBESHA1",
            "HmacPBESHA224",
            "HmacPBESHA256",
            "HmacPBESHA384",
            "HmacPBESHA512",
            "HmacPBESHA512/224",
            "HmacPBESHA512/256",
            "PBEWithHmacSHA256"
    );

    // https://docs.oracle.com/en/java/javase/26/docs/specs/security/standard-names.html#mac-algorithms
    public static final List<String> ALGORITHM_NAMES_LATEST_JDK = List.of(
            "HmacMD5",
            "HmacSHA1",
            "HmacSHA224",
            "HmacSHA256",
            "HmacSHA384",
            "HmacSHA512",
            "HmacSHA512/224",
            "HmacSHA512/256",
            "HmacSHA3-224",
            "HmacSHA3-256",
            "HmacSHA3-384",
            "HmacSHA3-512",
            "HmacPBESHA1",
            "HmacPBESHA224",
            "HmacPBESHA256",
            "HmacPBESHA384",
            "HmacPBESHA512",
            "HmacPBESHA512/224",
            "HmacPBESHA512/256",
            "PBEWithHmacSHA256"
    );

    private _Javax_Crypto_Mac_TestConstants() {
        throw new AssertionError("instantiation is not allowed");
    }
}
