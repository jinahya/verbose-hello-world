package com.github.jinahya.hello.api._java_lang;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2019 Jinahya, Inc.
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

import java.io.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing {@link HelloWorld#append(Appendable) append(appendable)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("append(appendable)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Append_Appendable_Test extends HelloWorld__Test {

    /**
     * Verifies that the {@link HelloWorld#append(Appendable) append(appendable)} method throws a
     * {@link NullPointerException} when the {@code appendable} argument is {@code null}.
     */
    @DisplayName("throws NPE / appendable is null")
    @Test
    void _ThrowNullPointerException_AppendableNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var appendable = (Appendable) null;
        // ------------------------------------------------------------------------------- when/then
        assertThrows(NullPointerException.class, () -> service.append(appendable));
    }

    /**
     * Verifies that the {@link HelloWorld#append(Appendable) append(appendable)} method invokes
     * {@link HelloWorld#set(byte[]) set(array)} method with an array of {@value HelloWorld#BYTES}
     * bytes, appends each byte to {@code appendable}, and returns the {@code appendable}.
     *
     * @see ArgumentCaptor#getValue()
     * @see ArgumentCaptor#getAllValues()
     */
    @DisplayName("happy path")
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        doAnswer(i -> {
            final var array = i.getArgument(0, byte[].class);
            final var src = new byte[HelloWorld.BYTES];
            ThreadLocalRandom.current().nextBytes(src);
            System.arraycopy(src, 0, array, 0, HelloWorld.BYTES);
            return array;
        }).when(service).set(argThat(v -> v != null && v.length >= HelloWorld.BYTES));
        final var appendable = mock(Appendable.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.append(appendable);
        // ------------------------------------------------------------------------------------ then
//        final var arrayCaptor = ArgumentCaptor.forClass(byte[].class);
//        verify(service, times(1)).set(arrayCaptor.capture());
//        final var array = arrayCaptor.getValue();
//        assertEquals(HelloWorld.BYTES, array.length);
//        final var charCaptor = ArgumentCaptor.forClass(char.class);
//        verify(appendable, times(array.length)).append(charCaptor.capture());
//        final var chars = charCaptor.getAllValues();
//        for (int i = 0; i < chars.size(); i++) {
//            assertEquals(array[i], (byte) chars.get(i).charValue());
//        }
        assertSame(appendable, result);
    }
}
