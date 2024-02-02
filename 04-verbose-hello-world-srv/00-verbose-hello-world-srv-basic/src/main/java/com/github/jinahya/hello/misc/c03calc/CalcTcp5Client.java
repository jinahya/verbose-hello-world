package com.github.jinahya.hello.misc.c03calc;

/*-
 * #%L
 * verbose-hello-world-srv-common
 * %%
 * Copyright (C) 2018 - 2023 Jinahya, Inc.
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

import com.github.jinahya.hello.util.JavaIoCloseableUtils;
import com.github.jinahya.hello.util._ExcludeFromCoverage_PrivateConstructor_Obviously;
import lombok.extern.slf4j.Slf4j;

import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
class CalcTcp5Client extends CalcTcp {

    // @formatter:off
    public static void main(final String... args) throws Exception {
        final var group = AsynchronousChannelGroup.withThreadPool(
                newExecutorForClient("tcp-5-client-")
        );
        final var sequence = new AtomicInteger();
        for (int i = 0; i < REQUEST_COUNT; i++) {
            // ------------------------------------------------------------------------ open/connect
            final var client = AsynchronousSocketChannel.open(group);
            client.<Void>connect(ADDR, null, new CompletionHandler<>() {
                @Override public void completed(final Void result, final Void a) {
                    final var message = new CalcMessage.OfBuffer()
                            .randomize()
                            .sequence(sequence)
                            .readyToWriteToServer();
                    // ----------------------------------------------------------------------- write
                    message.<Void>write(client, null, new CompletionHandler<>() {
                        @Override public void completed(final Integer w, final Void a) {
                            assert w > 0; // why?
                            if (message.hasRemaining()) {
                                message.write(client, null, this);
                                return;
                            }
                            // ---------------------------------------------------------------- read
                            message.readyToReadFromServer()
                                    .<Void>read(client, null, new CompletionHandler<>() {
                                        @Override
                                        public void completed(final Integer r, final Void a) {
                                            assert r > 0; // why?
                                            if (message.hasRemaining()) {
                                                message.read(client, null, this);
                                                return;
                                            }
                                            message.log();
                                            JavaIoCloseableUtils.closeUnchecked(client);
                                        }
                                        @Override
                                        public void failed(final Throwable exc, final Void a) {
                                            log.error("failed to read", exc);
                                            JavaIoCloseableUtils.closeUnchecked(client);
                                        }
                                    });
                        }
                        @Override public void failed(final Throwable exc, final Void a) {
                            log.error("failed to write", exc);
                            JavaIoCloseableUtils.closeUnchecked(client);
                        }
                    });
                }
                @Override public void failed(final Throwable exc, final Void a) {
                    log.error("failed to connect", exc);
                    assert !client.isOpen(); // why?
                }
            });
        }
        // -------------------------------------------------------- shutdown-group/await-termination
        group.shutdown();
        if (!group.awaitTermination(1L, TimeUnit.MINUTES)) {
            log.error("group not terminated");
        }
    }
    // @formatter:on

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private CalcTcp5Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
