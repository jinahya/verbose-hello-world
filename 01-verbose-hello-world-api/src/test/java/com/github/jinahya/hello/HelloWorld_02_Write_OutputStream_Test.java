package com.github.jinahya.hello;

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

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.UncheckedIOException;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

/**
 * A class for testing {@link HelloWorld#write(OutputStream) write(stream)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_02_Write_OutputStream_Arguments_Test
 */
@DisplayName("write(stream)")
@Slf4j
class HelloWorld_02_Write_OutputStream_Test extends _HelloWorldTest {

    /**
     * Stubs {@link HelloWorld#set(byte[]) set(array)} method to just return the {@code array}
     * argument.
     */
    @BeforeEach
    void beforeEach() {
        doAnswer(i -> i.getArgument(0))  // <1>
                .when(serviceInstance()) // <2>
                .set(any());             // <3>
    }

    /**
     * Asserts {@link HelloWorld#write(OutputStream) write(stream)} method invokes
     * {@link HelloWorld#set(byte[]) set(array)} method with an array of {@value HelloWorld#BYTES}
     * bytes, and invokes {@link OutputStream#write(byte[])} method on {@code stream} with the
     * {@code array}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("(stream) -> stream.write(set(array))")
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- GIVEN
        var service = serviceInstance();
        var stream = mock(OutputStream.class);
        // ------------------------------------------------------------------------------------ WHEN
        var result = service.write(stream);
        // ------------------------------------------------------------------------------------ THEN
        // TODO: Verify, service.set(array[12]) invoked, once
        // TODO: Verify, stream.write(array) invoked, once
        // TODO: Assert, result is same as stream
    }

    @org.junit.jupiter.api.Disabled("not implemented yet") // TODO: remove when implemented
    @Test
    void _ReadByteArrayInputStream_WriteByteArrayOutputStream() throws IOException {
        var service = serviceInstance();
        var outputStream = service.write(new ByteArrayOutputStream(BYTES));
        var inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        var array = new byte[BYTES];
        var read = inputStream.read(array);
        assertEquals(BYTES, read);
    }

    @org.junit.jupiter.api.Disabled("not implemented yet") // TODO: remove when implemented
    @Test
    void _ReadPipedInputStream_WritePipedOutputStream() throws IOException, InterruptedException {
        var service = serviceInstance();
        try (var pos = new PipedOutputStream();
             var pis = new PipedInputStream(pos, 1)) {
            var thread = new Thread(() -> {
                try {
                    var array = pis.readNBytes(BYTES);
                } catch (final IOException ioe) {
                    throw new UncheckedIOException("failed to read", ioe);
                }
            });
            thread.start();
            service.write(pos);
            pos.flush();
            thread.join(SECONDS.toMillis(1L));
        }
    }
}
