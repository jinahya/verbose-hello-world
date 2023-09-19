package com.github.jinahya.hello.misc.c02rfc862;

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

import lombok.extern.slf4j.Slf4j;

import java.io.EOFException;
import java.io.UncheckedIOException;
import java.nio.channels.CompletionHandler;

@Slf4j
final class Rfc862Tcp4ClientHandlers {

    enum Connect implements CompletionHandler<Void, Rfc862Tcp4ClientAttachment> {
        HANDLER() { // @formatter:off
            @Override
            public void completed(final Void result, final Rfc862Tcp4ClientAttachment attachment) {
                attachment.write();
            }
            @Override
            public void failed(final Throwable exc, final Rfc862Tcp4ClientAttachment attachment) {
                log.error("failed to connect", exc);
                attachment.closeUnchecked();
            }
        } // @formatter:on
    }

    enum Write implements CompletionHandler<Integer, Rfc862Tcp4ClientAttachment> {
        HANDLER() { // @formatter:off
            @Override
            public void completed(final Integer result,
                                  final Rfc862Tcp4ClientAttachment attachment) {
                attachment.read();
            }
            @Override
            public void failed(final Throwable exc, final Rfc862Tcp4ClientAttachment attachment) {
                log.error("failed to write", exc);
                attachment.closeUnchecked();
            }
        } // @formatter:on
    }

    enum Read implements CompletionHandler<Integer, Rfc862Tcp4ClientAttachment> {
        HANDLER() { // @formatter:off
            @Override
            public void completed(final Integer result,
                                  final Rfc862Tcp4ClientAttachment attachment) {
                if (result == -1) {
                    if (attachment.bytes() > 0) {
                        throw new UncheckedIOException(new EOFException("unexpected eof"));
                    }
                    attachment.closeUnchecked();
                    return;
                }
                if (attachment.bytes() == 0) { // all bytes have already been sent
                    attachment.read();
                    return;
                }
                attachment.write();
            }
            @Override
            public void failed(final Throwable exc, final Rfc862Tcp4ClientAttachment attachment) {
                log.error("failed to read", exc);
                attachment.closeUnchecked();
            }
        } // @formatter:on
    }
}
