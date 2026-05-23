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

import com.github.jinahya.hello.api.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.*;
import org.mockito.*;

import java.nio.*;
import java.nio.channels.*;
import java.nio.file.*;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Append_Path__Test extends HelloWorldTest {

    @Test
    void __(final @TempDir Path tempDir) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorldTestUtils.append_path_appends_hello_world(service());
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
