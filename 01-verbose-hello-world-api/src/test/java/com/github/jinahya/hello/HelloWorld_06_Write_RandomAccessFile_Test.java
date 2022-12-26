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

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.io.File.createTempFile;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * A class for testing {@link HelloWorld#write(RandomAccessFile)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_06_Write_RandomAccessFile_Arguments_Test
 */
@DisplayName("write(RandomAccessFile file)")
@Slf4j
class HelloWorld_06_Write_RandomAccessFile_Test extends HelloWorldTest {

    /**
     * Stubs {@link HelloWorld#set(byte[]) set(array)} method to just return the {@code array}
     * argument.
     */
    @DisplayName("[stubbing] set(array) returns array")
    @org.junit.jupiter.api.BeforeEach
    void stub_ReturnArray_SetArray() {
        doAnswer(i -> i.getArgument(0))
                .when(service())
                .set(any());
    }

    /**
     * Asserts {@link HelloWorld#write(RandomAccessFile) write(RandomAccessFile file)} method
     * invokes {@link HelloWorld#set(byte[]) set(array)} method with an array of
     * {@value HelloWorld#BYTES} bytes, and invokes {@link RandomAccessFile#write(byte[])} method on
     * the {@code file} argument with the array.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("invokes set(array[12])"
                 + ", invokes file.write(array)")
    @Test
    void _InvokeSetArrayWriteArrayToFile_() throws IOException {
        // GIVEN
        var service = service();
        var file = mock(RandomAccessFile.class);
        // WHEN
        service.write(file);
        // THEN: once, set(array[12]) invoked
        verify(service, times(1)).set(arrayCaptor().capture());
        var array = arrayCaptor().getValue();
        assertNotNull(array);
        assertEquals(BYTES, array.length);
        // THEN: only, file.write(array) invoked
        // TODO: Verify file.write(array) invoked
    }

    /**
     * Asserts {@link HelloWorld#write(RandomAccessFile) write(RandomAccessFile file)} method
     * returns the {@code file} argument.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("returns file")
    @Test
    void _ReturnFile_() throws IOException {
        // GIVEN
        var service = service();
        var file = mock(RandomAccessFile.class);
        // WHEN
        var actual = service.write(file);
        // THEN
        assertSame(file, actual);
    }

    /**
     * Asserts, redundantly, {@link HelloWorld#write(RandomAccessFile) write(RandomAccessFile file)}
     * method writes {@value HelloWorld#BYTES} bytes to the {@code file}.
     *
     * @param tempDir a temporary directory to test with.
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("writes 12 bytes")
    @Test
    @畵蛇添足
    void _Write12Bytes_(@TempDir File tempDir) throws IOException {
        // GIVEN
        var service = service();
        var tmp = createTempFile("tmp", null, tempDir);
        var pos = current().nextLong(1024);
        try (var file = new RandomAccessFile(tmp, "rw")) {
            file.seek(pos);
            // WHEN
            service.write(file);
            file.getFD().sync();
        }
        // THEN: tmp.length() is equal to (pos + 12)
        // TODO: Verify tmp.length() is equal to (pos + BYTES)
    }
}
