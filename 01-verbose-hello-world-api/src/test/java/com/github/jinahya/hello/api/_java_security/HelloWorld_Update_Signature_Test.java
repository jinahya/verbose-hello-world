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
 * {@link com.github.jinahya.hello.api.HelloWorld#update(Signature) update(signature)} 메서드를 테스트하는
 * 클래스.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/security/Signature.html">java.security.Signature</a>
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/security/KeyPairGenerator.html">java.security.KeyPairGenerator</a>
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/specs/security/standard-names.html#signature-algorithms">Signature
 * Algorithms</a>
 */
@DisplayName("update(signature)")
@Slf4j
class HelloWorld_Update_Signature_Test
        extends HelloWorld__Test {

    /**
     * {@code signature} 인수가 {@code null} 일 때
     * {@link com.github.jinahya.hello.api.HelloWorld#update(Signature) update(signature)} 메서드가
     * {@link NullPointerException} 을 던지는지 검증한다.
     */
    @DisplayName("should throw a <NullPointerException> when the <signature> argument is <null>")
    @Test
    void _ThrowNullPointerException_SignatureIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final Signature signature = null;
        // ----------------------------------------------------------------------------- when / then
        assertThrows(NullPointerException.class, () -> service.update(signature));
    }

    /**
     * {@link com.github.jinahya.hello.api.HelloWorld#update(Signature) update(signature)} 메서드가
     * {@link com.github.jinahya.hello.api.HelloWorld#set(byte[]) set(array)} 가 채워 준 배열로
     * {@link Signature#update(byte[]) signature.update(array)} 를 호출하고, 전달된 signature 를 그대로 반환하는지
     * 검증한다.
     */
    @DisplayName("should invoke <signature.update(buffer)>, and return the <signature>")
    @Test
    void __() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var service = set_array_returns_the_array(service());
        final var signature = mock(Signature.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.update(signature);
        // ------------------------------------------------------------------------------------ then
        final var array = set_array12_invoked_once(service);
        verify(signature, times(1)).update(array);
        assertEquals(signature, result);
    }
}
