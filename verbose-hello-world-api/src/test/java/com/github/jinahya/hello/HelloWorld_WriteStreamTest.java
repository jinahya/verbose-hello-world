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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import java.io.IOException;
import java.io.OutputStream;

import static org.mockito.quality.Strictness.LENIENT;

/**
 * A class for unit-testing {@link HelloWorld} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@MockitoSettings(strictness = LENIENT)
@ExtendWith({MockitoExtension.class})
@Slf4j
class HelloWorld_WriteStreamTest extends AbstractHelloWorldTest {

    /**
     * Asserts {@link HelloWorld#write(OutputStream) write(stream)} method throws a {@link NullPointerException} when
     * {@code stream} argument is {@code null}.
     */
    @DisplayName("write(stream) throws NullPointerException when stream is null")
    @Test
    void writeStream_NullPointerException_StreamIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> helloWorld.write((OutputStream) null));
    }

    /**
     * Asserts {@link HelloWorld#write(OutputStream)} method invokes {@link HelloWorld#set(byte[])} method with an array
     * of {@value com.github.jinahya.hello.HelloWorld#BYTES} bytes and writes the array to given {@code stream}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("write(stream) invokes set(byte[12]) and writes the array to the stream")
    @Test
    void writeStream_InvokeSetArrayAndWriteArrayToStream_() throws IOException {
    }

    /**
     * Asserts {@link HelloWorld#write(OutputStream)} method returns given {@code stream}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("write(stream) returns the stream")
    @Test
    void writeStream_ReturnStream_() throws IOException {
    }
}
