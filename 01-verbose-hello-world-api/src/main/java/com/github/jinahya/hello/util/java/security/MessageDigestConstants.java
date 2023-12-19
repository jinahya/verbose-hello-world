package com.github.jinahya.hello.util.java.security;

/*-
 * #%L
 * verbose-hello-world-api
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

import com.github.jinahya.hello.util._ExcludeFromCoverage_PrivateConstructor_Obviously;

import java.security.MessageDigest;

public final class MessageDigestConstants {

    public static final String CRYPTO_SERVICE = MessageDigest.class.getSimpleName();

    public static final String ALGORITHM_MD5 = "MD5";

    public static final String ALGORITHM_SHA_1 = "SHA-1";

    // ---------------------------------------------------------------------------------------------
    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private MessageDigestConstants() {
        throw new AssertionError("instantiation is not allowed");
    }
}
