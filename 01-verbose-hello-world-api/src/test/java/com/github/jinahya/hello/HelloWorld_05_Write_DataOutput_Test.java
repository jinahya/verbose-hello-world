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

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A class for testing {@link HelloWorld#write(DataOutput)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_05_Write_DataOutput_Arguments_Test
 */
@Slf4j
class HelloWorld_05_Write_DataOutput_Test extends HelloWorldTest {

    /**
     * Asserts {@link HelloWorld#write(DataOutput) write(data)} method invokes
     * {@link HelloWorld#set(byte[])} method with an array of
     * {@value com.github.jinahya.hello.HelloWorld#BYTES} bytes and writes the array to specified
     * data output.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("write(data)"
                 + " invokes set(array[12])"
                 + " and writes the array to data")
    @Test
    void write_InvokeSetArrayWriteArrayToData_() throws IOException {
        var data = Mockito.mock(DataOutput.class);               // <1>
        helloWorld().write(data);                                // <2>
        Mockito.verify(helloWorld(), Mockito.times(1))           // <3>
                .set(arrayCaptor().capture());
        var array = arrayCaptor().getValue();                    // <4>
        Assertions.assertNotNull(array);                         // <5>
        Assertions.assertEquals(HelloWorld.BYTES, array.length); // <6>
        // TODO: Verify helloWorld() invoked data.write(array) once.
    }

    /**
     * Asserts {@link HelloWorld#write(DataOutput) write(data)} method returns the {@code data}
     * argument.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("write(data) returns data")
    @Test
    void write_ReturnData_() throws IOException {
        var expected = Mockito.mock(DataOutput.class);
        var actual = helloWorld().write(expected);
        Assertions.assertSame(expected, actual);
    }

    /**
     * Asserts, redundantly, {@link HelloWorld#write(DataOutput) write(data)} method writes
     * {@value com.github.jinahya.hello.HelloWorld#BYTES} bytes to given {@code data} argument.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("write(data) writes 12 bytes")
    @Test
    void write_Appends12Bytes_() throws IOException {
        try (var baos = new ByteArrayOutputStream();
             var dos = new DataOutputStream(baos)) {
            baos.write(new byte[ThreadLocalRandom.current().nextInt(2)]); // byte[[0..1]]
            baos.flush();
            var size = baos.size();
            helloWorld().write((DataOutput) dos);
            dos.flush();
            var expected = size + HelloWorld.BYTES;
            var actual = baos.size();
            // TODO: Assert that expected and actual are equal.
        }
    }

    /**
     * Asserts, redundantly, {@link HelloWorld#write(DataOutput) write(data)} method writes
     * {@value com.github.jinahya.hello.HelloWorld#BYTES} bytes to given {@code data} arguments.
     *
     * @param tempDir a temporary directory to test with
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("write(data) writes 12 bytes")
    @Test
    void write_Appends12Bytes_(@TempDir File tempDir) throws IOException {
        var file = File.createTempFile("tmp", null, tempDir);
        try (var fos = new FileOutputStream(file);
             var dos = new DataOutputStream(fos)) {
            fos.write(new byte[ThreadLocalRandom.current().nextInt(2)]); // byte[[0..1]]
            fos.flush();
            var length = file.length();
            helloWorld().write((DataOutput) dos);
            dos.flush();
            var expected = length + HelloWorld.BYTES;
            var actual = file.length();
            // TODO: Assert that expected and actual are equal.
        }
    }
}
