package com.github.jinahya.hello.miscellaneous.c03chat;

import com.github.jinahya.hello.HelloWorldServerConstants;
import com.github.jinahya.hello.util.HelloWorldLangUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
class ChatTcp2Server {

    static class Attachment {

        private void copyToMultiple(byte[] array) {
            if (Objects.requireNonNull(array, "array is null").length != _ChatMessage.BYTES) {
                throw new IllegalArgumentException(
                        "array.length(" + array.length + ") != " + _ChatMessage.BYTES);
            }
            buffers.add(ByteBuffer.wrap(Arrays.copyOf(array, array.length)));
        }

        private void copyToMultiple(ByteBuffer buffer) {
            if (!Objects.requireNonNull(buffer, "buffer is null").hasArray()) {
                throw new IllegalArgumentException("buffer does not have a backing array");
            }
            copyToMultiple(buffer.array());
        }

        void copyToMultiple(Attachment attachment) {
            copyToMultiple(Objects.requireNonNull(attachment, "attachment is null").buffer);
        }

        void copyBufferToMultipleAndClearBuffer() {
            copyToMultiple(buffer);
            Arrays.fill(buffer.array(), (byte) 0);
            buffer.clear();
        }

        final ByteBuffer buffer = _ChatMessage.newBuffer();

        final List<ByteBuffer> buffers = new ArrayList<>();
    }

    public static void main(String... args) throws Exception {
        try (var selector = Selector.open();
             var server = ServerSocketChannel.open()) {
            server.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.TRUE);
            server.setOption(StandardSocketOptions.SO_REUSEPORT, Boolean.TRUE);
            server.bind(
                    new InetSocketAddress(InetAddress.getByName("0.0.0.0"), _ChatConstants.PORT)
            );
            log.debug("[S] bound to {}", server.getLocalAddress());
            server.configureBlocking(false);
            var serverKey = server.register(selector, SelectionKey.OP_ACCEPT);
            HelloWorldLangUtils.runWhenRead(
                    () -> {
                        serverKey.cancel();
                        selector.wakeup();
                    },
                    HelloWorldServerConstants.QUIT,
                    null
            );
            while (serverKey.isValid()) {
                if (selector.select(TimeUnit.SECONDS.toMillis(8L)) == 0) {
                    continue;
                }
                for (var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                    var selectedKey = i.next();
                    if (selectedKey.isAcceptable()) {
                        var channel = (ServerSocketChannel) selectedKey.channel();
                        var client = channel.accept(); // IOException
                        log.debug("[S] accepted from {} through {}", client.getRemoteAddress(),
                                  client.getLocalAddress());
                        var attachment = new Attachment();
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_READ, attachment);
                        continue;
                    }
                    if (selectedKey.isReadable()) {
                        var channel = (SocketChannel) selectedKey.channel();
                        var attachment = (Attachment) selectedKey.attachment();
                        var read = channel.read(attachment.buffer);
                        if (read == -1) {
                            channel.close();
                            assert !selectedKey.isValid();
                            continue;
                        } else if (!attachment.buffer.hasRemaining()) {
                            selector.keys().stream()
                                    .filter(k -> k.channel() instanceof SocketChannel)
                                    .filter(SelectionKey::isValid)
                                    .forEach(k -> {
                                        ((Attachment) k.attachment()).copyToMultiple(attachment);
                                        k.interestOpsOr(SelectionKey.OP_WRITE);
                                    });
                            attachment.buffer.clear();
                        }
                    }
                    if (selectedKey.isWritable() && selectedKey.isValid()) {
                        var channel = (SocketChannel) selectedKey.channel();
                        var attachment = (Attachment) selectedKey.attachment();
                        assert !attachment.buffers.isEmpty();
                        var buffer = attachment.buffers.get(0);
                        assert buffer.hasRemaining();
                        channel.write(buffer);
                        if (!buffer.hasRemaining()) {
                            attachment.buffers.remove(buffer);
                            if (attachment.buffers.isEmpty()) {
                                selectedKey.interestOpsAnd(~SelectionKey.OP_WRITE);
                            }
                        }
                    }
                }
            }
            selector.keys().stream()
                    .filter(k -> k.channel() instanceof SocketChannel)
                    .filter(SelectionKey::isValid)
                    .forEach(k -> {
                        var channel = k.channel();
                        try {
                            channel.close();
                        } catch (IOException ioe) {
                            log.error("[S] failed to close {}", channel);
                        }
//                        k.cancel();
                    });
        }
    }
}
