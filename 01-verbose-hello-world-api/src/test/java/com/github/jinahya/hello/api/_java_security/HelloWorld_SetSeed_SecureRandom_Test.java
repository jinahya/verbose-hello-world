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
 * {@link com.github.jinahya.hello.api.HelloWorld#setSeed(SecureRandom) setSeed(random)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/27/docs/api/java.base/java/security/SecureRandom.html">java.security.SecureRandom</a>
 */
@DisplayName("setSeed(random)")
@Slf4j
class HelloWorld_SetSeed_SecureRandom_Test extends HelloWorld__Test {

    /**
     * Verifies that the
     * {@link com.github.jinahya.hello.api.HelloWorld#setSeed(SecureRandom) setSeed(random)} method
     * throws a {@link NullPointerException} when the {@code random} argument is {@code null}.
     */
    @DisplayName("throws NPE / random is null")
    @Test
    void _ThrowNullPointerException_RandomIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final SecureRandom random = null;
        // ----------------------------------------------------------------------------- when / then
        assertThrows(NullPointerException.class, () -> service.setSeed(random));
    }

    /**
     * Verifies that the
     * {@link com.github.jinahya.hello.api.HelloWorld#setSeed(SecureRandom) setSeed(random)} method
     * invokes {@link HelloWorld#set(byte[]) set(array)} once, reseeds the {@code random} with the
     * array, and returns the {@code random}.
     */
    @DisplayName("happy path")
    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = set_array_returns_the_array(service());
        final var random = mock(SecureRandom.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.setSeed(random);
        // ------------------------------------------------------------------------------------ then
        final var array = set_array12_invoked_once(service());
        verify(random, times(1)).setSeed(array);
        verifyNoMoreInteractions(random);
        assertSame(random, result);
    }
}
