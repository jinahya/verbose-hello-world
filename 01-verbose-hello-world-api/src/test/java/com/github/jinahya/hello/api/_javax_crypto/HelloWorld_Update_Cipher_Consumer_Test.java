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
import java.util.concurrent.*;
import java.util.function.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("update(cipher, consumer)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Update_Cipher_Consumer_Test extends HelloWorld__Test {

    @DisplayName("should throw a <NullPointerException> when the <cipher> argument is <null>")
    @Test
    @SuppressWarnings("unchecked")
    void _ThrowNullPointerException_CipherIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var cipher = (Cipher) null;
        final var consumer = mock(Consumer.class);
        // ----------------------------------------------------------------------------- when / then
        assertThrows(NullPointerException.class, () -> service.update(cipher, consumer));
    }

    @DisplayName("should throw a <NullPointerException> when the <consumer> argument is <null>")
    @Test
    @SuppressWarnings("unchecked")
    void _ThrowNullPointerException_ConsumerIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var cipher = mock(Cipher.class);
        final var consumer = (Consumer) null;
        // ----------------------------------------------------------------------------- when / then
        assertThrows(NullPointerException.class, () -> service.update(cipher, consumer));
    }

    @DisplayName("""
            should invoke <cipher.update(buffer)>, forward the result to the <consumer>,
            and return the <cipher>""")
    @Test
    @SuppressWarnings("unchecked")
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = set_array_returns_the_array(service());
        final var cipher = mock(Cipher.class);
        final var output = ThreadLocalRandom.current().nextBoolean() ? new byte[0] : null;
        when(cipher.update(any())).thenReturn(output);
        final var consumer = mock(Consumer.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.update(cipher, consumer);
        // ------------------------------------------------------------------------------------ then
        final var array = set_array12_invoked_once(service);
        verify(cipher).update(array);
        verify(consumer, times(1)).accept(output);
        assertSame(cipher, result);
    }
}
