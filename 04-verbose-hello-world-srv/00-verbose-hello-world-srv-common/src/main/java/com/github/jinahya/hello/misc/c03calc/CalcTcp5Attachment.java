package com.github.jinahya.hello.misc.c03calc;

import com.github.jinahya.hello.misc._Attachment;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

@Slf4j
class CalcTcp5Attachment extends _Attachment {

    /**
     * Creates a new instance for specified client with {@link _CalcMessage#newBufferForClient()}.
     *
     * @param latch  a latch to {@link CountDownLatch#countDown() count down} then the new instance
     *               is {@link #close() closed}.
     * @param client the client.
     * @return a new instance.
     */
    static CalcTcp5Attachment newInstanceForClient(final CountDownLatch latch,
                                                   final AsynchronousSocketChannel client) {
        return new CalcTcp5Attachment(client, _CalcMessage.newBufferForClient()) {
            @Override
            public void close() throws IOException {
                latch.countDown();
                super.close();
            }
        };
    }

    /**
     * Creates a new instance for specified client with {@link _CalcMessage#newBufferForServer()}.
     *
     * @param client the client.
     * @return a new instance.
     */
    static CalcTcp5Attachment newInstanceForServer(final AsynchronousSocketChannel client) {
        return new CalcTcp5Attachment(client, _CalcMessage.newBufferForServer());
    }

    /**
     * Creates a new instance with specified client and buffer.
     *
     * @param client the client.
     * @param buffer the buffer.
     */
    private CalcTcp5Attachment(final AsynchronousSocketChannel client, final ByteBuffer buffer) {
        super();
        this.client = Objects.requireNonNull(client, "client is null");
        this.buffer = Objects.requireNonNull(buffer, "buffer is null");
    }

    // --------------------------------------------------------------------------- java.io.Closeable
    @Override
    public void close() throws IOException {
        client.close();
        super.close();
    }

    // -------------------------------------------------------------------------------------- client
    public void write(final CompletionHandler<Integer, ? super CalcTcp5Attachment> handler) {
        client.write(
                buffer,                            // <src>
                _CalcConstants.WRITE_TIMEOUT,      // <timeout>
                _CalcConstants.WRITE_TIMEOUT_UNIT, // <unit>
                this,                              // <attachment>
                handler                            // <handler>
        );
    }

    void read(final CompletionHandler<Integer, ? super CalcTcp5Attachment> handler) {
        client.read(
                buffer,                           // <dst>
                _CalcConstants.READ_TIMEOUT,      // <timeout>
                _CalcConstants.READ_TIMEOUT_UNIT, // <unit>
                this,                             // <attachment>
                handler                           // <handler>
        );
    }

    // ---------------------------------------------------------------------------------------------
    private final AsynchronousSocketChannel client;

    final ByteBuffer buffer;
}
