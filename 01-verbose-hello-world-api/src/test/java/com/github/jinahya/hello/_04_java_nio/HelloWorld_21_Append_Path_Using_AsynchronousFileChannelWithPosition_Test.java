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
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A class for testing
 * {@link HelloWorld#write(AsynchronousFileChannel, long, Object, CompletionHandler) write(channel,
 * position, handler, attachment)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(channel, position)")
@Slf4j
class HelloWorld_21_Append_Path_Using_AsynchronousFileChannelWithPosition_Test
        extends HelloWorldTest {

    @Test
    void __(@TempDir final Path dir) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // stub, <service.write(channel, position)>
        //         will write 12 bytes to the <channel> starting at <position>
        BDDMockito.willAnswer(i -> {
                    final var channel = i.getArgument(0, AsynchronousFileChannel.class);
                    var position = i.getArgument(1, Long.class);
                    for (final var b = ByteBuffer.allocate(HelloWorld.BYTES); b.hasRemaining(); ) {
                        position += channel.write(b, position).get();
                    }
                    return channel;
                })
                .given(service)
                .write(ArgumentMatchers.notNull(), ArgumentMatchers.longThat(v -> v >= 0L));
        final var path = Files.createTempFile(dir, null, null);
        final var position = ThreadLocalRandom.current().nextLong(128L);
        // ------------------------------------------------------------------------------------ when
        try (var channel = AsynchronousFileChannel.open(path, StandardOpenOption.WRITE)) {
            final var result = service.write(channel, position);
            channel.force(true);
            assert result == channel;
        }
        // ------------------------------------------------------------------------------------ then
        // assert, <path>'s <size> increased by <12>
        Assertions.assertEquals(
                position + HelloWorld.BYTES,
                Files.size(path)
        );
    }
}
