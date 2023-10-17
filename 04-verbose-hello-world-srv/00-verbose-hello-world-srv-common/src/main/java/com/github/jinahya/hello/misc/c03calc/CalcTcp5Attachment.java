package com.github.jinahya.hello.misc.c03calc;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Objects;

class CalcTcp5Attachment {

    static CalcTcp5Attachment newInstanceForClient(final AsynchronousSocketChannel client) {
        return new CalcTcp5Attachment(client, _CalcMessage.newBufferForClient());
    }

    static CalcTcp5Attachment newInstanceForServer(final AsynchronousSocketChannel client) {
        return new CalcTcp5Attachment(client, _CalcMessage.newBufferForServer());
    }

    private CalcTcp5Attachment(final AsynchronousSocketChannel client, final ByteBuffer buffer) {
        super();
        this.client = Objects.requireNonNull(client, "client is null");
        this.buffer = Objects.requireNonNull(buffer, "buffer is null");
    }

    private final AsynchronousSocketChannel client;

    private final ByteBuffer buffer;
}
