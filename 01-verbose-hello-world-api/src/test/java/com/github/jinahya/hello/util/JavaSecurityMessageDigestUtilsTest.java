package com.github.jinahya.hello.util;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2024 Jinahya, Inc.
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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A class testing methods defined in {@link JavaSecurityMessageDigestUtils} class.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class JavaSecurityMessageDigestUtilsTest {

    @DisplayName("updateDigest(digest, buffer, bytes)")
    @Nested
    class UpdateDigestTest {

        @Test
        void __() throws NoSuchAlgorithmException {
            final var buffer = ByteBuffer.allocate(ThreadLocalRandom.current().nextInt(128));
            buffer.limit(ThreadLocalRandom.current().nextInt(buffer.limit() + 1));
            buffer.position(
                    ThreadLocalRandom.current().nextInt(buffer.position(), buffer.remaining() + 1)
            );
            final var digest = MessageDigest.getInstance("SHA-1");
            final var bytes = ThreadLocalRandom.current().nextInt(0, buffer.position() + 1);
            JavaSecurityMessageDigestUtils.updateDigest(digest, buffer, bytes);
        }
    }
}
