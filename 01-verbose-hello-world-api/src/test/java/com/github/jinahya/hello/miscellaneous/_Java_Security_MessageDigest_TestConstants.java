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
import lombok.extern.slf4j.*;

import java.util.*;

/**
 * Constants for testing {@link java.security.MessageDigest} usages.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/security/MessageDigest.html">java.security.MessageDigest</a>
 * (Java 25)
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/security/MessageDigest.html">java.security.MessageDigest</a>
 * (Java 26)
 */
@Slf4j
@SuppressWarnings({"java:S101"})
public final class _Java_Security_MessageDigest_TestConstants {

    public static final String MESSAGE_DIGEST_SERVICE_TYPE = "MessageDigest";

    /**
     * The {@link java.security.MessageDigest} algorithm names tested by this module.
     */
    public static final List<String> MESSAGE_DIGEST_ALGORITHMS = List.of(
            "SHA-1",
            "SHA-256",
            "SHA-384"
    );

    /**
     * .
     *
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/security/MessageDigest.html">java.security.MessageDigest</a>
     * (Java 25)
     */
    @_LatestLTS
    public static final List<String> ALGORITHMS_LATEST_LTS = List.of(
            "SHA-1",
            "SHA-256",
            "SHA-384"
    );

    /**
     * .
     *
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/security/MessageDigest.html">java.security.MessageDigest</a>
     * (Java 26)
     */
    @_LatestJDK
    public static final List<String> ALGORITHMS_LATEST_JDK = List.of(
            "SHA-1",
            "SHA-256",
            "SHA-384"
    );

    private _Java_Security_MessageDigest_TestConstants() {
        throw new AssertionError("instantiation is not allowed");
    }
}
