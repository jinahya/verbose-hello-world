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

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * A class for testing {@link HelloWorld#write(BufferedWriter) write(writer)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/io/BufferedWriter.html">java.io.BufferedWriter</a>
 */
@DisplayName("write(BufferedWriter)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Write_BufferedWriter_Test
        extends HelloWorldTest {

    /**
     * Verifies that the {@link HelloWorld#write(BufferedWriter) write(writer)} method throws a
     * {@link NullPointerException} when the {@code writer} argument is {@code null}.
     */
    @DisplayName("""
            should throw a <NullPointerException>
            when the <writer> argument is <null>"""
    )
    @Test
    void _ThrowNullPointerException_WriterIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var writer = (BufferedWriter) null;
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.write(writer)
        );
    }

    /**
     * Verifies that the {@link HelloWorld#write(BufferedWriter)} method invokes
     * {@link HelloWorld#write(Writer)} method with {@code writer}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("should invoke <write((Writer) write)>")
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        Mockito.doAnswer(AdditionalAnswers.returnsFirstArg())
                .when(service)
                .write(ArgumentMatchers.any(Writer.class));
        final var writer = Mockito.mock(BufferedWriter.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.write(writer);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(service, Mockito.times(1)).write((Writer) writer);
        Assertions.assertSame(writer, result);
    }
}
