package com.github.jinahya.hello.misc.c02rfc862;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.util.Objects;

@Slf4j
final class Rfc862Udp2ServerAttachment {

    Rfc862Udp2ServerAttachment(final SelectionKey key) throws IOException {
        super();
        this.key = Objects.requireNonNull(key, "clientKey is null");
        this.channel = (DatagramChannel) this.key.channel();
        buffer = ByteBuffer.allocate(this.channel.getOption(StandardSocketOptions.SO_RCVBUF));
    }

    SocketAddress receive() throws IOException {
        assert key.isValid();
        assert key.isReadable();
        address = channel.receive(buffer);
        assert channel.isBlocking() || address != null;
        log.debug("{} byte(s) received from {}", buffer.position(), address);
        key.interestOpsAnd(~SelectionKey.OP_READ);
        key.interestOpsOr(SelectionKey.OP_WRITE);
        return address;
    }

    int send() throws IOException {
        assert key.isValid();
        assert key.isWritable();
        buffer.flip();
        final var s = channel.send(buffer, address);
        _Rfc862Utils.logServerBytes(buffer.position());
        _Rfc862Utils.logDigest(buffer.flip());
        key.interestOpsAnd(~SelectionKey.OP_WRITE);
        key.cancel();
        assert !key.isValid();
        return s;
    }

    private final SelectionKey key;

    private final DatagramChannel channel;

    private final ByteBuffer buffer;

    private SocketAddress address;
}
