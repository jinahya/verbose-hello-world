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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * A class for exploring {@link HelloWorld#write(java.io.DataOutput) write(output)} method against a
 * real {@link File} through a {@link DataOutputStream}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("HelloWorld.append(File) using DataOutput")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Append_File_Using_DataOutput_Test
        extends HelloWorld__Test {

    /**
     * Verifies that {@link HelloWorld#write(DataOutput)} appends the {@code hello-world-bytes} to a
     * real {@link File} when driven through a {@link DataOutputStream} opened in appending mode.
     *
     * @param dir the {@link TempDir} that holds the temp file.
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("happy path")
    @Test
    void __(@TempDir final File dir)
            throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // stub: <service.write(DataOutput)> will write <hello, world> bytes.
        Mockito.doAnswer(i -> {
            final var output = i.getArgument(0, DataOutput.class);
            output.write(HelloWorld__TestUtils.hello_world_byte_array());
            return output;
        }).when(service).write(ArgumentMatchers.<DataOutput>notNull());
        // prepare: create a temp file, and write some dummy bytes
        final File file = File.createTempFile("tmp", null, dir);
        try (var s = new FileOutputStream(file)) {
            s.write(new byte[ThreadLocalRandom.current().nextInt(8)]);
            s.flush();
        }
        // prepare: current <length> of the <file>
        final var length = file.length();
        // ------------------------------------------------------------------------------------ when
        try (var output = new DataOutputStream(new FileOutputStream(file, true))) { // appending!
            final var result = service.write((DataOutput) output);
            assert result == output;
            output.flush();
        }
        // ------------------------------------------------------------------------------------ then
        // verify: <service.write(output)> invoked, once
        verify(service, times(1)).write(ArgumentMatchers.<DataOutput>notNull());
        verifyNoMoreInteractions(service);
        // assert: <file>'s <length> increased by <12>
        assertEquals(
                length + HelloWorld.BYTES,
                file.length()
        );
        // verify: print <file>'s content
        try (var f = new RandomAccessFile(file, "r")) {
            f.seek(length);
            final var bytes = new byte[HelloWorld.BYTES];
            final var r = f.read(bytes);
            assert r == bytes.length;
            log.debug("decoded: {}", new String(bytes, StandardCharsets.US_ASCII));
        }
    }
}
