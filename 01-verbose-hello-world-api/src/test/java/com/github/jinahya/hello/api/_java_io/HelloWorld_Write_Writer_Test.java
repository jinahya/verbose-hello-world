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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing {@link HelloWorld#write(Writer) write(writer)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://github.com/jinahya/verbose-hello-world/issues/1">Implement
 * HelloWorld#write(Writer)</a> (GitHub Issues)
 */
@DisplayName("HelloWorld.write(Writer)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Write_Writer_Test extends HelloWorld__Test {

    /**
     * Verifies that the {@link HelloWorld#write(Writer) write(writer)} method throws a
     * {@link NullPointerException} when the {@code writer} argument is {@code null}.
     */
    @DisplayName("throws NPE / writer is null")
    @Test
    void _ThrowNullPointerException_WriterIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var writer = (Writer) null;
        // ------------------------------------------------------------------------------- when/then
        assertThrows(NullPointerException.class, () -> service.write(writer));
    }

    /**
     * Verifies that the {@link HelloWorld#write(Writer) write(writer)} method invokes
     * {@link HelloWorld#append(Appendable) append(appendable)} method with given {@code writer},
     * and returns the {@code writer}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("happy path")
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        doAnswer(returnsFirstArg()).when(service).append(any(Appendable.class));
        final var writer = mock(Writer.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.write(writer);
        // ------------------------------------------------------------------------------------ then
//        verify(service, times(1)).append(writer);
        assertSame(writer, result);
    }
}
