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
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

@DisplayName("append using RandomAccessFile")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_22_Append_File_Using_RandomAccessFile_Test extends HelloWorldTest {

    @Test
    void __(@TempDir final File dir) throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // stub, <service.write(RandomAccessFile)> will write the <hello, world> bytes.
        BDDMockito.willAnswer(i -> {
                    final var file = i.getArgument(0, RandomAccessFile.class);
                    file.write("hello, world".getBytes(StandardCharsets.US_ASCII));
                    return file;
                })
                .given(service)
                .write(ArgumentMatchers.<RandomAccessFile>notNull());
        // create a temp file
        final File file = File.createTempFile("tmp", null, dir);
        assert file.length() == 0L;
        final var pos = ThreadLocalRandom.current().nextLong(128L);
        log.debug("pos: {}", pos);
        // ------------------------------------------------------------------------------------ when
        try (var f = new RandomAccessFile(file, "rw")) { // check, rws, rwd
            f.seek(pos);
            final var result = service.write(f);
            assert result == f;
            f.getFD().sync();
        }
        // ------------------------------------------------------------------------------------ then
        // verify, <service.write(RandomAccessFile)> invoked, once
        Mockito.verify(service, Mockito.times(1))
                .write(ArgumentMatchers.<RandomAccessFile>notNull());
        // verify, no unverified interactions on the <service>
        Mockito.verifyNoMoreInteractions(service);
        // assert, <file.length> increased by <12>
        Assertions.assertEquals(
                pos + HelloWorld.BYTES,
                file.length()
        );
        // print <file>'s content
        try (var f = new RandomAccessFile(file, "r")) {
            f.seek(pos);
            final var bytes = new byte[HelloWorld.BYTES];
            final var r = f.read(bytes);
            assert r == bytes.length;
            log.debug("string: {}", new String(bytes, StandardCharsets.US_ASCII));
        }
    }
}
