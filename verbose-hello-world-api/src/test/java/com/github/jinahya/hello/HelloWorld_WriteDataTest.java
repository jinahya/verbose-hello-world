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

import java.io.DataOutput;
import java.io.IOException;

import static org.mockito.quality.Strictness.LENIENT;

/**
 * A class for unit-testing {@link HelloWorld#write(DataOutput)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@MockitoSettings(strictness = LENIENT)
@ExtendWith({MockitoExtension.class})
@Slf4j
class HelloWorld_WriteDataTest extends AbstractHelloWorldTest {

    /**
     * Asserts {@link HelloWorld#write(DataOutput)} method throws a {@link NullPointerException} when {@code data}
     * argument is {@code null}.
     */
    @DisplayName("write(data) method throws NullPointerException when data is null")
    @Test
    void writeData_NullPointerException_DataIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> helloWorld.write((DataOutput) null));
    }

    /**
     * Asserts {@link HelloWorld#write(DataOutput)} method invokes {@link HelloWorld#set(byte[])} method with an array
     * of {@value com.github.jinahya.hello.HelloWorld#BYTES} bytes and writes the array to specified data output.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("write(data) invokes set(array) and writes the array to data")
    @Test
    void writeData_InvokeSetArrayWriteArrayToData_() throws IOException {
    }

    /**
     * Asserts {@link HelloWorld#write(DataOutput)} method returns given data output.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("write(data) returns data")
    @Test
    void writeData_ReturnData_() throws IOException {
    }
}
