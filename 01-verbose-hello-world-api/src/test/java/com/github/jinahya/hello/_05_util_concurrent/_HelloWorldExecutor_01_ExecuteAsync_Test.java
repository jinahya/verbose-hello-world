package com.github.jinahya.hello._05_util_concurrent;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2024 Jinahya, Inc.
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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A class for testing
 * {@link com.github.jinahya.hello.HelloWorldExecutor#executeAsync(Supplier, Function, Executor)}
 * method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PACKAGE)
class _HelloWorldExecutor_01_ExecuteAsync_Test extends __HelloWorldExecutorTest {

    @DisplayName("set(array)")
    @Test
    void __SetArray() throws ExecutionException, InterruptedException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        BDDMockito.willAnswer(i -> i.getArgument(0, byte[].class))
                .given(service)
                .set(ArgumentMatchers.notNull());
        final var executor = executor();
        final var array = new byte[HelloWorld.BYTES];
        // ------------------------------------------------------------------------------------ when
        final var result = executor.executeAsync(
                () -> service,
                s -> s.set(array),
                EXECUTOR
        ).get();
        // ------------------------------------------------------------------------------------ when
        Mockito.verify(service, Mockito.times(1)).set(array);
        Assertions.assertEquals(array, result);
    }

    @DisplayName("write(AsynchronousFileChannel)")
    @Test
    void __WriteAsynchronousFileChannel(@TempDir final Path tempDir)
            throws IOException, ExecutionException, InterruptedException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        BDDMockito.willAnswer(i -> {
                    final var buffer = i.getArgument(0, ByteBuffer.class);
                    buffer.position(buffer.position() + HelloWorld.BYTES);
                    return buffer;
                })
                .given(service)
                .put(ArgumentMatchers.argThat(b -> b != null && b.remaining() >= HelloWorld.BYTES));
        final var executor = executor();
        final var file = Files.createTempFile(tempDir, null, null);
        final var position = ThreadLocalRandom.current().nextLong(8L);
        // ------------------------------------------------------------------------------------ when
        try (final var channel = AsynchronousFileChannel.open(file, StandardOpenOption.WRITE)) {
            final var result = executor.executeAsync(
                    () -> service,
                    s -> {
                        try {
                            return s.write(channel, position);
                        } catch (final InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            throw new RuntimeException(ie);
                        } catch (final ExecutionException ee) {
                            throw new RuntimeException(ee);
                        }
                    },
                    EXECUTOR
            ).get();
            Assertions.assertEquals(channel, result);
        }
        // ------------------------------------------------------------------------------------ when
        Assertions.assertEquals(position + HelloWorld.BYTES, Files.size(file));
    }
}
