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
import java.util.*;

/**
 * A utility class for printing {@link Key} summaries to {@link System#out}.
 */
@Slf4j
class _Java_Security_Key_Test {

    static void __(final Key key) {
        final var encoded = key.getEncoded();
        System.out.printf(
                "%20s %16s %10s %10d %s%n",
                key.getClass().getSimpleName(),
                key.getAlgorithm(),
                key.getFormat(),
                encoded.length,
                Base64.getEncoder().encodeToString(encoded)
        );
    }

    private _Java_Security_Key_Test() {
        throw new AssertionError("instantiation is not allowed");
    }
}
