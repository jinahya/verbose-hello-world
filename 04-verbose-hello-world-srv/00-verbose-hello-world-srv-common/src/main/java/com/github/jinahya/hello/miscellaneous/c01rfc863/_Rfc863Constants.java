package com.github.jinahya.hello.miscellaneous.c01rfc863;

/*-
 * #%L
 * verbose-hello-world-srv-common
 * %%
 * Copyright (C) 2018 - 2023 Jinahya, Inc.
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

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.time.Duration;

final class _Rfc863Constants {

    static final InetAddress ADDR = InetAddress.getLoopbackAddress();

    private static final int PORT = 9 + 50000;

    static final InetSocketAddress ADDRESS = new InetSocketAddress(ADDR, PORT);

    static final String ALGORITHM = "SHA-1";

    static final Duration SO_TIMEOUT = Duration.ofSeconds(16);

    private _Rfc863Constants() {
        throw new AssertionError("instantiation is not allowed");
    }
}
