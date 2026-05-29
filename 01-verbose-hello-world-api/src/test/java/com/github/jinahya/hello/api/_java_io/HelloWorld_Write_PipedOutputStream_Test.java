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
 * A class for testing {@link HelloWorld#write(PipedOutputStream) write(stream)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(stream)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Write_PipedOutputStream_Test
        extends HelloWorldTest {

    /**
     * Asserts {@link HelloWorld#write(PipedOutputStream) write(stream)} method invokes
     * {@link HelloWorld#write(OutputStream)} method with {@code writer} and returns the result.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("""
            should invoke <write(writer)>"""
    )
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorldTestUtils.write_stream_writes_12_bytes(service());
        final var stream = Mockito.mock(PipedOutputStream.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.write(stream);
        // ------------------------------------------------------------------------------------ then
        HelloWorldTestUtils.write_stream_invoked_once(service);
        Assertions.assertSame(stream, result);
    }

    @畵蛇添足("testing with an existing file doesn't add any extra value")
    @DisplayName("<file>'s length should be increased by <12>")
    @Test
    void _添足_畵蛇() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorldTestUtils.write_stream_writes_12_bytes(service());
        // -------------------------------------------------------------------------------- buffered
        try (var snk = new PipedInputStream(HelloWorld.BYTES);
             var stream = new PipedOutputStream(snk)) {
            service.write(stream).flush();
            final var array = snk.readNBytes(HelloWorld.BYTES);
            Assertions.assertEquals(HelloWorld.BYTES, array.length);
        }
        // --------------------------------------------------------------------------- concurrent(1)
        try (var snk = new PipedInputStream(1)) {
            final var thread = Thread.ofPlatform().start(() -> {
                try {
                    final var array = snk.readNBytes(HelloWorld.BYTES);
                    Assertions.assertEquals(HelloWorld.BYTES, array.length);
                } catch (final IOException ioe) {
                    throw new RuntimeException(ioe);
                }
            });
            try (var stream = new PipedOutputStream(snk)) {
                service.write(stream).flush();
                if (false) {
                    thread.join();
                }
            }
        }
        // -------------------------------------------------------------------- concurrent(internal)
        try (var snk = new PipedInputStream()) {
            final var thread = Thread.ofPlatform().start(() -> {
                try {
                    final var array = snk.readNBytes(HelloWorld.BYTES);
                    Assertions.assertEquals(HelloWorld.BYTES, array.length);
                } catch (final IOException ioe) {
                    throw new RuntimeException(ioe);
                }
            });
            try (var stream = new PipedOutputStream(snk)) {
                service.write(stream).flush();
                thread.join();
            }
        }
    }
}
