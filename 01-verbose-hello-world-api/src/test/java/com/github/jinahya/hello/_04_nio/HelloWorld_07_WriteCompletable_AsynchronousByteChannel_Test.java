package com.github.jinahya.hello._04_nio;

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
import com.github.jinahya.hello._HelloWorldTest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

/**
 * A class for testing
 * {@link HelloWorld#writeCompletable(AsynchronousByteChannel) writeCompletable(channel)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_07_WriteCompletable_AsynchronousByteChannel_Arguments_Test
 */
@DisplayName("writeCompletable(channel)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_07_WriteCompletable_AsynchronousByteChannel_Test extends _HelloWorldTest {

    /**
     * Verifies
     * {@link HelloWorld#writeCompletable(AsynchronousByteChannel) writeCompletable(channel)} method
     * returns a completable future completes with the {@code channel}.
     *
     * @throws Exception if thrown while getting the result of the future returned from the
     *                   {@link HelloWorld#writeCompletable(AsynchronousByteChannel)
     *                   writeCompletable(channel)} method.
     * @see CompletableFuture#get(long, TimeUnit)
     */
    @DisplayName("(channel)completed<channel>")
    @Test
    @SuppressWarnings({"unchecked"})
    void _Completed_() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = Mockito.mock(AsynchronousByteChannel.class);
        Mockito.doAnswer(i -> {
            final var handler = i.getArgument(1, CompletionHandler.class);
            final var attachment = i.getArgument(2);
            handler.completed(channel, attachment);
            return null;
        }).when(service).writeAsync(
                ArgumentMatchers.same(channel),
                ArgumentMatchers.notNull(),
                ArgumentMatchers.any()
        );
        // ------------------------------------------------------------------------------------ when
        final var future = service.writeCompletable(channel);
        // ------------------------------------------------------------------------------------ then
        // TODO: get the <result> of the <future>, with timeout
        // TODO: verify, service.writeAsync(same(chanel), notNull(), any()) invoked, once.
        // TODO: assert, result is same as channel.
    }

    /**
     * Verifies
     * {@link HelloWorld#writeCompletable(AsynchronousByteChannel) writeCompletable(channel)} method
     * returns a completable future being completed exceptionally.
     *
     * @see CompletableFuture#handle(BiFunction)
     * @see CompletableFuture#join()
     */
    @DisplayName("(channel)completedExceptionally")
    @Test
    @SuppressWarnings({"unchecked"})
    void _CompletedExceptionally_() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = Mockito.mock(AsynchronousByteChannel.class);
        final var exc = Mockito.mock(Throwable.class);
        Mockito.doAnswer(i -> {
            final var handler = i.getArgument(1, CompletionHandler.class);
            final var attachment = i.getArgument(2);
            handler.failed(exc, attachment);
            return null;
        }).when(service).writeAsync(
                ArgumentMatchers.same(channel),
                ArgumentMatchers.notNull(),
                ArgumentMatchers.any()
        );
        // ------------------------------------------------------------------------------------ when
        final var future = service.writeCompletable(channel);
        // ------------------------------------------------------------------------------------ then
        // TODO: assert, <future> completes exceptionally with the <exc>
    }
}
