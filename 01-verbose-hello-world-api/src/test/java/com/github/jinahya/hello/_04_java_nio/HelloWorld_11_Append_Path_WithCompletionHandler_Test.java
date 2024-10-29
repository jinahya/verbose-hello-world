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
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * A class for testing
 * {@link HelloWorld#append(Path, Object, CompletionHandler) append(path, attachment, handler)}
 * method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("append(path, handler, attachment)")
@Slf4j
class HelloWorld_11_Append_Path_WithCompletionHandler_Test extends HelloWorldTest {

    /**
     * Verifies that the
     * {@link HelloWorld#append(Path, Object, CompletionHandler) append(path, attachment, handler})
     * method throws a {@link NullPointerException} when the {@code path} argument is {@code null}.
     */
    @DisplayName("""
            should throw a <NullPointerException>
            when the <path> argument is <null>"""
    )
    @Test
    @SuppressWarnings({"unchecked"})
    void _ThrowNullPointerException_PathIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var path = (Path) null;
        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
        final var handler = Mockito.mock(CompletionHandler.class);
        // ------------------------------------------------------------------------------- when/then
        // assert: <service.append(null, attachment, handler> throws a <NullPointerException>
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.append(path, attachment, handler)
        );
    }

    /**
     * Verifies that the
     * {@link HelloWorld#append(Path, Object, CompletionHandler) append(path, attachment, handler)}
     * method throws a {@link NullPointerException} when the {@code handler} argument is
     * {@code null}.
     */
    @DisplayName("""
            should throw a <NullPointerException>
            when the <handler> argument is <null>"""
    )
    @Test
    void _ThrowNullPointerException_HandlerIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var path = Mockito.mock(Path.class);
        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
        final var handler = (CompletionHandler<Path, Object>) null;
        // ------------------------------------------------------------------------------- when/then
        // assert: <service.append(path, attachment, null) throws a <NullPointerException>
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.append(path, attachment, handler)
        );
    }

    /**
     * Asserts
     * {@link HelloWorld#append(Path, Object, CompletionHandler) append(path, attachment, handler)}
     * method appends {@value HelloWorld#BYTES} bytes to the {@code path}.
     *
     * @param dir a temporary directory to test with.
     */
    @DisplayName("""
            should invoke <write(a-channel, path.size, an-attachment, a-handler>
            and invoke <handler.completed(path, attachment)>""")
    @Test
    @SuppressWarnings({"unchecked"})
    void __(@TempDir final Path dir) throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // stub: <service.write(channel, position, attachment, handler)>
        //       will write <12> bytes to the channel,
        //       and invokes <handler.completed(channel, attachment)>
        Mockito.doAnswer(i -> {
            final var channel = i.getArgument(0, AsynchronousFileChannel.class);
            final var position = i.getArgument(1, Long.class);
            final var attachment = i.getArgument(2);
            final var handler = (CompletionHandler<AsynchronousFileChannel, Object>)
                    i.getArgument(3, CompletionHandler.class);
            final var buffer = new_hello_world_buffer();
            channel.write(buffer, position, position, new CompletionHandler<>() { // @formatter:off
                @Override public void completed(final Integer r, final Long a) {
                    log.debug("completed({}, {})", r, a);
                    assert r > 0L;
                    if (!buffer.hasRemaining()) {
                        handler.completed(channel, attachment);
                        return;
                    }
                    final var position = a + r;
                    channel.write(buffer, position, position, this);
                }
                @Override public void failed(final Throwable t, final Long a) {
                    log.error("failed({}, {})", t, a, t);
                    handler.failed(t, attachment);
                } // @formatter:off
            });
            return null;
        }).when(service).write(
                ArgumentMatchers.argThat(c -> c != null && c.isOpen()), // <channel>
                ArgumentMatchers.longThat(p -> p >= 0L),                // <position>
                ArgumentMatchers.any(),                                 // <attachment>
                ArgumentMatchers.notNull()                              // <handler>
        );
        // prepare: create temporary file
        final var path = Files.createTempFile(dir, null, null);
        try (var c = FileChannel.open(path, StandardOpenOption.WRITE)) {
            final var b  = ByteBuffer.allocate(ThreadLocalRandom.current().nextInt(128));
            while (b .hasRemaining()) {
                final var w = c.write(b );
                assert w >= 0L;
            }
            c.force(false);
        }
        final var size = Files.size(path);
        // prepare: a random <attachment>; null or non-null
        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
        // prepare: a mock object of <CompletionHandler>
        final var handler = Mockito.mock(CompletionHandler.class,
                                         Mockito.withSettings().verboseLogging());
        // ------------------------------------------------------------------------------------ when
        service.append(path, attachment, handler);
        // ------------------------------------------------------------------------------------ then
        // verify: <service.write(a-channel, <size>, an-attachment, a-handler> invoked, once
        Mockito.verify(service, Mockito.times(1)).write(
                ArgumentMatchers.notNull(),                // <channel>
                ArgumentMatchers.longThat(p -> p == size), // <position>
                ArgumentMatchers.any(),                    // <attachment>
                ArgumentMatchers.notNull()                 // <handler>
        );
        // await: <handler.completed(<path>, <attachment>)> invoked, once
        Mockito.verify(handler, Mockito.timeout(TimeUnit.SECONDS.toMillis(64L)).times(1)).completed(
                ArgumentMatchers.same(path),      // <result>
                ArgumentMatchers.same(attachment) // <attachment>
        );
        // verify: <path>'s size has been increased by <12>
        Assertions.assertEquals(
                size + HelloWorld.BYTES,
                Files.size(path)
        );
    }
}