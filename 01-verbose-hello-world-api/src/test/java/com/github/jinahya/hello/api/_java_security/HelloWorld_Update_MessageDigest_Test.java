package com.github.jinahya.hello.api._java_security;

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
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;

import java.security.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing
 * {@link com.github.jinahya.hello.api.HelloWorld#update(MessageDigest) update(digest)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/security/MessageDigest.html">java.security.MessageDigest</a>
 */
@DisplayName("update(digest)")
@Slf4j
class HelloWorld_Update_MessageDigest_Test
        extends HelloWorld__Test {

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that the
     * {@link com.github.jinahya.hello.api.HelloWorld#update(MessageDigest) update(digest)} method
     * throws a {@link NullPointerException} when the {@code digest} argument is {@code null}.
     */
    @DisplayName("should throw a <NullPointerException> when the <digest> argument is <null>")
    @Test
    void _ThrowNullPointerException_DigestIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final MessageDigest digest = null;
        // ----------------------------------------------------------------------------- when / then
        assertThrows(NullPointerException.class, () -> service.update(digest));
    }

    /**
     * Verifies that the
     * {@link com.github.jinahya.hello.api.HelloWorld#update(MessageDigest) update(digest)} method
     * invokes {@link MessageDigest#update(byte[]) digest.update(array)} with the array filled by
     * {@link com.github.jinahya.hello.api.HelloWorld#set(byte[]) set(array)}, and returns the
     * {@code digest}.
     */
    @DisplayName("should invoke <digest.update(buffer)>, and return the <digest>")
    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = set_array_returns_the_array(service());
        final var digest = mock(MessageDigest.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.update(digest);
        // ------------------------------------------------------------------------------------ then
        final var array = set_array12_invoked_once(service);
        verify(digest, times(1)).update(array);
        assertSame(digest, result);
    }
}
