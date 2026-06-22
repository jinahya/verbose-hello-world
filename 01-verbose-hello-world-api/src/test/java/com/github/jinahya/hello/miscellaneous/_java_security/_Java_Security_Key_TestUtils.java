package com.github.jinahya.hello.miscellaneous._java_security;

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
import java.util.*;

/**
 * A class providing test utilities for {@link java.security.Key} usages.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
@SuppressWarnings({"java:S101"})
class _Java_Security_Key_TestUtils {

    static void printKey(final Key key) {
        final var encoded = key.getEncoded();
        final var string = Base64.getEncoder().encodeToString(encoded);
        final var formatted =
                string.length() > 11
                ? string.substring(0, 4) + "..." + string.substring(string.length() - 4)
                : string;
        System.out.printf(
                "%30s %16s %10s %10d %s%n",
                key.getClass().getSimpleName(),
                key.getAlgorithm(),
                key.getFormat(),
                encoded.length,
                formatted
        );
    }

    private _Java_Security_Key_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
