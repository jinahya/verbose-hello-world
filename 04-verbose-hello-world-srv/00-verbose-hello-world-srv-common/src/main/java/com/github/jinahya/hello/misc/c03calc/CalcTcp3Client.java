package com.github.jinahya.hello.misc.c03calc;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

@Slf4j
class CalcTcp3Client {

    private static void close(final SelectionKey key) {
        final var channel = key.channel();
        try {
            channel.close();
            assert !key.isValid();
        } catch (final IOException ioe) {
            log.debug("failed to close {}", channel, ioe);
            key.cancel();
        }
        assert !key.isValid();
    }

    private static void sub(final Selector selector) {
        for (var c = 0; c < _CalcConstants.TOTAL_REQUESTS; c++) {
            // ------------------------------------------------------------------------- connect/try
            try {
                final var client = SocketChannel.open();
                client.configureBlocking(false);
                final SelectionKey clientKey;
                try {
                    final var connectedImmediately = client.connect(_CalcConstants.ADDR);
                    if (connectedImmediately) {
                        clientKey = client.register(
                                selector,                         // <sel>
                                SelectionKey.OP_WRITE,            // <ops>
                                _CalcMessage.newBufferForClient() // <att>
                        );
                    } else {
                        clientKey = client.register(
                                selector,               // <sel>
                                SelectionKey.OP_CONNECT // <ops>
                        );
                    }
                    assert clientKey.isValid();
                    selector.wakeup();
                } catch (final IOException ioe) {
                    log.error("failed to connect/register", ioe);
                    client.close();
                }
            } catch (final IOException ioe) {
                log.error("failed to open/close", ioe);
            }
        }
        while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
            try {
                if (selector.select(_CalcConstants.CLIENT_SELECT_TIMEOUT_MILLIS) == 0) {
                    continue;
                }
            } catch (final IOException ioe) {
                log.error("failed to select", ioe);
            }
            for (final var i = selector.selectedKeys().iterator(); i.hasNext(); ) {
                final var selectedKey = i.next();
                i.remove();
                // ------------------------------------------------------------------ connect/finish
                if (selectedKey.isConnectable()) {
                    final var channel = (SocketChannel) selectedKey.channel();
                    try {
                        if (!channel.finishConnect()) {
                            continue;
                        }
                    } catch (final IOException ioe) {
                        log.error("failed to finish connecting", ioe);
                        close(selectedKey);
                        continue;
                    }
                    selectedKey.attach(_CalcMessage.newBufferForClient());
                    selectedKey.interestOps(SelectionKey.OP_WRITE);
                }
                // --------------------------------------------------------------------------- write
                if (selectedKey.isWritable()) {
                    final var channel = (SocketChannel) selectedKey.channel();
                    final var attachment = (ByteBuffer) selectedKey.attachment();
                    assert attachment.hasRemaining();
                    try {
                        final int w = channel.write(attachment);
                        assert w > 0; // why?
                    } catch (final IOException ioe) {
                        log.error("failed to write", ioe);
                        close(selectedKey);
                        continue;
                    }
                    if (!attachment.hasRemaining()) {
                        assert attachment.position() == _CalcMessage.LENGTH_REQUEST;
                        selectedKey.interestOpsAnd(~SelectionKey.OP_WRITE);
                        attachment.limit(attachment.position() + _CalcMessage.LENGTH_RESPONSE);
                        selectedKey.interestOps(SelectionKey.OP_READ);
                    }
                }
                // ---------------------------------------------------------------------------- read
                if (selectedKey.isReadable()) {
                    final var channel = (SocketChannel) selectedKey.channel();
                    final var attachment = (ByteBuffer) selectedKey.attachment();
                    try {
                        final int r = channel.read(attachment);
                        if (r == -1) {
                            log.error("unexpected eof");
                            close(selectedKey);
                            continue;
                        }
                    } catch (final IOException ioe) {
                        log.error("failed to read from {}", channel, ioe);
                        close(selectedKey);
                        continue;
                    }
                    if (!attachment.hasRemaining()) {
                        selectedKey.interestOpsAnd(~SelectionKey.OP_READ);
                        close(selectedKey);
                        _CalcMessage.log(attachment);
                    }
                }
            }
        }
    }

    public static void main(final String... args) throws IOException {
        try (var selector = Selector.open()) {
            sub(selector);
        }
    }
}
