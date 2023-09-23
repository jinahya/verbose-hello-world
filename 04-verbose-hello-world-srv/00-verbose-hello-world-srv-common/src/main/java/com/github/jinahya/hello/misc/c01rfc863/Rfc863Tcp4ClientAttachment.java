package com.github.jinahya.hello.misc.c01rfc863;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Objects;

@Slf4j
final class Rfc863Tcp4ClientAttachment extends _Rfc863Attachment.Client {

    Rfc863Tcp4ClientAttachment(final AsynchronousChannelGroup group,
                               final AsynchronousSocketChannel client) {
        super();
        this.group = Objects.requireNonNull(group, "group is null");
        this.client = Objects.requireNonNull(client, "client is null");
    }

    @Override
    public void close() throws IOException {
        group.shutdownNow();
        super.close();
    }

    private final AsynchronousChannelGroup group;

    final AsynchronousSocketChannel client;
}
