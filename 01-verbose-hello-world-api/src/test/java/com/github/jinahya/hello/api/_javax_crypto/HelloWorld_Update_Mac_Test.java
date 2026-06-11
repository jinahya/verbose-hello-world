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
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;

import javax.crypto.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing {@link com.github.jinahya.hello.api.HelloWorld#update(Mac)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/crypto/Mac.html">javax.crypto.Mac</a>
 */
@DisplayName("update(mac)")
@Slf4j
class HelloWorld_Update_Mac_Test
        extends HelloWorld__Test {

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that the method throws a {@link NullPointerException} when the {@code mac} argument
     * is {@code null}.
     */
    @DisplayName("should throw a <NullPointerException> when the <mac> argument is <null>")
    @Test
    void _ThrowNullPointerException_MacIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final Mac mac = null;
        // ----------------------------------------------------------------------------- when / then
        assertThrows(NullPointerException.class, () -> service.update(mac));
    }

    /**
     * Verifies that the method invokes {@code mac.update(buffer)}, and returns the {@code mac}.
     */
    @DisplayName("should invoke <mac.update(buffer)>, and return the <mac>")
    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = set_array_returns_the_array(service());
        final var mac = mock(Mac.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.update(mac);
        // ------------------------------------------------------------------------------------ then
        final var array = set_array12_invoked_once(service);
        verify(mac, times(1)).update(array);
        assertSame(mac, result);
    }
}
