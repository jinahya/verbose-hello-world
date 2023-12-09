package com.github.jinahya.hello.misc.c01rfc863;

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

import com.github.jinahya.hello.misc.c00rfc86_._Rfc86_Constants;

import java.net.InetSocketAddress;
import java.util.HexFormat;
import java.util.function.Function;

final class _Rfc863Constants
        extends _Rfc86_Constants {

    // ---------------------------------------------------------------------------------------- addr

    private static final int RFC863_PORT = 9;

    private static final int PORT = RFC863_PORT + 50000;

    static final InetSocketAddress ADDR = new InetSocketAddress(HOST, PORT);

    // ------------------------------------------------------------------------------------- timeout

    // -------------------------------------------------------------------------------------- digest

    static final String ALGORITHM = "SHA-1";

    static Function<? super byte[], ? extends CharSequence> PRINTER =
            b -> HexFormat.of().formatHex(b);

    // ---------------------------------------------------------------------------------------------
    private _Rfc863Constants() {
        super();
        throw new AssertionError("instantiation is not allowed");
    }
}
