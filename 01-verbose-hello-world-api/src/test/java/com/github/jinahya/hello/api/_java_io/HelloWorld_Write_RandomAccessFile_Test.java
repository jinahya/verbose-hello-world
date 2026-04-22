package com.github.jinahya.hello.api._java_io;

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

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import com.github.jinahya.hello.api.畵蛇添足;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
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
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A class for testing {@link HelloWorld#write(RandomAccessFile) write(file)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(file)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({
        "java:S101"
})
class HelloWorld_Write_RandomAccessFile_Test
        extends HelloWorldTest {

    /**
     * Verifies that the
     * {@link HelloWorld#write(RandomAccessFile file) write(RandomAccessFile file)} method throws a
     * {@link NullPointerException} when the {@code file} argument is {@code null}.
     */
    @DisplayName("""
            should throw a <NullPointerException>
            when the <file> argument is <null>"""
    )
    @Test
    void _ThrowNullPointerException_FileIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var file = (RandomAccessFile) null;
        // ------------------------------------------------------------------------------- when/then
        // assert: <service.write(file:null)> throws a <NullPointerException>
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.write(file)
        );
    }

    /**
     * Verifies {@link HelloWorld#write(RandomAccessFile) write(file)} method invokes
     * {@link HelloWorld#set(byte[]) set(array)} method with an array of {@value HelloWorld#BYTES}
     * bytes, and invokes {@link RandomAccessFile#write(byte[])} method on the {@code file} argument
     * with the array.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("should invoke <set(array[12])>, <file.write(array)>, and returns <file>")
    @Test
    void __()
            throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // stub: <service.set(array)> to return the <array>
        Mockito.doAnswer(i -> i.getArgument(0))
                .when(service)
                .set(ArgumentMatchers.any(byte[].class));
        // prepare: a mock object of <RandomAccessFile>
        final var file = Mockito.mock(RandomAccessFile.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.write(file);
        // ------------------------------------------------------------------------------------ then
        // verify: <service.set(byte[12])> invoked, once
        final var array = HelloWorldTestUtils.set_array12_invoked_once(service);
        // verify: <file.write(array)> invoked, once and only
//        Mockito.verify(file, Mockito.times(1)).write(array);
//        Mockito.verifyNoMoreInteractions(file);
        // assert: <result> is same as <file>
        Assertions.assertSame(file, result);
    }

    @畵蛇添足("testing with an existing file doesn't add any extra value")
    @DisplayName("<file>'s length should be increased by <12>")
    @Test
    void _添足_畵蛇(@TempDir final File dir) throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var decoded = "hello, world";
        final var encoded = decoded.getBytes(StandardCharsets.US_ASCII);
        Mockito.doAnswer(i -> {
            final var array = i.getArgument(0, byte[].class);
            System.arraycopy(encoded, 0, array, 0, array.length);
            return array;
        }).when(service).set(ArgumentMatchers.<byte[]>any());
        Mockito.doAnswer(i -> {            final var file = i.getArgument(0, RandomAccessFile.class);
            file.write(service.set(new byte[HelloWorld.BYTES]));
            return file;
        }).when(service).write(ArgumentMatchers.<RandomAccessFile>any());
        final var name = "test.bin";
        final var position = ThreadLocalRandom.current().nextLong(128L);
        // ------------------------------------------------------------------------------------ when
        try (var file = new RandomAccessFile(new File(dir, name), "rw")) {
            file.seek(position);
            service.write(file);
            file.getFD().sync();
        }
        // ------------------------------------------------------------------------------------ then
        try (var file = new RandomAccessFile(new File(dir, name), "r")) {
            file.seek(position);
            final var array = new byte[HelloWorld.BYTES];
            file.readFully(array);
            Assertions.assertEquals(decoded, new String(array, StandardCharsets.US_ASCII));
        }
    }
}
