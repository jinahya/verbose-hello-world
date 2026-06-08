package com.github.jinahya.hello.api._javax_crypto;

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
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;

import javax.crypto.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing {@link HelloWorld#updateAAD(Cipher) updateAAD(cipher)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/crypto/Cipher.html">javax.crypto.Cipher</a>
 */
@DisplayName("updateAAD(cipher)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_UpdateAAD_Cipher_Test extends HelloWorld__Test {

    /**
     * Verifies that the {@link HelloWorld#updateAAD(Cipher) updateAAD(cipher)} method throws a
     * {@link NullPointerException} when the {@code cipher} argument is {@code null}.
     */
    @DisplayName("should throw a <NullPointerException> when the <cipher> argument is <null>")
    @Test
    void _ThrowNullPointerException_CipherIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final Cipher cipher = null;
        // ----------------------------------------------------------------------------- when / then
        assertThrows(NullPointerException.class, () -> service.updateAAD(cipher));
    }

    /**
     * Verifies that the {@link HelloWorld#updateAAD(Cipher) updateAAD(cipher)} method invokes
     * {@link Cipher#updateAAD(byte[]) cipher.updateAAD(array)} with the array filled by
     * {@link HelloWorld#set(byte[]) set(array)}, and returns the {@code cipher}.
     */
    @DisplayName("should invoke <cipher.updateAAD(array)>, and return the <cipher>")
    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = set_array_returns_the_array(service());
        final var cipher = mock(Cipher.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.updateAAD(cipher);
        // ------------------------------------------------------------------------------------ then
        final var array = set_array12_invoked_once(service);
        verify(cipher).updateAAD(array);
        assertSame(cipher, result);
    }
}
