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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

@畵蛇添足
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Write_DataOutput_畵蛇添足_Test
        extends HelloWorldTest {

    @TempDir
    private static File tempDir;

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void __() throws IOException {
        HelloWorldTestUtils.write_dataoutput_writes_hello_world_bytes(service());
    }

    // ---------------------------------------------------------------------------------------------
    @Nested
    class RandomAccessFileTest {

        @Test
        void __() throws IOException {
            // ------------------------------------------------------------------------------- given
            final var tempFile = File.createTempFile("tmp", "tmp", tempDir);
            final var pos = ThreadLocalRandom.current().nextLong(0L, 128L);
            // -------------------------------------------------------------------------------- when
            try (var file = new RandomAccessFile(tempFile, "rw")) {
                file.seek(pos);
                service().write((DataOutput) file);
                file.getFD().sync();
            }
            // -------------------------------------------------------------------------------- then
            Assertions.assertEquals(pos + HelloWorld.BYTES, tempFile.length());
            try (var file = new RandomAccessFile(tempFile, "r")) {
                file.seek(pos);
                final var b = new byte[HelloWorld.BYTES];
                file.readFully(b);
                log.debug("read: {}", new String(b, StandardCharsets.US_ASCII));
            }
        }
    }
}
