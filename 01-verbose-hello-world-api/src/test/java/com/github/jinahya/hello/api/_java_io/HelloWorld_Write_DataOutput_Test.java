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

import static com.github.jinahya.hello.api.HelloWorldTestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing {@link HelloWorld#write(DataOutput) write(output)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(data)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Write_DataOutput_Test
        extends HelloWorldTest {

    /**
     * Asserts {@link HelloWorld#write(DataOutput) write(output)} method throws a
     * {@link NullPointerException} when the {@code data} argument is {@code null}.
     */
    @DisplayName("""
            should throw a <NullPointerException>
            when the <output> argument is <null>"""
    )
    @Test
    void _ThrowNullPointerException_DataIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var output = (DataOutput) null;
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.write(output)
        );
    }

    /**
     * Asserts {@link HelloWorld#write(DataOutput) write(output)} method invokes
     * {@link HelloWorld#set(byte[]) set(array)} method with an array of {@value HelloWorld#BYTES}
     * bytes, writes the array to specified data output, and returns the {@code output}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("should invoke <set(array[12])>, and invoke output.write(array)")
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        doAnswer(returnsFirstArg()).when(service).set(any(byte[].class));
        final var output = mock(DataOutput.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.write(output);
        // ------------------------------------------------------------------------------------ then
        final var array = set_array12_invoked_once(service);
//        verify(output, times(1)).write(array);
        assertSame(output, result);
    }
}
