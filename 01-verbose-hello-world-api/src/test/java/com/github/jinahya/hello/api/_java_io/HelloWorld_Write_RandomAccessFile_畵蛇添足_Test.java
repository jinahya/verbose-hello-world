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

@畵蛇添足
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({
        "java:S101"
})
class HelloWorld_Write_RandomAccessFile_畵蛇添足_Test
        extends HelloWorldTest {

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
        Mockito.doAnswer(i -> {
            final var file = i.getArgument(0, RandomAccessFile.class);
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
