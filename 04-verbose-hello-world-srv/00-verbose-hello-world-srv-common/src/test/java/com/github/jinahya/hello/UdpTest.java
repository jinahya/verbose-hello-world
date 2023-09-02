package com.github.jinahya.hello;

import com.github.jinahya.hello.miscellaneous.Rfc768Constants;
import com.github.jinahya.hello.miscellaneous.Rfc791Constants;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.net.DatagramSocket;
import java.net.StandardSocketOptions;
import java.nio.channels.DatagramChannel;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class UdpTest {

    private static final int MAX_LENGTH =
            (-1 >>> Rfc768Constants.HEADER_LENGTH_SIZE)
            - Rfc768Constants.HEADER_BYTES
            - Rfc791Constants.MIN_HEADER_BYTES;

    @Test
    void __DatagramSocket() throws Exception {
        try (DatagramSocket socket = new DatagramSocket()) {
            var sendBufferSize = socket.getSendBufferSize();
            log.debug("sendBufferSize: {}", sendBufferSize);
            assertTrue(sendBufferSize >= MAX_LENGTH);
            var receiveBufferSize = socket.getReceiveBufferSize();
            log.debug("receiveBufferSize: {}", receiveBufferSize);
            assertTrue(receiveBufferSize >= MAX_LENGTH);
        }
    }

    @Test
    void __DatagramChanel() throws Exception {
        try (DatagramChannel channel = DatagramChannel.open()) {
            var sendBufferSize = channel.getOption(StandardSocketOptions.SO_SNDBUF);
            log.debug("sendBufferSize: {}", sendBufferSize);
            assertTrue(sendBufferSize >= MAX_LENGTH);
            var receiveBufferSize = channel.getOption(StandardSocketOptions.SO_RCVBUF);
            log.debug("receiveBufferSize: {}", receiveBufferSize);
            assertTrue(receiveBufferSize >= MAX_LENGTH);
        }
    }
}
