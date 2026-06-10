package com.github.jinahya.hello.api._java_util_zip;

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

import java.util.zip.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing {@link HelloWorld#setInput(Deflater) setInput(deflater)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("setInput(deflater)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_SetInput_Deflater_Test extends HelloWorld__Test {

    /**
     * Verifies that the {@link HelloWorld#setInput(Deflater) setInput(deflater)} method throws a
     * {@link NullPointerException} when the {@code deflater} argument is {@code null}.
     */
    @DisplayName("should throw a <NullPointerException> when the <deflater> argument is <null>")
    @Test
    void _ThrowNullPointerException_DeflaterIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final Deflater deflator = null;
        // ----------------------------------------------------------------------------- when / then
        assertThrows(NullPointerException.class, () -> service.setInput(deflator));
    }

    /**
     * Verifies that the {@link HelloWorld#setInput(Deflater) setInput(deflater)} method invokes
     * {@link HelloWorld#set(byte[]) set(array)} once and passes the array to
     * {@link Deflater#setInput(byte[]) deflater.setInput(array)}, and returns the
     * {@code deflater}.
     */
    @DisplayName("should invoke <set(byte[12])> and pass the array to <deflater.setInput>")
    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = set_array_returns_the_array(service());
        final var deflater = mock(Deflater.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.setInput(deflater);
        // ------------------------------------------------------------------------------------ then
        final var array = set_array12_invoked_once(service);
        verify(deflater, times(1)).setInput(array);
        verifyNoMoreInteractions(deflater);
        assertSame(deflater, result);
    }
}
