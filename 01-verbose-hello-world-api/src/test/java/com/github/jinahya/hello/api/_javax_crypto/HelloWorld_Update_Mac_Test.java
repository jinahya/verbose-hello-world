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
import org.mockito.*;

import javax.crypto.*;

/**
 * A class for testing {@link com.github.jinahya.hello.api.HelloWorld#update(Mac)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/crypto/Mac.html">javax.crypto.Mac</a>
 */
@Slf4j
class HelloWorld_Update_Mac_Test
        extends HelloWorld__Test {

    // ---------------------------------------------------------------------------------------------
    @Test
    void _ThrowNullPointerException_MacIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final Mac mac = null;
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(NullPointerException.class, () -> service.update(mac));
    }

    @DisplayName("mac.update(set(byte[12]))")
    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorld__TestUtils.set_array_returns_the_array(service());
        final var mac = Mockito.mock(Mac.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.update(mac);
        // ------------------------------------------------------------------------------------ then
        final var array = HelloWorld__TestUtils.set_array12_invoked_once(service);
        Mockito.verify(mac, Mockito.times(1)).update(array);
        Assertions.assertSame(mac, result);
    }
}
