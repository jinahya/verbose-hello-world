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

import com.github.jinahya.hello.api.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.*;
import org.mockito.*;

import java.io.*;
import java.nio.charset.*;
import java.util.concurrent.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * A class for exploring {@link HelloWorld#write(java.io.DataOutput) write(output)} method against a
 * real {@link File} through a {@link RandomAccessFile}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("append(file) using RandomAccessFile")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Append_File_Using_RandomAccessFile_Test
        extends HelloWorld__Test {

    /**
     * Verifies that {@link HelloWorld#write(DataOutput)} writes the {@code hello-world-bytes} to a
     * real {@link File} when driven through a {@link RandomAccessFile} positioned at a random
     * offset.
     *
     * @param dir the {@link TempDir} that holds the temp file.
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("should write <hello-world-bytes> to the <file> through a <RandomAccessFile>")
    @Test
    void __(@TempDir final File dir)
            throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // stub: <service.write(RandomAccessFile)> will write the <hello, world> bytes.
        doAnswer(i -> {
            final var file = i.getArgument(0, RandomAccessFile.class);
            file.write(hello_world_byte_array());
            return file;
        }).when(service).write(ArgumentMatchers.<RandomAccessFile>notNull());
        // prepare: create a temp file
        final File file = File.createTempFile("tmp", null, dir);
        assert file.length() == 0L;
        // prepare: a random <pos>
        final var pos = ThreadLocalRandom.current().nextLong(128L);
        // ------------------------------------------------------------------------------------ when
        try (var f = new RandomAccessFile(file, "rw")) { // check, rws, rwd
            f.seek(pos);
            final var result = service.write(f);
            assert result == f;
            f.getFD().sync();
        }
        // ------------------------------------------------------------------------------------ then
        // verify: <service.write(RandomAccessFile)> invoked, once
        verify(service, times(1))
                .write(ArgumentMatchers.<RandomAccessFile>notNull());
        verifyNoMoreInteractions(service);
        // assert: <file>'s <length> increased by <12>
        assertEquals(pos + HelloWorld.BYTES, file.length());
        // verify: print <file>'s content
        try (var f = new RandomAccessFile(file, "r")) {
            f.seek(pos);
            final var b = new byte[HelloWorld.BYTES];
            final var r = f.read(b);
            assert r == b.length;
            log.debug("decoded: {}", new String(b, StandardCharsets.US_ASCII));
        }
    }
}
