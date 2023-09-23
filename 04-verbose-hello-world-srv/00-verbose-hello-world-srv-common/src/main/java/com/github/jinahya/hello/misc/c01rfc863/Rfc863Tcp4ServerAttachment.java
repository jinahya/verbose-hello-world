package com.github.jinahya.hello.misc.c01rfc863;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Objects;

@Slf4j
final class Rfc863Tcp4ServerAttachment extends _Rfc863Attachment.Server {

    Rfc863Tcp4ServerAttachment(final AsynchronousChannelGroup group) {
        super();
        this.group = Objects.requireNonNull(group, "group is null");
    }

    @Override
    public void close() throws IOException {
        if (client != null) {
            client.close();
        }
        group.shutdownNow();
        super.close();
    }

    private final AsynchronousChannelGroup group;

    AsynchronousSocketChannel client;
}
