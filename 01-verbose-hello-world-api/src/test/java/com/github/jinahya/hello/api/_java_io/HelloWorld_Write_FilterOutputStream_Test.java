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
import java.nio.charset.*;

/**
 * A class for testing {@link HelloWorld#write(FilterOutputStream)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(stream)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Write_FilterOutputStream_Test
        extends HelloWorld__Test {

    /**
     * Verifies that the {@link HelloWorld#write(FilterOutputStream)} method throws a
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
        final var stream = (FilterOutputStream) null;
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.write(stream)
        );
    }

    /**
     * Asserts {@link HelloWorld#write(FilterOutputStream)} method invokes
     * {@link HelloWorld#write(OutputStream)} method with {@code stream}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("""
            should invoke <write((OutputStream) stream)>"""
    )
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        Mockito.doAnswer(AdditionalAnswers.returnsFirstArg())
                .when(service)
                .write(ArgumentMatchers.<OutputStream>notNull());
        final var stream = Mockito.mock(FilterOutputStream.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.write(stream);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(service, Mockito.times(1)).<OutputStream>write(stream);
        Assertions.assertSame(stream, result);
    }

    @畵蛇添足
    @Test
    void _添足_畵蛇() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        Mockito.doAnswer(i -> {
                    final var stream = i.getArgument(0, OutputStream.class);
                    stream.write(HelloWorld__TestUtils.hello_world_byte_array());
                    return stream;
                })
                .when(service)
                .write(ArgumentMatchers.<OutputStream>notNull());
        final var stream = Mockito.spy(new FilterOutputStream(new ByteArrayOutputStream()) {
            @Override
            public void write(final byte[] b) throws IOException {
                log.debug("write('{}')", new String(b, StandardCharsets.US_ASCII));
                super.write(b);
            }
        });
        // ------------------------------------------------------------------------------------ when
        service.write(stream);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(stream, Mockito.times(1))
                .write(ArgumentMatchers.eq(HelloWorld__TestUtils.hello_world_byte_array()));
    }
}
