package com.github.jinahya.hello.miscellaneous.c02rfc862;

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

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ThreadLocalRandom;

final class _Rfc862Utils {

    static byte[] newByteArray() {
        return new byte[ThreadLocalRandom.current().nextInt(1024) + 1];
    }

    static ByteBuffer newByteBuffer() {
        return ByteBuffer.wrap(newByteArray());
    }

    private static final String ALGORITHM = "SHA-256";

    static MessageDigest newMessageDigest() {
        try {
            return MessageDigest.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException nsae) {
            throw new RuntimeException("failed to create a message digest for " + ALGORITHM, nsae);
        }
    }

    private _Rfc862Utils() {
        throw new AssertionError("instantiation is not allowed");
    }
}