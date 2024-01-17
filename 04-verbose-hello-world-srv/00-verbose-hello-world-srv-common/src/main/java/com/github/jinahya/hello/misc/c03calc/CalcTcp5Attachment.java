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

import com.github.jinahya.hello.util._Attachment;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

@Slf4j
abstract class CalcTcp5Attachment extends _Attachment {

    /**
     * Creates a new instance, for client, with specified client.
     *
     * @param client the client.
     * @param latch  a latch to {@link CountDownLatch#countDown() count down} then the new instance
     *               is {@link #close() closed}.
     * @return a new instance.
     */
    static CalcTcp5Attachment newInstanceForClient(final AsynchronousSocketChannel client,
                                                   final CountDownLatch latch) {
        return new CalcTcp5Attachment(client, _CalcMessage.newInstanceForClient()) {
            @Override
            public void close() throws IOException {
                latch.countDown();
                super.close();
            }
        };
    }

    /**
     * Creates a new instance, for servers, with specified client.
     *
     * @param client the client.
     * @return a new instance.
     */
    static CalcTcp5Attachment newInstanceForServer(final AsynchronousSocketChannel client) {
        return new CalcTcp5Attachment(client, _CalcMessage.newInstanceForServer()) {
        };
    }

    /**
     * Creates a new instance with specified client and message.
     *
     * @param client  the client.
     * @param message the message.
     */
    private CalcTcp5Attachment(final AsynchronousSocketChannel client, final _CalcMessage message) {
        super();
        this.client = Objects.requireNonNull(client, "client is null");
        this.message = Objects.requireNonNull(message, "message is null");
    }

    // --------------------------------------------------------------------------- java.io.Closeable
    @Override
    public void close() throws IOException {
        client.close();
        super.close();
    }

    // -------------------------------------------------------------------------------------- client

    /**
     * Connects to {@link _CalcConstants#ADDR} with specified handler.
     *
     * @param handler the handler.
     */
    void connect(final CompletionHandler<Void, ? super CalcTcp5Attachment> handler) {
        Objects.requireNonNull(handler, "handler is null");
        assert client.isOpen();
        client.connect(
                _CalcConstants.ADDR, // <remote>
                this,                // <attachment>
                handler              // <handler>
        );
    }

    void write(final CompletionHandler<Integer, ? super CalcTcp5Attachment> handler) {
        Objects.requireNonNull(handler, "handler is null");
        assert client.isOpen();
        message.write(
                client, // <channel>
                this,   // <attachment>
                handler // <handler>
        );
    }

    void read(final CompletionHandler<Integer, ? super CalcTcp5Attachment> handler) {
        Objects.requireNonNull(handler, "handler is null");
        assert client.isOpen();
        message.read(
                client, // <channel>
                this,   // <attachment>
                handler // <handler>
        );
    }

    // ------------------------------------------------------------------------------------- message

    boolean hasRemaining() {
        return message.hasRemaining();
    }

    CalcTcp5Attachment readyToReceiveResult() {
        message.readyToReceiveResult();
        return this;
    }

    CalcTcp5Attachment apply() {
        message.apply();
        return this;
    }

    CalcTcp5Attachment readyToSendResult() {
        message.readyToSendResult();
        return this;
    }

    CalcTcp5Attachment log() {
        message.log();
        return this;
    }

    // ---------------------------------------------------------------------------------------------
    private final AsynchronousSocketChannel client;

    private final _CalcMessage message;
}
