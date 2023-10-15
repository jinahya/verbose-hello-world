package com.github.jinahya.hello.misc.c03calc;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class CalcTcp3Client {

    public static void main(final String... args) throws IOException {
        try (var selector = Selector.open()) {
            for (int c = 0; c < _CalcConstants.NUMBER_OF_REQUESTS; c++) {
                final var client = SocketChannel.open();
                client.configureBlocking(false);
                // ---------------------------------------------------------------------------- bond
                if (ThreadLocalRandom.current().nextBoolean()) {
                    client.bind(new InetSocketAddress(_CalcConstants.HOST, 0));
                }
                // --------------------------------------------------------------------- connect/try
                final SelectionKey clientKey;
                final var connectedImmediately = client.connect(_CalcConstants.ADDR);
                if (connectedImmediately) {
                    clientKey = client.register(selector, SelectionKey.OP_WRITE);
                } else {
                    clientKey = client.register(selector, SelectionKey.OP_CONNECT);
                }
                clientKey.attach(_CalcUtils.newBufferForClient());
                // -------------------------------------------------------------------------- select
                while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                    if (selector.select(_CalcConstants.SELECT_TIMEOUT_MILLIS) == 0) {
                        continue;
                    }
                    for (final var i = selector.selectedKeys().iterator(); i.hasNext(); ) {
                        final var selectedKey = i.next();
                        i.remove();
                        // ---------------------------------------------------------- connect/finish
                        if (selectedKey.isConnectable()) {
                            final var channel = (SocketChannel) selectedKey.channel();
                            try {
                                final var connected = channel.finishConnect();
                                assert connected;
                            } catch (final IOException ioe) {
                                log.error("failed to connect", ioe);
                                channel.close();
                                assert !selectedKey.isValid();
                                continue;
                            }
                            selectedKey.interestOps(SelectionKey.OP_WRITE);
                        }
                        // ------------------------------------------------------------------- write
                        if (selectedKey.isWritable()) {
                            final var channel = (SocketChannel) selectedKey.channel();
                            final var attachment = (ByteBuffer) selectedKey.attachment();
                            final int w = channel.write(attachment);
                            if (!attachment.hasRemaining()) {
                                selectedKey.interestOpsAnd(~SelectionKey.OP_WRITE);
                                attachment.limit(attachment.capacity());
                                assert attachment.remaining() == Integer.BYTES;
                                selectedKey.interestOps(SelectionKey.OP_READ);
                            }
                        }
                        // -------------------------------------------------------------------- read
                        if (selectedKey.isReadable()) {
                            final var channel = (SocketChannel) selectedKey.channel();
                            final var attachment = (ByteBuffer) selectedKey.attachment();
                            final int r = channel.read(attachment);
                            if (r == -1) {
                                log.error("unexpected eof");
                                channel.close();
                                assert !selectedKey.isValid();
                                continue;
                            }
                            if (!attachment.hasRemaining()) {
                                selectedKey.interestOpsAnd(~SelectionKey.OP_READ);
                                channel.close();
                                assert !selectedKey.isValid();
                                _CalcUtils.log(attachment.flip());
                            }
                        }
                    }
                }
            }
        }
    }
}
