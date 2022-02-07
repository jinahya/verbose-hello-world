package com.github.jinahya.hello;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import static java.net.InetAddress.getLoopbackAddress;

// 2022-02-07 Windows 와 MacOS 에서 다른 행태를 보인다!!
@Slf4j
class DatagramServer {

    public static void main(final String[] args) throws IOException {
        final DatagramSocket socket = new DatagramSocket(null);
        socket.bind(new InetSocketAddress(getLoopbackAddress(), 0));
        log.debug("bound to {}", socket.getLocalSocketAddress());
        new Thread(() -> {
            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(4L));
                socket.close();
            } catch (final InterruptedException ie) {
                log.error("interrupted", ie);
                Thread.currentThread().interrupt();
            }
        }).start();
        while (!socket.isClosed()) {
            final var packet = new DatagramPacket(new byte[0], 0);
            try {
                socket.receive(packet);
                log.debug("received from {}", packet.getSocketAddress());
            } catch (final IOException ioe) {
                if (socket.isClosed()) {
                    break;
                }
                log.error("failed to receive", ioe);
            }
        }
    }
}
