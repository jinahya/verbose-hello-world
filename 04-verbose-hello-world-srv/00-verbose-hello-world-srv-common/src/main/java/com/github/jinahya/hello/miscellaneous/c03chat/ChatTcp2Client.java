package com.github.jinahya.hello.miscellaneous.c03chat;

import com.github.jinahya.hello.HelloWorldServerUtils;
import com.github.jinahya.hello.util.HelloWorldLangUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

@Slf4j
class ChatTcp2Client {

    // @formatter:off
    private static class Attachment extends ChatTcp2Server.Attachment {
    }
    // @formatter:on

    public static void main(String... args) throws Exception {
        InetAddress addr;
        try {
            addr = InetAddress.getByName(args[0]);
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            addr = InetAddress.getLoopbackAddress();
        }
        try (var selector = Selector.open();
             var client = SocketChannel.open()) {
            SelectionKey clientKey;
            client.configureBlocking(false);
            if (client.connect(new InetSocketAddress(addr, _ChatConstants.PORT))) {
                log.debug("(immediately) connected to {}, through {}", client.getRemoteAddress(),
                          client.getLocalAddress());
                clientKey = client.register(selector, SelectionKey.OP_READ, new Attachment());
            } else {
                clientKey = client.register(selector, SelectionKey.OP_CONNECT);
            }
            HelloWorldLangUtils.readLinesAndCallWhenTests(
                    HelloWorldServerUtils::isQuit, // <predicate>
                    () -> {                        // <callable>
                        clientKey.cancel();
                        assert !clientKey.isValid();
                        selector.wakeup();
                        return null;
                    },
                    l -> {                         // <consumer>
                        var attachment = ((Attachment) clientKey.attachment());
                        if (attachment == null) { // not connected yet.
                            return;
                        }
                        var buffer = _ChatMessage.OfBuffer.of(_ChatUtils.prependUsername(l));
                        attachment.buffers.add(buffer);
                        clientKey.interestOpsOr(SelectionKey.OP_WRITE);
                        selector.wakeup();
                    }
            );
            while (clientKey.isValid()) {
                if (selector.select(TimeUnit.SECONDS.toMillis(8L)) == 0) {
                    continue;
                }
                for (var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                    var key = i.next();
                    if (key.isConnectable()) {
                        var channel = (SocketChannel) key.channel();
                        var connected = channel.finishConnect();
                        assert connected;
                        log.debug("connected to {}, through {}", channel.getRemoteAddress(),
                                  channel.getLocalAddress());
                        key.interestOpsAnd(~SelectionKey.OP_CONNECT);
                        key.attach(new Attachment());
                        key.interestOpsOr(SelectionKey.OP_READ);
                        continue;
                    }
                    if (key.isReadable()) {
                        var channel = (SocketChannel) key.channel();
                        assert channel == client;
                        var attachment = (Attachment) key.attachment();
                        var r = channel.read(attachment.buffer);
                        if (r == -1) {
                            channel.close();
                            assert !clientKey.isValid();
                            continue;
                        }
                        if (!attachment.buffer.hasRemaining()) {
                            _ChatMessage.OfBuffer.printToSystemOut(attachment.buffer);
                            attachment.buffer.clear();
                        }
                    }
                    if (key.isWritable()) {
                        var channel = (SocketChannel) key.channel();
                        assert channel == client;
                        var attachment = (Attachment) key.attachment();
                        assert !attachment.buffers.isEmpty();
                        var buffer = attachment.buffers.get(0);
                        assert buffer.hasRemaining();
                        var w = channel.write(buffer);
                        assert w > 0;
                        if (!buffer.hasRemaining()) {
                            attachment.buffers.remove(0);
                            if (attachment.buffers.isEmpty()) {
                                key.interestOpsAnd(~SelectionKey.OP_WRITE);
                            }
                        }
                    }
                }
            }
        }
    }
}
