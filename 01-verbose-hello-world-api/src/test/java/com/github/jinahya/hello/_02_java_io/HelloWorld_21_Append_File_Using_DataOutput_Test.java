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

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

@DisplayName("appends using DataOutput")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_21_Append_File_Using_DataOutput_Test extends HelloWorldTest {

    @Test
    void __(@TempDir final File dir) throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // stub, <service.set(DataOutput)> will write <12> empty bytes.
        BDDMockito.willAnswer(i -> {
                    final var output = i.getArgument(0, DataOutput.class);
                    output.write(new byte[HelloWorld.BYTES]);
                    return output;
                })
                .given(service)
                .write(ArgumentMatchers.<DataOutput>notNull());
        // create a temp file, and write some dummy bytes
        final File file = File.createTempFile("tmp", null, dir);
        try (var stream = new FileOutputStream(file)) {
            stream.write(new byte[ThreadLocalRandom.current().nextInt(128)]);
            stream.flush();
        }
        final var length = file.length();
        // ------------------------------------------------------------------------------------ when
        try (var output = new DataOutputStream(new FileOutputStream(file, true))) { // appending!
            final var result = service.write((DataOutput) output);
            assert result == output;
            output.flush();
        }
        // ------------------------------------------------------------------------------------ then
        // verify, <service.write(output)> invoked, once
        Mockito.verify(service, Mockito.times(1)).write(ArgumentMatchers.<DataOutput>notNull());
        // verify, no unverified interactions on the <service>
        Mockito.verifyNoMoreInteractions(service);
        // assert, <file.length> increased by <12>
        Assertions.assertEquals(
                length + HelloWorld.BYTES,
                file.length()
        );
    }
}
