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
import org.mockito.*;

import java.io.*;

/**
 * A class for testing {@link HelloWorld#write(PipedWriter) write(writer)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/io/PipedWriter.html">java.io.PipedWriter</a>
 */
@DisplayName("write(BufferedWriter)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Write_PipedWriter_Test
        extends HelloWorld__Test {

    /**
     * Verifies that the {@link HelloWorld#write(PipedWriter)} method throws a
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
        final var writer = (PipedWriter) null;
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.write(writer)
        );
    }

    /**
     * Verifies that the {@link HelloWorld#write(PipedWriter)} method invokes
     * {@link HelloWorld#write(Writer)} method with {@code writer}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("should invoke <write((Writer) writer)>")
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorld__TestUtils.write_writer_writes_12_chars(service());
        final var writer = Mockito.mock(PipedWriter.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.write(writer);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(service, Mockito.times(1)).write((Writer) writer);
        Assertions.assertSame(writer, result);
    }

    @Test
    void _添足_畵蛇() throws IOException, InterruptedException {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorld__TestUtils.write_writer_writes_12_chars(service());
        {
            try (var reader = Mockito.spy(new PipedReader(HelloWorld.BYTES));
                 var writer = new PipedWriter(reader)) {
                // ---------------------------------------------------------------------------- when
                service.write(writer).flush();
                // ---------------------------------------------------------------------------- then
                final var array = new char[HelloWorld.BYTES];
                final var read = reader.read(array);
                Assertions.assertEquals(HelloWorld.BYTES, read);
            }
        }
        {
            try (final var reader = Mockito.spy(new PipedReader())) {
                final var thread = Thread.ofPlatform().start(() -> {
                    try {
                        for (int c; (c = reader.read()) != -1; ) {
                        }
                    } catch (final IOException ioe) {
                        throw new RuntimeException(ioe);
                    }
                });
                try (final var writer = new PipedWriter(reader)) {
                    // ------------------------------------------------------------------------ when
                    service.write(writer).flush();
                }
                thread.join();
            }
        }
    }
}
