package com.github.jinahya.hello.api._java_nio_file;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@畵蛇添足
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Append_Path_畵蛇添足_Test
        extends HelloWorldTest {

    @TempDir
    private static Path tempDir;

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void __() throws IOException {
        HelloWorldTestUtils.append_path_appends_hello_world(service());
    }

    @Test
    void _添足_畵蛇() throws Exception {
        // ----------------------------------------------------------------------------------- given
        var service = service();
        Mockito.doAnswer(i -> {
            final var path = i.getArgument(0, Path.class);
            try (var channel = FileChannel.open(path, StandardOpenOption.APPEND)) {
                for (final var b = ByteBuffer.allocate(HelloWorld.BYTES);
                     b.hasRemaining(); ) {
                    final var written = channel.write(b);
                    assert written >= 0;
                }
                channel.force(true);
            }
            return path;
        }).when(service).append(ArgumentMatchers.notNull(Path.class));
        final var path = Files.createTempFile(tempDir, null, null);
        HelloWorldTestUtils.writeSome(path);
        final var size = Files.size(path);
        // ------------------------------------------------------------------------------------ when
        service.append(path);
        // ------------------------------------------------------------------------------------ then
        Assertions.assertEquals(
                size + HelloWorld.BYTES,
                Files.size(path)
        );
    }
}
