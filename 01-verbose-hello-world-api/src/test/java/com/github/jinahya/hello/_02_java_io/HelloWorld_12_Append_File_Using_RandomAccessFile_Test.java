package com.github.jinahya.hello._02_java_io;

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
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Tests appending the {@code hello, world} to an instance of {@link File} using
 * {@link HelloWorld#write(Writer)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("append using Writer")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_12_Append_File_Using_RandomAccessFile_Test extends HelloWorldTest {

    @Test
    void _appendToFileUsingDataOutput_(@TempDir final File tempDir) throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // stub, service.write(RandomAccessFile) will write 12 empty bytes.
        BDDMockito.willAnswer(i -> {
                    final var file = i.getArgument(0, RandomAccessFile.class);
                    file.write(new byte[HelloWorld.BYTES]);
                    return file;
                })
                .given(service)
                .write(ArgumentMatchers.<RandomAccessFile>notNull());
        // create a temp file
        final File f = File.createTempFile("tmp", "tmp", tempDir);
        final var pos = ThreadLocalRandom.current().nextLong(128L);
        // ------------------------------------------------------------------------------------ when
        try (var file = new RandomAccessFile(f, "rw")) {
            file.seek(pos);
            service.write(file);
            file.getFD().sync();
        }
        // ------------------------------------------------------------------------------------ then
        Assertions.assertEquals(pos + HelloWorld.BYTES, f.length());
    }
}
