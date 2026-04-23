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
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import com.github.jinahya.hello.api.畵蛇添足;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * A class for testing {@link HelloWorld#write(OutputStream) write(stream)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(stream)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Write_OutputStream_Test
        extends HelloWorldTest {

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
        Mockito.doAnswer(i -> i.getArgument(0))
                .when(service)
                .set(ArgumentMatchers.any(byte[].class));
        final var stream = Mockito.mock(OutputStream.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.write(stream);
        // ------------------------------------------------------------------------------------ then
        final var array = HelloWorldTestUtils.set_array12_invoked_once(service);
//        Mockito.verify(stream, Mockito.times(1)).write(array);
        Assertions.assertSame(stream, result);
    }

    @畵蛇添足("testing with an existing file doesn't add any extra value")
    @DisplayName("<file>'s length should be increased by <12>")
    @Test
    void _添足_畵蛇(@TempDir final File dir) throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var decoded = "hello, world";
        final var encoded = decoded.getBytes(StandardCharsets.US_ASCII);
        Mockito.doAnswer(i -> {
            final var stream = i.getArgument(0, OutputStream.class);
            stream.write(encoded);
            return stream;
        }).when(service).write(ArgumentMatchers.<OutputStream>any());
        final var file = File.createTempFile("tmp", null, dir);
        // ------------------------------------------------------------------------------------ when
        try (var stream = new FileOutputStream(file)) {
            final var result = service.write(stream);
            assert result == stream;
            stream.flush();
        }
        // ------------------------------------------------------------------------------------ then
        try (var stream = new FileInputStream(file)) {
            final var b = new byte[HelloWorld.BYTES];
            // what a classic loop for reading fully
            for (int r, o = 0; o < b.length; ) {
                if ((r = stream.read(b, o, b.length - o)) == -1) {
                    throw new EOFException("unexpected eof");
                }
                o += r;
            }
            log.debug("decoded: {}", new String(b, StandardCharsets.US_ASCII));
        }
    }
}
