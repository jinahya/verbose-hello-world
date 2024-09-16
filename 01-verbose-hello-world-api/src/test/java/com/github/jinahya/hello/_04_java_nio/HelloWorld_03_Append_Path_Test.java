package com.github.jinahya.hello._04_java_nio;

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
import com.github.jinahya.hello.HelloWorldTest;
import com.github.jinahya.hello.畵蛇添足;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * A class for testing {@link HelloWorld#append(Path) append(path)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("append(path)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_03_Append_Path_Test extends HelloWorldTest {

    /**
     * Verifies that the {@link HelloWorld#append(Path) append(path)} method throws a
     * {@link NullPointerException} when the {@code path} argument is {@code null}.
     */
    @DisplayName("""
            should throw a NullPointerException
            when the path argument is null"""
    )
    @Test
    void _ThrowNullPointerException_PathIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var path = (Path) null;
        // ------------------------------------------------------------------------------- when/then
        // assert, service.append(path) throws a NullPointerException
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.append(path)
        );
    }

    @DisplayName("should invoke write(FileChannel.open(path, CREATE, WRITE, APPEND))")
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // stub, <service.write(channel)> will return the <channel>
        BDDMockito.willAnswer(i -> i.getArgument(0))
                .given(service)
                .write(ArgumentMatchers.<WritableByteChannel>any());
        final var path = Mockito.mock(Path.class);
        final var channel = Mockito.mock(FileChannel.class);
        try (var mockStatic = Mockito.mockStatic(FileChannel.class)) {
            // FileChannel.open(path, arguments) will return the channel
            mockStatic.when(() -> FileChannel.open(ArgumentMatchers.same(path),
                                                   ArgumentMatchers.any(OpenOption[].class)))
                    .thenReturn(channel);
            // -------------------------------------------------------------------------------- when
            final var result = service.append(path);
            // -------------------------------------------------------------------------------- then
            // verify, <new FileChannel.open(path, <options>)> invoked, once

            // verify, <options> contains <StandardOpenOption.WRITE>, <StandardOpenOption.CREATE>,
            //         <StandardOpenOption.APPEND)>, and no others

            // verify, <write(channel)> invoked, once.

            // verify, <channel.force(true)> invoked, once.

            // verify, <channel.close()> invoked, once.

            // assert, <result> is same as <path>
            Assertions.assertSame(path, result);
        }
    }

    @畵蛇添足("testing with a real path doesn't add any value")
    @DisplayName("path's size should be increased by HelloWorld.BYTES")
    @Test
    void _添足_畵蛇(@TempDir final Path dir) throws Exception {
        // ----------------------------------------------------------------------------------- given
        var service = service();
        // stub, <service.append(Path)> will append 12 bytes
        BDDMockito.willAnswer(i -> {
                    var path = i.getArgument(0, Path.class);
                    try (var channel = FileChannel.open(path, StandardOpenOption.APPEND)) {
                        for (var b = ByteBuffer.allocate(HelloWorld.BYTES); b.hasRemaining(); ) {
                            final var w = channel.write(b);
                            assert w >= 0; // why?
                        }
                        channel.force(true);
                    }
                    return path;
                })
                .given(service)
                .append(ArgumentMatchers.any(Path.class));
        var path = Files.createTempFile(dir, null, null);
        var size = Files.size(path);
        // ------------------------------------------------------------------------------------ when
        var result = service.append(path);
        // ------------------------------------------------------------------------------------ then
        // assert, <path>'s <size> has been increased by <12>
        Assertions.assertEquals(
                size + HelloWorld.BYTES,
                Files.size(result)
        );
        // assert, <result> is same as <path>
        Assertions.assertEquals(path, result);
    }
}
