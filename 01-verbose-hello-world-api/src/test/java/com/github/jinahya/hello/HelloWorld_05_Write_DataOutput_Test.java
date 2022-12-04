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
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static java.io.File.createTempFile;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * A class for testing {@link HelloWorld#write(DataOutput)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_05_Write_DataOutput_Arguments_Test
 */
@Slf4j
class HelloWorld_05_Write_DataOutput_Test extends HelloWorldTest {

    /**
     * Asserts {@link HelloWorld#write(DataOutput) write(DataOutput output)} method invokes
     * {@link HelloWorld#set(byte[]) set(array)} method with an array of
     * {@value com.github.jinahya.hello.HelloWorld#BYTES} bytes, and writes the array to specified
     * data output.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("write(data)"
                 + " invokes set(array[12])"
                 + ", and writes the array to data")
    @Test
    void write_InvokeSetArrayWriteArrayToData_() throws IOException {
        var service = helloWorld();
        var output = mock(DataOutput.class);                         // <1>
        service.write(output);                                       // <2>
        verify(helloWorld(), times(1)).set(arrayCaptor().capture()); // <3>
        var array = arrayCaptor().getValue();                        // <4>
        assertNotNull(array);                                        // <5>
        assertEquals(HelloWorld.BYTES, array.length);                // <6>
        // TODO: Verify the service invoked output.write(array) once.
    }

    /**
     * Asserts {@link HelloWorld#write(DataOutput) write(DataOutput output)} method returns the
     * {@code output} argument.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("write(DataOutput output) returns output")
    @Test
    void write_ReturnData_() throws IOException {
        var service = helloWorld();
        var output = mock(DataOutput.class);
        var actual = helloWorld().write(output);
        assertSame(output, actual);
    }

    /**
     * Asserts, redundantly, {@link HelloWorld#write(DataOutput) write(data)} method writes
     * {@value com.github.jinahya.hello.HelloWorld#BYTES} bytes to given {@code data} argument.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("write(DataOutput) writes 12 bytes")
    @Test
    @畵蛇添足
    void write_Appends12Bytes_() throws IOException {
        var service = helloWorld();
        try (var baos = new ByteArrayOutputStream();
             var dos = new DataOutputStream(baos)) {
            baos.write(new byte[current().nextInt(2)]); // byte[[0..1]]
            baos.flush();
            var size = baos.size();
            service.write((DataOutput) dos);
            dos.flush();
            var expected = size + HelloWorld.BYTES;
            var actual = baos.size();
            // TODO: Assert that expected and actual are equal.
        }
    }

    /**
     * Asserts, redundantly, {@link HelloWorld#write(DataOutput) write(DataOutput data)} method
     * writes {@value com.github.jinahya.hello.HelloWorld#BYTES} bytes to given {@code data}
     * arguments.
     *
     * @param tempDir a temporary directory to test with
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("write(data) writes 12 bytes")
    @Test
    @畵蛇添足
    void write_Appends12Bytes_(@TempDir File tempDir) throws IOException {
        var service = helloWorld();
        var file = createTempFile("tmp", null, tempDir);
        try (var fos = new FileOutputStream(file);
             var dos = new DataOutputStream(fos)) {
            fos.write(new byte[current().nextInt(2)]); // byte[[0..1]]
            fos.flush();
            var length = file.length();
            service.write((DataOutput) dos);
            dos.flush();
            var expected = length + HelloWorld.BYTES;
            var actual = file.length();
            // TODO: Assert that expected and actual are equal.
        }
    }
}
