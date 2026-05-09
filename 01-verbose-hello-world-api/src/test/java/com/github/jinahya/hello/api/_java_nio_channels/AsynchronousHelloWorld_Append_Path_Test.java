package com.github.jinahya.hello.api._java_nio_channels;

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

import com.github.jinahya.hello.api.AsynchronousHelloWorld;
import com.github.jinahya.hello.api.AsynchronousHelloWorldTest;
import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.畵蛇添足;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A class for testing
 * {@link AsynchronousHelloWorld#append(Path, Object, CompletionHandler) append(path, attachment,
 * handler)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Disabled
@DisplayName("append(path, attachment, handler)")
@Slf4j
class AsynchronousHelloWorld_Append_Path_Test
        extends AsynchronousHelloWorldTest {

//    /**
//     * Verifies that the
//     * {@link AsynchronousHelloWorld#append(Path, Object, CompletionHandler) append(path,
//     * attachment, handler)} method throws a {@link NullPointerException} when the {@code path}
//     * argument is {@code null}.
//     */
//    @DisplayName("""
//            should throw a <NullPointerException>
//            when the <path> argument is <null>""")
//    @Test
//    @SuppressWarnings({"unchecked"})
//    void _ThrowNullPointerException_PathIsNull() {
//        // ----------------------------------------------------------------------------------- given
//        final var service = asynchronousService();
//        final var path = (Path) null;
//        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
//        final var handler = Mockito.mock(CompletionHandler.class);
//        // ----------------------------------------------------------------------------- when / then
//        Assertions.assertThrows(
//                NullPointerException.class,
//                () -> service.append(path, attachment, handler)
//        );
//    }
//
//    /**
//     * Verifies that the
//     * {@link AsynchronousHelloWorld#append(Path, Object, CompletionHandler) append(path,
//     * attachment, handler)} method throws a {@link NullPointerException} when the {@code handler}
//     * argument is {@code null}.
//     */
//    @DisplayName("""
//            should throw a <NullPointerException>
//            when the <handler> argument is <null>""")
//    @Test
//    void _ThrowNullPointerException_HandlerIsNull(@TempDir final Path dir) {
//        // ----------------------------------------------------------------------------------- given
//        final var service = asynchronousService();
//        final var path = dir.resolve("test.dat");
//        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
//        final var handler = (CompletionHandler<Path, Object>) null;
//        // ----------------------------------------------------------------------------- when / then
//        Assertions.assertThrows(
//                NullPointerException.class,
//                () -> service.append(path, attachment, handler)
//        );
//    }
//
//    /**
//     * Verifies that the
//     * {@link AsynchronousHelloWorld#append(Path, Object, CompletionHandler) append(path,
//     * attachment, handler)} method opens a channel, invokes
//     * {@link AsynchronousHelloWorld#write(AsynchronousFileChannel, long, Object,
//     * CompletionHandler)}, and on completion, forces and closes the channel, and notifies
//     * {@code handler.completed(path, attachment)}.
//     */
//    @DisplayName("""
//            should write to the file, force, close the channel,
//            and invoke <handler.completed(path, attachment)>""")
//    @Test
//    @SuppressWarnings({"unchecked"})
//    void _complete_(@TempDir final Path dir)
//            throws Exception {
//        // ----------------------------------------------------------------------------------- given
//        final var service = asynchronousService();
//        final var file = Files.createTempFile(dir, null, null);
//        // capture the internally-opened channel to verify force/close
//        final var capturedChannel = new AtomicReference<AsynchronousFileChannel>();
//        // stub <service.write(channel, position, attachment, handler)> to simulate success:
//        //         capture the channel and invoke the internal handler's completed.
//        Mockito.doAnswer(i -> {
//            final var c = i.getArgument(0, AsynchronousFileChannel.class);
//            final var p = i.getArgument(1, Long.class);
//            final var a = i.getArgument(2);
//            final var h = i.getArgument(3, CompletionHandler.class);
//            capturedChannel.set(c);
//            // simulate successful write completion
//            h.completed(c, a);
//            return null;
//        }).when(service).write(
//                ArgumentMatchers.<AsynchronousFileChannel>notNull(), // <channel>
//                ArgumentMatchers.anyLong(),                         // <position>
//                ArgumentMatchers.any(),                             // <attachment>
//                ArgumentMatchers.<CompletionHandler<? super AsynchronousFileChannel, ?>>notNull() // <handler>
//        );
//        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
//        final var handler = Mockito.mock(CompletionHandler.class);
//        // ------------------------------------------------------------------------------------ when
//        service.append(file, attachment, handler);
//        // ------------------------------------------------------------------------------------ then
//        // verify, <handler.completed(file, attachment)> invoked, once.
//        Mockito.verify(handler, Mockito.timeout(TimeUnit.SECONDS.toMillis(8L)).times(1))
//                .completed(file, attachment);
//        // verify, <handler.failed(?, ?)> never invoked.
//        Mockito.verify(handler, Mockito.never())
//                .failed(ArgumentMatchers.any(), ArgumentMatchers.any());
//        // verify, <service.write(channel, position, ?, ?)> invoked with the channel and its size.
//        Mockito.verify(service, Mockito.times(1)).write(
//                ArgumentMatchers.<AsynchronousFileChannel>notNull(),
//                ArgumentMatchers.eq(0L), // file was empty, so position == 0
//                ArgumentMatchers.any(),
//                ArgumentMatchers.<CompletionHandler<? super AsynchronousFileChannel, ?>>notNull()
//        );
//        // verify, the channel was closed (by append's internal handler after force + close).
//        Assertions.assertFalse(capturedChannel.get().isOpen());
//    }
//
//    /**
//     * Verifies that the
//     * {@link AsynchronousHelloWorld#append(Path, Object, CompletionHandler) append(path,
//     * attachment, handler)} method, when the internal write fails, closes the channel and notifies
//     * {@code handler.failed(exc, attachment)}.
//     */
//    @DisplayName("""
//            should close the channel
//            and invoke <handler.failed(exc, attachment)>
//            when the write fails""")
//    @Test
//    @SuppressWarnings({"unchecked"})
//    void _failed_(@TempDir final Path dir)
//            throws Exception {
//        // ----------------------------------------------------------------------------------- given
//        final var service = asynchronousService();
//        final var file = Files.createTempFile(dir, null, null);
//        // the exception to simulate
//        final var exc = new RuntimeException("simulated write failure");
//        // capture the internally-opened channel to verify close
//        final var capturedChannel = new AtomicReference<AsynchronousFileChannel>();
//        // stub <service.write(channel, position, attachment, handler)> to simulate failure:
//        //         capture the channel and invoke the internal handler's failed.
//        Mockito.doAnswer(i -> {
//            final var c = i.getArgument(0, AsynchronousFileChannel.class);
//            final var a = i.getArgument(2);
//            final var h = i.getArgument(3, CompletionHandler.class);
//            capturedChannel.set(c);
//            // simulate write failure
//            h.failed(exc, a);
//            return null;
//        }).when(service).write(
//                ArgumentMatchers.<AsynchronousFileChannel>notNull(), // <channel>
//                ArgumentMatchers.anyLong(),                         // <position>
//                ArgumentMatchers.any(),                             // <attachment>
//                ArgumentMatchers.<CompletionHandler<? super AsynchronousFileChannel, ?>>notNull() // <handler>
//        );
//        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
//        final var handler = Mockito.mock(CompletionHandler.class);
//        // ------------------------------------------------------------------------------------ when
//        service.append(file, attachment, handler);
//        // ------------------------------------------------------------------------------------ then
//        // verify, <handler.failed(exc, attachment)> invoked, once.
//        Mockito.verify(handler, Mockito.timeout(TimeUnit.SECONDS.toMillis(8L)).times(1))
//                .failed(exc, attachment);
//        // verify, <handler.completed(?, ?)> never invoked.
//        Mockito.verify(handler, Mockito.never())
//                .completed(ArgumentMatchers.any(), ArgumentMatchers.any());
//        // verify, the channel was closed (by append's internal handler).
//        Assertions.assertFalse(capturedChannel.get().isOpen());
//    }
//
//    /**
//     * Verifies the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to a real
//     * temporary file via
//     * {@link AsynchronousHelloWorld#append(Path, Object, CompletionHandler) append(path,
//     * attachment, handler)}, and verifies the file size increased by {@value HelloWorld#BYTES}.
//     */
//    @畵蛇添足("testing with a real file doesn't add any value")
//    @Test
//    void _添足_畵蛇(@TempDir final Path dir)
//            throws Exception {
//        // ----------------------------------------------------------------------------------- given
//        final var service = asynchronousService();
//        final var file = Files.createTempFile(dir, null, null);
//        final var sizeBefore = Files.size(file);
//        final var latch = new CountDownLatch(1);
//        // ------------------------------------------------------------------------------------ when
//        service.append(file, null, new CompletionHandler<>() { // @formatter:off
//            @Override public void completed(final Path p, final Object a) {
//                log.debug("appended to {}", p);
//                Assertions.assertSame(file, p);
//                latch.countDown();
//            }
//            @Override public void failed(final Throwable exc, final Object a) {
//                log.error("failed to append", exc);
//                latch.countDown();
//            } // @formatter:on
//        });
//        Assertions.assertTrue(
//                latch.await(8L, TimeUnit.SECONDS),
//                "append did not complete in time"
//        );
//        // ------------------------------------------------------------------------------------ then
//        Assertions.assertEquals(sizeBefore + HelloWorld.BYTES, Files.size(file));
//    }
}
