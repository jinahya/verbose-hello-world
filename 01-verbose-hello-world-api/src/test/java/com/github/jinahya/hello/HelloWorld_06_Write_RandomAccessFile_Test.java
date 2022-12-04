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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import static java.io.File.createTempFile;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * A class for testing {@link HelloWorld#write(RandomAccessFile)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_06_Write_RandomAccessFile_Arguments_Test
 */
@Slf4j
class HelloWorld_06_Write_RandomAccessFile_Test extends HelloWorldTest {

    /**
     * Asserts {@link HelloWorld#write(RandomAccessFile) write((RandomAccessFile) file)} method
     * invokes {@link HelloWorld#set(byte[]) set(byte[])} method with an array of
     * {@value com.github.jinahya.hello.HelloWorld#BYTES} bytes, and invokes
     * {@link RandomAccessFile#write(byte[])} method on the {@code file} argument with the array.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("write((RandomAccessFile) file)"
                 + " invokes set(array[12])"
                 + ", and invokes file.write(array)")
    @Test
    void write_InvokeSetArrayWriteArrayToFile_() throws IOException {
        var service = helloWorld();
        var file = mock(RandomAccessFile.class);
        service.write(file);
        verify(helloWorld(), times(1)).set(arrayCaptor().capture());
        var array = arrayCaptor().getValue();
        assertNotNull(array);
        assertEquals(HelloWorld.BYTES, array.length);
        // TODO: Verify the service invoked file.write(array)
    }

    /**
     * Asserts {@link HelloWorld#write(RandomAccessFile) write((RandomAccessFile) file)} method
     * returns the {@code file} argument.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("write((RandomAccessFile) file) returns file")
    @Test
    void write_ReturnFile_() throws IOException {
        var service = helloWorld();
        var file = mock(RandomAccessFile.class);
        var actual = service.write(file);
        assertSame(file, actual);
    }

    /**
     * Asserts, redundantly,
     * {@link HelloWorld#write(RandomAccessFile) write((RandomAccessFile) file)} method writes
     * {@value com.github.jinahya.hello.HelloWorld#BYTES} bytes.
     *
     * @param tempDir a temporary directory to test with.
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("write((RandomAccess) file) writes 12 bytes")
    @Test
    @畵蛇添足
    void write_InvokeSetArrayWriteArrayToFile_(@TempDir File tempDir) throws IOException {
        var service = helloWorld();
        var tf = createTempFile("tmp", null, tempDir);
        try (var raf = new RandomAccessFile(tf, "rw")) {
            var pos = current().nextLong(128);
            raf.seek(pos);
            service.write(raf);
            raf.getFD().sync();
            // TODO: Assert tf.length() is equals to (pos + 12)
        }
    }
}
