package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-srv-common
 * %%
 * Copyright (C) 2018 - 2022 Jinahya, Inc.
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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ThreadLocalRandom;

import static com.github.jinahya.hello.HelloWorldServerUtils.loadHelloWorld;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * A class for testing {@link HelloWorldServerUtils} class.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorldServerUtilsTest {

    /**
     * Asserts {@link HelloWorldServerUtils#loadHelloWorld()} returns non-null instance.
     */
    @DisplayName("load() return non-null")
    @Test
    void load_NotNull_() {
        var helloWorld = loadHelloWorld();
        assertNotNull(helloWorld);
    }

    @Nested
    class PortTest {

        @Test
        void writePort(@TempDir Path tempDir) throws IOException {
            var dir = Files.createTempDirectory(tempDir, null);
            var port = ThreadLocalRandom.current().nextInt(1, 65536);
            HelloWorldServerUtils.writePortNumber(dir, port);
            var resolved = dir.resolve("port.txt");
            Assertions.assertTrue(Files.isRegularFile(resolved));
            Assertions.assertEquals(Short.BYTES, Files.size(resolved));
        }

        @Test
        void writeAndReadPort(@TempDir Path tempDir)
                throws IOException, InterruptedException {
            Path dir = Files.createTempDirectory(tempDir, null);
            int expected = ThreadLocalRandom.current().nextInt(1, 65536);
            var thread = new Thread(() -> {
                try {
                    var actual = HelloWorldServerUtils.readPortNumber(dir);
                    Assertions.assertEquals(expected, actual);
                } catch (IOException | InterruptedException e) {
                    log.error("failed to read port", e);
                }
            });
            thread.start();
            HelloWorldServerUtils.writePortNumber(dir, expected);
            thread.join();
        }
    }
}
