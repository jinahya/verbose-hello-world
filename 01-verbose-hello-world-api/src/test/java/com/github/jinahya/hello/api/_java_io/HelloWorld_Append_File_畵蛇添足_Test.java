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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

@畵蛇添足
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Append_File_畵蛇添足_Test
        extends HelloWorldTest {

    @TempDir
    private static File tempDir;

    @BeforeEach
    void __() throws IOException {
        HelloWorldTestUtils.append_file_appends_hello_world(service());
    }

    @DisplayName("(directory)FileNotFoundException")
    @Test
    void __Directory() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var directory = new File(tempDir, Long.toString(System.nanoTime()));
        assert directory.mkdir();
        assert directory.exists();
        assert !directory.isFile();
        assert directory.isDirectory();
        final var length = directory.length(); // unspecified
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(FileNotFoundException.class, () -> {
            service.append(directory);
        });
    }

    @DisplayName("(existing)")
    @Test
    void __Existing() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var file = File.createTempFile("tmp", null, tempDir);
        assert file.exists();
        assert file.isFile();
        assert !file.isDirectory();
        HelloWorldTestUtils.writeSome(file);
        final var length = file.length();
        // ------------------------------------------------------------------------------------ when
        service.append(file);
        // ------------------------------------------------------------------------------------ then
        Assertions.assertEquals(length + HelloWorld.BYTES, file.length());
    }

    @DisplayName("(!existing)")
    @Test
    void __NotExisting() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var file = new File(tempDir, Long.toString(System.nanoTime()));
        assert !file.exists();
        assert !file.isFile();
        assert !file.isDirectory();
        final var length = file.length();
        assert length == 0;
        // ------------------------------------------------------------------------------------ when
        service.append(file);
        // ------------------------------------------------------------------------------------ then
        Assertions.assertTrue(file.isFile());
        Assertions.assertEquals(HelloWorld.BYTES, file.length());
    }
}
