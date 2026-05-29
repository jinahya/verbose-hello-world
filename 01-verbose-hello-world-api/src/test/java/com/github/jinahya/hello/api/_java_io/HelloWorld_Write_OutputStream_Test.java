package com.github.jinahya.hello.api._java_io;

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

import java.io.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing {@link HelloWorld#write(OutputStream) write(stream)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(stream)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Write_OutputStream_Test extends HelloWorld__Test {

    /**
     * Verifies that the {@link HelloWorld#write(OutputStream) write(stream)} method throws a
     * {@link NullPointerException} when the {@code stream} argument is {@code null}.
     */
    @DisplayName("""
            should throw a <NullPointerException>
            when the <stream> argument is <null>"""
    )
    @Test
    void _ThrowNullPointerException_StreamIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var stream = (OutputStream) null;
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.write(stream)
        );
    }

    /**
     * Asserts {@link HelloWorld#write(OutputStream) write(stream)} method invokes
     * {@link HelloWorld#set(byte[]) set(array)} method with an array of {@value HelloWorld#BYTES}
     * bytes, {@link OutputStream#write(byte[]) writes} the {@code array} to the {@code stream}, and
     * returns the {@code stream}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("""
            should invoke <set[array[12])>
            and writes the <array> to the <stream>"""
    )
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        doAnswer(returnsFirstArg()).when(service).set(any(byte[].class));
        final var stream = mock(OutputStream.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.write(stream);
        // ------------------------------------------------------------------------------------ then
        final var array = set_array12_invoked_once(service);
//        verify(stream, times(1)).write(array);
        assertSame(stream, result);
    }
}
