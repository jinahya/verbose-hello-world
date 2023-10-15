package com.github.jinahya.hello.misc.c03calc;

import com.github.jinahya.hello.misc._TcpUtils;
import com.github.jinahya.hello.util.HelloWorldServerUtils;
import com.github.jinahya.hello.util.JavaLangUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Objects;

@Slf4j
class CalcTcp3Server {

    private static void close(final SocketChannel client) {
        Objects.requireNonNull(client, "client is null");
        try {
            client.close();
        } catch (final IOException ioe) {
            log.error("failed to close {}", client, ioe);
        }
    }

    private static void serve(final Selector selector) {
        while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
            try {
                if (selector.select(_CalcConstants.SELECT_TIMEOUT_MILLIS) == 0) {
                    continue;
                }
            } catch (final IOException ioe) {
                log.error("failed to select", ioe);
                continue;
            }
            for (final var i = selector.selectedKeys().iterator(); i.hasNext(); ) {
                final var selectedKey = i.next();
                i.remove();
                if (selectedKey.isAcceptable()) {
                    final var channel = (ServerSocketChannel) selectedKey.channel();
                    final SocketChannel client;
                    try {
                        client = channel.accept();
                    } catch (final IOException ioe) {
                        log.debug("failed to accept", ioe);
                        continue;
                    }
                    try {
                        client.configureBlocking(false);
                        final var clientKey = client.register(
                                selector,                       // <sel>
                                SelectionKey.OP_READ,           // <ops>
                                _CalcUtils.newBufferForServer() // <att>
                        );
                        assert clientKey.isValid();
                    } catch (final IOException ioe) {
                        log.error("failed to configure/register", ioe);
                        close(client);
                    }
                }
                if (selectedKey.isReadable()) {
                    final var channel = (SocketChannel) selectedKey.channel();
                    final var attachment = (ByteBuffer) selectedKey.attachment();
                    final int r;
                    try {
                        r = channel.read(attachment);
                    } catch (final IOException ioe) {
                        log.error("failed to read", ioe);
                        close(channel);
                        assert !selectedKey.isValid();
                        continue;
                    }
                    if (r == -1) {
                        log.error("unexpected eof");
                        close(channel);
                        assert !selectedKey.isValid();
                        continue;
                    }
                    assert r >= 0;
                    if (!attachment.hasRemaining()) {
                        selectedKey.interestOpsAnd(~SelectionKey.OP_READ);
                        CalcOperator.apply(attachment.clear());
                        assert attachment.remaining() == Integer.BYTES;
                        selectedKey.interestOpsOr(SelectionKey.OP_WRITE);
                    }
                }
                if (selectedKey.isWritable()) {
                    final var channel = (SocketChannel) selectedKey.channel();
                    final var attachment = (ByteBuffer) selectedKey.attachment();
                    final int w;
                    try {
                        w = channel.write(attachment);
                    } catch (final IOException ioe) {
                        log.error("failed to write", ioe);
                        close(channel);
                        assert !selectedKey.isValid();
                        continue;
                    }
                    assert w >= 0;
                    if (!attachment.hasRemaining()) {
                        close(channel);
                        assert !selectedKey.isValid();
                    }
                }
            }
        }
    }

    public static void main(final String... args) throws IOException {
        try (var selector = Selector.open();
             var server = ServerSocketChannel.open()) {
            // -------------------------------------------------------------------------------- BIND
            server.bind(_CalcConstants.ADDR, 50);
            _TcpUtils.logBound(server);
            // ---------------------------------------------------------------------------- REGISTER
            server.configureBlocking(false);
            final var serverKey = server.register(selector, SelectionKey.OP_ACCEPT);
            JavaLangUtils.readLinesAndCallWhenTests(
                    HelloWorldServerUtils::isQuit,
                    () -> {
                        server.close();
                        assert !serverKey.isValid();
                        return null;
                    },
                    null
            );
            serve(selector);
        }
    }
}
