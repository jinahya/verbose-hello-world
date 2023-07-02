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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.lang.Thread.currentThread;
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
    @org.junit.jupiter.api.BeforeEach
    void _ReturnArray_SetArray() {
        doAnswer(i -> i.getArgument(0)) // <1>
                .when(serviceInstance())        // <2>
                .set(any());            // <3>
    }

    /**
     * Asserts {@link HelloWorld#write(OutputStream) write(stream)} method invokes
     * {@link HelloWorld#set(byte[]) set(array)} method with an array of {@value HelloWorld#BYTES}
     * bytes, and invokes {@link OutputStream#write(byte[])} method on {@code stream} with the
     * {@code array}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("invokes set(array[12])"
                 + ", invokes stream.write(array)")
    @Test
    void _InvokeSetArrayAndWriteArrayToStream_() throws IOException {
        // GIVEN
        var service = serviceInstance();
        // TODO: Create a mock object of java.io.OutputStream, say 'stream'
        // WHEN
        // TODO: Invoke service.write(stream)
        // THEN: once, set(array[12]) invoked
        // TODO: Verify set(array[12]) invoked
        // THEN: once, write(array) invoked
        // TODO: Verify stream.write(array) invoked
    }

    /**
     * Asserts {@link HelloWorld#write(OutputStream) write(stream)} method returns given
     * {@code stream} argument.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("returns stream")
    @Test
    void _ReturnStream_() throws IOException {
        // GIVEN
        var service = serviceInstance();
        var stream = mock(OutputStream.class);
        // WHEN
        var actual = service.write(stream);
        // THEN: actual is same as stream
        // TODO: Verify actual is same as stream
    }

    /**
     * Reads {@value HelloWorld#BYTES} bytes from a {@link PipedInputStream} connected to a
     * {@link PipedOutputStream} while invoking {@link HelloWorld#write(OutputStream) write(stream)}
     * method with the {@code PipedOutputStream}.
     *
     * @throws IOException          if an I/O error occurs.
     * @throws InterruptedException if interrupted while executing.
     * @see HelloWorld#write(OutputStream)
     */
    @org.junit.jupiter.api.Disabled("enable when implemented")
    @Test
    void _ReadPipedInputStream_WritePipedOutputStream() throws IOException, InterruptedException {
        var service = serviceInstance();
        try (PipedOutputStream pos = new PipedOutputStream();
             PipedInputStream pis = new PipedInputStream(pos, BYTES)) {
            var thread = new Thread(() -> {
                byte[] b = new byte[BYTES];
                int off = 0;
                while (!currentThread().isInterrupted() && off < b.length) {
                    try {
                        int r = pis.read(b, off, b.length - off);
                        if (r == -1) {
                            throw new EOFException("unexpected eof");
                        }
                        off += r;
                    } catch (IOException ioe) {
                        log.error("failed to read", ioe);
                    }
                }
                assertEquals(BYTES, off);
            });
            thread.start();
            service.write(pos);
            pos.flush();
            thread.join(SECONDS.toMillis(1L));
        }
    }
}
