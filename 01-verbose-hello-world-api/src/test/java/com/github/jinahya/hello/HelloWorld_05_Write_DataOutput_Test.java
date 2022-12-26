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
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.io.File.createTempFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * A class for testing {@link HelloWorld#write(DataOutput) write(data)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_05_Write_DataOutput_Arguments_Test
 */
@DisplayName("write(data)")
@Slf4j
class HelloWorld_05_Write_DataOutput_Test extends HelloWorldTest {

    /**
     * Stubs {@link HelloWorld#set(byte[]) set(array)} method to return the {@code array} argument.
     */
    @BeforeEach
    void stub_ReturnArray_SetArray() {
        doAnswer(i -> i.getArgument(0))
                .when(service())
                .set(any());
    }

    /**
     * Asserts {@link HelloWorld#write(DataOutput) write(data)} method invokes
     * {@link HelloWorld#set(byte[]) set(array)} method with an array of {@value HelloWorld#BYTES}
     * bytes, and writes the array to specified data output.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("invokes set(array[12])"
                 + ", invokes data.write(array)")
    @Test
    void _InvokeSetArrayWriteArrayToData_() throws IOException {
        // GIVEN
        var service = service();
        var data = mock(DataOutput.class);                      // <1>
        // WHEN
        service.write(data);                                    // <2>
        // THEN: once, set(array[12]) invoked
        verify(service, times(1)).set(arrayCaptor().capture()); // <3>
        var array = arrayCaptor().getValue();
        assertEquals(BYTES, array.length);                      // <4>
        // THEN: only, data.write(array) invoked
        // TODO: Verify data.write(array) invoked
    }

    /**
     * Asserts {@link HelloWorld#write(DataOutput) write(data)} method returns the {@code data}
     * argument.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("returns data")
    @Test
    void _ReturnData_() throws IOException {
        // GIVEN
        var service = service();
        var data = mock(DataOutput.class);
        // WHEN
        var actual = service.write(data);
        // THEN
        assertSame(data, actual);
    }

    /**
     * Asserts, redundantly, {@link HelloWorld#write(DataOutput) write(data)} method writes
     * {@value HelloWorld#BYTES} bytes to given {@code data} argument.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("writes 12 bytes")
    @Test
    @畵蛇添足
    void _Write12Bytes_() throws IOException {
        // GIVEN
        var service = service();
        try (var baos = new ByteArrayOutputStream();
             var dos = new DataOutputStream(baos)) {
            // WHEN
            service.write((DataOutput) dos);
            dos.flush();
            // THEN: baos.size() is equals to HelloWorld.BYTES
            // TODO: Verify baos.size() is equal to BYTES
        }
    }

    /**
     * Asserts, redundantly, {@link HelloWorld#write(DataOutput) write(data)} method writes
     * {@value HelloWorld#BYTES} bytes to given {@code data} arguments.
     *
     * @param tempDir a temporary directory to test with.
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("writes 12 bytes to a file")
    @Test
    @畵蛇添足
    void _Write12Bytes_(@TempDir File tempDir) throws IOException {
        // GIVEN
        var service = service();
        var file = createTempFile("tmp", null, tempDir);
        // TODO: write some to file
        var length = file.length();
        try (var fos = new FileOutputStream(file, true);
             var dos = new DataOutputStream(fos)) {
            // WHEN
            service.write((DataOutput) dos);
            dos.flush();
        }
        // THEN: file.length() is equal to (length + HelloWorld.BYTES)
        // TODO: Verify file.length() is equal to (length + BYTES)
    }
}
