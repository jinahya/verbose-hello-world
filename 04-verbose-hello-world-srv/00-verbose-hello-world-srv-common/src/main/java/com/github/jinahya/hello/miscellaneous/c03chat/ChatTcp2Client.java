package com.github.jinahya.hello.miscellaneous.c03chat;

import com.github.jinahya.hello.HelloWorldServerConstants;
import com.github.jinahya.hello.miscellaneous.c03chat.ChatTcp2Server.Attachment;
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
                log.debug("[C] connected (immediately) to {}, through {}",
                          client.getRemoteAddress(), client.getLocalAddress());
                clientKey = client.register(selector, SelectionKey.OP_READ);
            } else {
                clientKey = client.register(selector, SelectionKey.OP_CONNECT);
            }
            clientKey.attach(new Attachment());
            HelloWorldLangUtils.callWhenRead(
                    v -> !Thread.currentThread().isInterrupted(),
                    HelloWorldServerConstants.QUIT,
                    () -> {
                        clientKey.channel().close();
                        assert !clientKey.isValid();
                        return null;
                    },
                    m -> {
                        var buffer = _ChatMessage.newBuffer(_ChatUtils.prependUsername(m));
                        ((Attachment) clientKey.attachment()).buffers.add(buffer);
                        clientKey.interestOpsOr(SelectionKey.OP_WRITE);
                        selector.wakeup();
                    }
            );
            while (clientKey.isValid()) {
                if (selector.select(TimeUnit.SECONDS.toMillis(8L)) == 0) {
                    continue;
                }
                for (var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                    var selectedKey = i.next();
                    if (selectedKey.isConnectable()) {
                        var channel = (SocketChannel) selectedKey.channel();
                        var connected = channel.finishConnect(); // IOException
                        assert connected;
                        log.debug("[C] connected to {}, through {}", channel.getRemoteAddress(),
                                  channel.getLocalAddress());
                        selectedKey.interestOpsAnd(~SelectionKey.OP_CONNECT);
                        selectedKey.interestOpsOr(SelectionKey.OP_READ);
                        continue;
                    }
                    if (selectedKey.isReadable()) {
                        var channel = (SocketChannel) selectedKey.channel();
                        var attachment = (Attachment) selectedKey.attachment();
                        var r = channel.read(attachment.buffer); // IOException
                        if (r == -1) {
                            channel.close();
                            assert !clientKey.isValid();
                            break;
                        }
                        if (!attachment.buffer.hasRemaining()) {
                            System.out.printf("%1$s%n", _ChatMessage.toString(attachment.buffer));
                            attachment.buffer.clear();
                        }
                    }
                    if (selectedKey.isWritable()) {
                        var channel = (SocketChannel) selectedKey.channel();
                        var attachment = (Attachment) selectedKey.attachment();
                        assert !attachment.buffers.isEmpty();
                        var buffer = attachment.buffers.get(0);
                        assert buffer.hasRemaining();
                        var w = channel.write(buffer);
                        if (!buffer.hasRemaining()) {
                            attachment.buffers.remove(0);
                            if (attachment.buffers.isEmpty()) {
                                selectedKey.interestOpsAnd(~SelectionKey.OP_WRITE);
                                assert (selectedKey.interestOps() & SelectionKey.OP_WRITE) == 0;
                            }
                        }
                    }
                }
            }
        }
    }
}
