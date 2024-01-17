package com.github.jinahya.hello._01_lang;

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

import com.github.jinahya.hello.HelloWorld;
import com.github.jinahya.hello._HelloWorldTest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import java.io.IOException;

/**
 * A class for testing {@link HelloWorld#append(Appendable) append(appendable)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("append(appendable) arguments")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_03_Append_Appendable_Test extends _HelloWorldTest {

    /**
     * Verifies that the {@link HelloWorld#append(Appendable) append(appendable)} method throws a
     * {@link NullPointerException} when the {@code appendable} argument is {@code null}.
     */
    @DisplayName("""
            should throw a NullPointerException
            when the appendable argument is null"""
    )
    @Test
    void _ThrowNullPointerException_AppendableNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var appendable = (Appendable) null;
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.append(appendable)
        );
    }

    /**
     * Verifies {@link HelloWorld#append(Appendable) append(appendable)} method invokes
     * {@link HelloWorld#set(byte[]) set(array)} method with an array of {@value HelloWorld#BYTES}
     * bytes, appends each byte to {@code appendable}, and returns the {@code appendable}.
     */
    @DisplayName("-> set(array[12]) -> appendable.append((char) b)")
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        BDDMockito.willAnswer(i -> {
            final var array = i.getArgument(0, byte[].class);
            if (array != null) {
                for (int j = 0; j < array.length; j++) {
                    array[j] = (byte) j;
                }
            }
            return array;
        }).given(service).set(ArgumentMatchers.any(byte[].class));
        final var appendable = Mockito.mock(Appendable.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.append(appendable);
        // ------------------------------------------------------------------------------------ then
        final var arrayCaptor = ArgumentCaptor.forClass(byte[].class);
        Mockito.verify(service, Mockito.times(1)).set(arrayCaptor.capture());
        final var array = arrayCaptor.getValue();
        Assertions.assertNotNull(array);
        Assertions.assertEquals(HelloWorld.BYTES, array.length);
        // TODO: verify each byte in <array> has been appended to <appendable>
        Assertions.assertSame(appendable, result);
    }
}
