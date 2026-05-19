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

import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import javax.crypto.Cipher;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Update_Cipher_Test
        extends HelloWorldTest {

    @DisplayName("""
            should throw a <NullPointerException>
            when the <cipher> argument is <null>""")
    @Test
    void _ThrowNullPointerException_CipherIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var cipher = (Cipher) null;
        @SuppressWarnings("unchecked")
        final var consumer = (Consumer<? super byte[]>) Mockito.mock(Consumer.class);
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.update(cipher, consumer)
        );
    }

    @DisplayName("""
            should throw a <NullPointerException>
            when the <consumer> argument is <null>""")
    @Test
    void _ThrowNullPointerException_ConsumerIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var cipher = Mockito.mock(Cipher.class);
        final var consumer = (Consumer<? super byte[]>) null;
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.update(cipher, consumer)
        );
    }

    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorldTestUtils.set_array_returns_the_array(service());
        final var cipher = Mockito.mock(Cipher.class);
        final var output = ThreadLocalRandom.current().nextBoolean() ? new byte[0] : null;
        Mockito.when(cipher.update(ArgumentMatchers.notNull())).thenReturn(output);
        @SuppressWarnings("unchecked")
        final var consumer = (Consumer<? super byte[]>) Mockito.mock(Consumer.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.update(cipher, consumer);
        // ------------------------------------------------------------------------------------ then
        final var array = HelloWorldTestUtils.set_array12_invoked_once(service);
        Mockito.verify(cipher).update(array);
        Mockito.verify(consumer, Mockito.times(1)).accept(output);
        Assertions.assertSame(cipher, result);
    }
}
