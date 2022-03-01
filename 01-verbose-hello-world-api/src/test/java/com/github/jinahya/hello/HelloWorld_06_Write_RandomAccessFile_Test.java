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
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * A class for testing {@link HelloWorld#write(RandomAccessFile)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_06_Write_RandomAccessFile_Arguments_Test
 */
@Slf4j
class HelloWorld_06_Write_RandomAccessFile_Test
        extends HelloWorldTest {

    /**
     * Asserts {@link HelloWorld#write(RandomAccessFile)} method invokes {@link
     * HelloWorld#set(byte[])} method with an array of {@value com.github.jinahya.hello.HelloWorld#BYTES}
     * bytes and invokes {@link RandomAccessFile#write(byte[])} on {@code file} with the array.
     *
     * @param tempDir a temporary directory to test with.
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("write(file)"
                 + " invokes set(array[12]) method"
                 + " and invokes file.write(array)")
    @Test
    void write_InvokeSetArrayWriteArrayToFile_(@TempDir File tempDir) throws IOException {
        var file = Mockito.mock(RandomAccessFile.class);
        helloWorld().write(file);
        Mockito.verify(helloWorld(), Mockito.times(1))
                .set(arrayCaptor().capture());
        var array = arrayCaptor().getValue();
        Assertions.assertNotNull(array);
        Assertions.assertEquals(HelloWorld.BYTES, array.length);
        Mockito.verify(file, Mockito.times(1))
                .write(ArgumentMatchers.same(array));
    }

    /**
     * Asserts {@link HelloWorld#write(RandomAccessFile)} method returns the {@code file} argument.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("write(file) returns file")
    @Test
    void write_ReturnFile_() throws IOException {
        var expected = Mockito.mock(RandomAccessFile.class);
        var actual = helloWorld().write(expected);
        Assertions.assertSame(expected, actual);
    }
}
