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
import org.mockito.Mockito;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

@DisplayName("append using Writer")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_23_Append_File_Using_Writer_Test extends HelloWorldTest {

    @Test
    void _appendToFileUsingDataOutput_(@TempDir final File dir) throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // stub, <service.write(writer)> will write 'hello, world' chars
        BDDMockito.willAnswer(i -> {
                    final var writer = i.getArgument(0, Writer.class);
                    writer.write("hello, world".toCharArray());
                    return writer;
                })
                .given(service)
                .write(ArgumentMatchers.<Writer>notNull());
        // create a temp file, and write some dummy bytes
        final File file = File.createTempFile("tmp", null, dir);
        if (ThreadLocalRandom.current().nextBoolean()) {
            try (var stream = new FileOutputStream(file)) {
                stream.write(new byte[ThreadLocalRandom.current().nextInt(8)]);
                stream.flush();
            }
        }
        final var length = file.length();
        log.debug("file.length before: {}", length);
        // ------------------------------------------------------------------------------------ when
        try (var writer = new OutputStreamWriter(new FileOutputStream(file, true), // appending!
                                                 StandardCharsets.US_ASCII)) {     // US_ASCII!
            final var result = service.write(writer);
            assert result == writer;
            writer.flush();
        }
        // ------------------------------------------------------------------------------------ then
        // verify, <service.write(Writer)> invoked, once
        Mockito.verify(service, Mockito.times(1)).write(ArgumentMatchers.<Writer>notNull());
        // verify, no unverified interactions on the <service>
        Mockito.verifyNoMoreInteractions(service);
        // assert, <file.length> increased by <12>
        Assertions.assertEquals(
                length + HelloWorld.BYTES,
                file.length()
        );
        // print <file>'s content
        log.debug("file.length after: {}", file.length());
        try (var f = new RandomAccessFile(file, "r")) {
            f.seek(length);
            final byte[] bytes = new byte[HelloWorld.BYTES];
            final var r = f.read(bytes);
            assert r == bytes.length;
            log.debug("string: {}", new String(bytes, StandardCharsets.US_ASCII));
        }
    }
}
