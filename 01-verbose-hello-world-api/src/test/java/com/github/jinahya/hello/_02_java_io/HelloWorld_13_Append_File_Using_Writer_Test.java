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
import org.apache.commons.text.RandomStringGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
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
class HelloWorld_13_Append_File_Using_Writer_Test extends HelloWorldTest {

    @Test
    void _appendToFileUsingDataOutput_(@TempDir final File tempDir) throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // prepare cbuf
        final char[] cbuf;
        {
            cbuf = new RandomStringGenerator.Builder()
                    .withinRange(0, 65535)
                    .get()
                    .generate(HelloWorld.BYTES).toCharArray();
            assert cbuf.length == HelloWorld.BYTES;
            log.debug("cbuf: {}", String.valueOf(cbuf));
            for (final var c : cbuf) {
                log.debug("c: {}", String.format("%1$04x", (int) c));
            }
        }
        // stub service.set(DataOutput) will write 12 empty bytes.
        BDDMockito.willAnswer(i -> {
                    final var writer = i.getArgument(0, Writer.class);
                    writer.write(cbuf);
                    return writer;
                })
                .given(service)
                .write(ArgumentMatchers.<Writer>any());
        // create a temp file
        final File file = File.createTempFile("tmp", "tmp", tempDir);
        // write some dummy bytes
        if (ThreadLocalRandom.current().nextBoolean()) {
            try (var stream = new FileOutputStream(file)) {
                stream.write(new byte[ThreadLocalRandom.current().nextInt(128)]);
                stream.flush();
            }
        }
        final var length = file.length();
        // ------------------------------------------------------------------------------------ when
        try (var writer = new OutputStreamWriter(new FileOutputStream(file, true), // appending!
                                                 StandardCharsets.US_ASCII)) {     // US_ASCII!
            service.write(writer);
            writer.flush();
        }
        // ------------------------------------------------------------------------------------ then
        Assertions.assertEquals(length + HelloWorld.BYTES, file.length());
    }
}
