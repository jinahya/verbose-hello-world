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
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.io.DataOutput;
import java.io.File;
import java.io.IOException;

/**
 * A class for testing {@link HelloWorld#write(DataOutput)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_05_Write_DataOutput_Arguments_Test
 */
@Slf4j
class HelloWorld_05_Write_DataOutput_Test
        extends HelloWorldTest {

    /**
     * Asserts {@link HelloWorld#write(DataOutput) write(data)} method invokes
     * {@link HelloWorld#set(byte[]) set(array)} method with an array of {@link
     * HelloWorld#BYTES} bytes and writes the array to specified data output.
     *
     * @param tempDir a temporary directory to test with
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("write(data) invokes set(byte[BYTES])"
                 + " and writes the array to data")
    @Test
    void write_InvokeSetArrayWriteArrayToData_(@TempDir final File tempDir)
            throws IOException {
        final DataOutput data = Mockito.mock(DataOutput.class);  // <1>
        helloWorld().write(data);                                // <2>
        Mockito.verify(helloWorld(), Mockito.times(1))           // <3>
                .set(arrayCaptor().capture());
        final byte[] array = arrayCaptor().getValue();           // <4>
        Assertions.assertEquals(HelloWorld.BYTES, array.length); // <5>
        // TODO: Implement!
    }

    /**
     * Asserts {@link HelloWorld#write(DataOutput) write(data)} method returns
     * the {@code data} argument.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("write(data) returns data")
    @Test
    void write_ReturnData_() throws IOException {
        final DataOutput expected = Mockito.mock(DataOutput.class);
        final DataOutput actual = helloWorld().write(expected);
        Assertions.assertSame(expected, actual);
    }
}
