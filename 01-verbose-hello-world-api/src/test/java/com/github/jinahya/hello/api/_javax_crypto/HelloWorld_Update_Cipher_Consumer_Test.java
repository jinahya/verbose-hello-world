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
import org.mockito.*;

import javax.crypto.*;
import java.util.function.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing {@link HelloWorld#update(Cipher, ObjIntConsumer) update(cipher, consumer)}
 * method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/crypto/Cipher.html">javax.crypto.Cipher</a>
 */
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
        final Cipher cipher = null;
        final var consumer = mock(ObjIntConsumer.class);
        // ----------------------------------------------------------------------------- when / then
        assertThrows(NullPointerException.class, () -> service.update(cipher, consumer));
    }

    @DisplayName("should throw a <NullPointerException> when the <consumer> argument is <null>")
    @Test
    void _ThrowNullPointerException_ConsumerIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var cipher = mock(Cipher.class);
        final ObjIntConsumer<byte[]> consumer = null;
        // ----------------------------------------------------------------------------- when / then
        assertThrows(NullPointerException.class, () -> service.update(cipher, consumer));
    }

    @DisplayName("""
            should invoke <cipher.update(array, 0, BYTES, output, 0)>,
            forward <(output, length)> to the <consumer>, and return the <cipher>""")
    @Test
    @SuppressWarnings("unchecked")
    void __() throws ShortBufferException {
        // ----------------------------------------------------------------------------------- given
        final var service = set_array_returns_the_array(service());
        final var cipher = mock(Cipher.class);
        final var outputSize = HelloWorld.BYTES + 4;
        doReturn(outputSize).when(cipher).getOutputSize(HelloWorld.BYTES);
        final var bytesStored = HelloWorld.BYTES;
        doReturn(bytesStored).when(cipher)
                .update(any(byte[].class), anyInt(), anyInt(), any(byte[].class), anyInt());
        final var consumer = (ObjIntConsumer<byte[]>) mock(ObjIntConsumer.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.update(cipher, consumer);
        // ------------------------------------------------------------------------------------ then
        verify(cipher).getOutputSize(HelloWorld.BYTES);
        final var outputCaptor = ArgumentCaptor.forClass(byte[].class);
        verify(consumer).accept(outputCaptor.capture(), eq(bytesStored));
        assertEquals(outputSize, outputCaptor.getValue().length);
        assertSame(cipher, result);
    }
}
