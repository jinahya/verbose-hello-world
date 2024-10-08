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
import com.github.jinahya.hello.HelloWorldTestUtils;
import com.github.jinahya.hello.畵蛇添足;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashSet;

/**
 * A class for testing {@link HelloWorld#append(Path) append(path)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("append(Path)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_04_Append_Path_Test extends HelloWorldTest {

    /**
     * Verifies that the {@link HelloWorld#append(Path) append(path)} method throws a
     * {@link NullPointerException} when the {@code path} argument is {@code null}.
     */
    @DisplayName("""
            should throw a <NullPointerException>
            when the <path> argument is <null>"""
    )
    @Test
    void _ThrowNullPointerException_PathIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var path = (Path) null;
        // ------------------------------------------------------------------------------- when/then
        // assert, <service.append(path)> throws a <NullPointerException>
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.append(path)
        );
    }

    @DisplayName("should invoke <write(FileChannel.open(path, CREATE, WRITE, APPEND))>")
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // stub, <service.write(channel)> will return the <channel>
        Mockito.doAnswer(i -> i.getArgument(0))
                .when(service)
                .write(ArgumentMatchers.<WritableByteChannel>any());
        // prepare, a mock object of <java.nio.file.Path>
        final var path = Mockito.mock(Path.class);
        // prepare, a mock object of <java.nio.channels.FileChannel>
        final var channel = Mockito.mock(FileChannel.class);
        // mock, static methods of <java.nio.channels.FileChannel>
        try (var mockStatic = Mockito.mockStatic(FileChannel.class)) {
            // stub, <FileChannel.open(path, options)> will return the <channel>
            mockStatic.when(() -> FileChannel.open(ArgumentMatchers.same(path),
                                                   ArgumentMatchers.any(OpenOption[].class)))
                    .thenReturn(channel);
            // -------------------------------------------------------------------------------- when
            final var result = service.append(path);
            // -------------------------------------------------------------------------------- then
            // verify, <FileChannel.open(path, <options>)> invoked, once
            final var captor = ArgumentCaptor.forClass(OpenOption[].class);
//            mockStatic.verify(
//                    () -> FileChannel.open(ArgumentMatchers.same(path), captor.capture()),
//                    Mockito.times(1)
//            );
            final var value = captor.getValue();
            // verify, <values[0]> contains <StandardOpenOption.CREATE>,
            //         <StandardOpenOption.APPEND)>, and no others
//            final var options = new HashSet<>(Arrays.asList(value));
//            Assertions.assertTrue(options.remove(StandardOpenOption.CREATE));
//            Assertions.assertTrue(options.remove(StandardOpenOption.APPEND));
//            Assertions.assertTrue(options.isEmpty());
            // verify, <write(channel)> invoked, once.
//            Mockito.verify(service, Mockito.times(1)).write(channel);
            // verify, <channel.force(true)> invoked, once.
//            Mockito.verify(channel, Mockito.times(1)).force(true);
            // verify, <channel.close()> invoked, once.
//            Mockito.verify(channel, Mockito.times(1)).close();
            // assert, <result> is same as <path>
            Assertions.assertSame(path, result);
        }
    }

    @畵蛇添足("testing with an existing file doesn't add any value")
    @DisplayName("<path>'s size should be increased by <12>")
    @Test
    void _添足_畵蛇(@TempDir final Path dir) throws Exception {
        // ----------------------------------------------------------------------------------- given
        var service = service();
        // stub, <service.append(path)> will append <12> <zero> bytes
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
                })
                .when(service)
                .append(ArgumentMatchers.notNull(Path.class));
        // create, a temp file
        final var path = Files.createTempFile(dir, null, null);
        // write, some
        HelloWorldTestUtils.writeSome(path);
        log.debug("size: {}", Files.size(path));
        log.debug("lastModifiedTime: {}", Files.getLastModifiedTime(path));
        final var size = Files.size(path);
        // ------------------------------------------------------------------------------------ when
        final var result = service.append(path);
        log.debug("size: {}", Files.size(path));
        log.debug("lastModifiedTime: {}", Files.getLastModifiedTime(path));
        // ------------------------------------------------------------------------------------ then
        // assert, <path>'s <size> increased by <12>
        Assertions.assertEquals(
                size + HelloWorld.BYTES,
                Files.size(result)
        );
        // assert, <result> is same as <path>
        Assertions.assertSame(path, result);
    }
}
