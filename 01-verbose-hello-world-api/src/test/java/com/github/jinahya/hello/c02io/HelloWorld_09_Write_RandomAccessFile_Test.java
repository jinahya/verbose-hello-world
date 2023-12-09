package com.github.jinahya.hello.c02io;

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

import com.github.jinahya.hello.HelloWorld;
import com.github.jinahya.hello._HelloWorldTest;
import com.github.jinahya.hello.畵蛇添足;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * A class for testing {@link HelloWorld#write(RandomAccessFile)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_09_Write_RandomAccessFile_Arguments_Test
 */
@DisplayName("write(RandomAccessFile file)")
@Slf4j
class HelloWorld_09_Write_RandomAccessFile_Test
        extends _HelloWorldTest {

    @BeforeEach
    void _beforeEach() {
        _stub_SetArray_ToReturnTheArray();
    }

    /**
     * Asserts {@link HelloWorld#write(RandomAccessFile) write(file)} method invokes
     * {@link HelloWorld#set(byte[]) set(array)} method with an array of {@value HelloWorld#BYTES}
     * bytes, and invokes {@link RandomAccessFile#write(byte[])} method on the {@code file} argument
     * with the array.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("(file) -> file.write(set(array[12]))")
    @Test
    void _InvokeSetArrayWriteArrayToFile_()
            throws IOException {
        // ----------------------------------------------------------------------------------- given
        var service = serviceInstance();
        var file = mock(RandomAccessFile.class);
        // ------------------------------------------------------------------------------------ when
        var result = service.write(file);
        // ------------------------------------------------------------------------------------ then
        verify(service, times(1)).set(arrayCaptor().capture());
        var array = arrayCaptor().getValue();
        assertNotNull(array);
        assertEquals(BYTES, array.length);
        // TODO: Verify file.write(array) invoked, once and only
        assertSame(result, file);
    }

    /**
     * Asserts, redundantly, {@link HelloWorld#write(RandomAccessFile) write(RandomAccessFile file)}
     * method writes {@value HelloWorld#BYTES} bytes to the {@code file}.
     *
     * @param tempDir a temporary directory to test with.
     * @throws IOException if an I/O error occurs.
     */
    @org.junit.jupiter.api.Disabled("not implemented yet")
    // TODO: Remove when implemented
    @DisplayName("(file) -> writes 12 bytes")
    @Test
    @畵蛇添足
    void _Write12Bytes_(@TempDir File tempDir)
            throws IOException {
        // ----------------------------------------------------------------------------------- given
        var service = serviceInstance();
        var tmp = createTempFile("tmp", null, tempDir);
        var pos = current().nextLong(1024);
        // ------------------------------------------------------------------------------------ when
        try (var file = new RandomAccessFile(tmp, "rw")) {
            file.seek(pos);
            var result = service.write(file);
            file.getFD().sync();
        }
        // ------------------------------------------------------------------------------------ then
        assertEquals(pos + BYTES, tmp.length());
    }
}
