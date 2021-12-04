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
import org.mockito.Mockito;

import java.io.IOException;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * A class for testing {@link HelloWorld#write(OutputStream)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorld_02_Write_OutputStream_Test extends HelloWorldTest {

    /**
     * Asserts {@link HelloWorld#write(OutputStream) write(stream)} method throws a {@link NullPointerException} when
     * the {@code stream} argument is {@code null}.
     */
    @DisplayName("write((OutputStream) null) throws NullPointerException")
    @Test
    void write_ThrowNullPointerException_StreamIsNull() {
        assertThrows(NullPointerException.class, () -> helloWorld().write((OutputStream) null));
    }

    /**
     * Asserts {@link HelloWorld#write(OutputStream)} method invokes {@link HelloWorld#set(byte[])} method with an array
     * of {@value com.github.jinahya.hello.HelloWorld#BYTES} bytes and invokes {@link OutputStream#write(byte[])} method
     * on {@code stream} with the array.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("write(stream) invokes set(byte[BYTES]) and writes the array to the stream")
    @Test
    void write_InvokeSetArrayAndWriteArrayToStream_() throws IOException {
    }

    /**
     * Asserts {@link HelloWorld#write(OutputStream)} method returns given {@code stream} argument.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("write(stream) returns stream")
    @Test
    void write_ReturnStream_() throws IOException {
        final OutputStream expected = Mockito.mock(OutputStream.class);
        final OutputStream actual = helloWorld().write(expected);
        // TODO: Implement!
    }
}
