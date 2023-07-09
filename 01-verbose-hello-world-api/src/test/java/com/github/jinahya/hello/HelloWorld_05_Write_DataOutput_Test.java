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
import java.io.OutputStream;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.io.File.createTempFile;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

/**
 * A class for testing {@link HelloWorld#write(DataOutput) write(data)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_05_Write_DataOutput_Arguments_Test
 */
@DisplayName("write(data)")
@Slf4j
class HelloWorld_05_Write_DataOutput_Test extends _HelloWorldTest {

    /**
     * Stubs {@link HelloWorld#set(byte[]) set(array)} method to return the {@code array} argument.
     */
    @DisplayName("set(array[12])array")
    @BeforeEach
    void stub_ReturnArray_SetArray() {
        doAnswer(i -> i.getArgument(0))
                .when(serviceInstance())
                .set(argThat(a -> a != null && a.length == BYTES));
    }

    /**
     * Asserts {@link HelloWorld#write(DataOutput) write(data)} method invokes
     * {@link HelloWorld#set(byte[]) set(array)} method with an array of {@value HelloWorld#BYTES}
     * bytes, writes the array to specified data output, and returns the {@code data}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("(data) -> data.write(set(array[12]))")
    @Test
    void _InvokeSetArrayWriteArrayToData_() throws IOException {
        // ----------------------------------------------------------------------------------- GIVEN
        var service = serviceInstance();
        var data = mock(DataOutput.class);                      // <1>
        // ------------------------------------------------------------------------------------ WHEN
        var result = service.write(data);                       // <2>
        // ------------------------------------------------------------------------------------ THEN
        // TODO: Verify, service.set(array[12]) invoked, once.
        // TODO: Verify, data.write(array) invoked, once.
        // TODO: Verify, no more interactions with data.
        assertSame(data, result);
    }

    /**
     * Asserts, redundantly, {@link HelloWorld#write(DataOutput) write(data)} method writes
     * {@value HelloWorld#BYTES} bytes to given {@code data} argument.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("(data) -> writes 12 bytes")
    @Test
    @畵蛇添足
    void _Write12Bytes_() throws IOException {
        // ----------------------------------------------------------------------------------- GIVEN
        var service = serviceInstance();
        try (var baos = new ByteArrayOutputStream()) {
            if (current().nextBoolean()) {
                // TODO: (Optional) Write some bytes to baos
            }
            var size = baos.size();
            try (var dos = new DataOutputStream(baos)) {
                // ---------------------------------------------------------------------------- WHEN
                var result = service.write((DataOutput) dos);
                dos.flush();
                // --------------------------------------------------------------------------- THEN
                // TODO: Assert, baos.size() is equal to (size + BYTES)
            }
        }
    }

    /**
     * Asserts, redundantly, {@link HelloWorld#write(DataOutput) write(data)} method writes
     * {@value HelloWorld#BYTES} bytes to given {@code data} arguments.
     *
     * @param tempDir a temporary directory to test with.
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("(data) -> writes 12 bytes")
    @Test
    @畵蛇添足
    void _Write12Bytes_(@TempDir File tempDir) throws IOException {
        // ----------------------------------------------------------------------------------- GIVEN
        var service = serviceInstance();
        var file = createTempFile("tmp", null, tempDir);
        if (current().nextBoolean()) {
            // TODO: (optional) Write some bytes to the file
        }
        var length = file.length();
        // ------------------------------------------------------------------------------------ WHEN
        try (var fos = new FileOutputStream(file, true); // append
             var dos = new DataOutputStream(fos)) {
            var result = service.write((DataOutput) dos);
            ((OutputStream) result).flush();
        }
        // ------------------------------------------------------------------------------------ THEN
        // TODO: Assert file.length() is same as (length + BYTES)
    }
}
