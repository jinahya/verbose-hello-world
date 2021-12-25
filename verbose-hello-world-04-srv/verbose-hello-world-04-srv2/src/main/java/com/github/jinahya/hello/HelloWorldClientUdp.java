package com.github.jinahya.hello;

import lombok.extern.slf4j.Slf4j;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
public class HelloWorldClientUdp implements Callable<byte[]> {

    static void clients(final SocketAddress endpoint, final int count, final Consumer<byte[]> consumer)
            throws InterruptedException {
        Objects.requireNonNull(endpoint, "endpoint is null");
        if (count <= 0) {
            throw new IllegalArgumentException("count(" + count + ") is not positive");
        }
        Objects.requireNonNull(consumer, "consumer is null");
        final CountDownLatch latch = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            new Thread(() -> {
                try {
                    final byte[] b = new HelloWorldClientUdp(endpoint).call();
                    consumer.accept(b);
                } catch (final Exception e) {
                    log.error("failed to call for {}", endpoint, e);
                } finally {
                    latch.countDown();
                }
            }).start();
        }
        if (!latch.await(1L, TimeUnit.MINUTES)) {
            log.warn("latch is still not broken!");
        }
    }

    /**
     * Creates a new instance which connects to specified endpoint.
     *
     * @param endpoint the endpoint to connect.
     */
    HelloWorldClientUdp(final SocketAddress endpoint) {
        super();
        this.endpoint = Objects.requireNonNull(endpoint, "endpoint is null");
    }

    @Override
    public byte[] call() throws Exception {
        final byte[] array = new byte[HelloWorld.BYTES];
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(10000); // 10 sec
            socket.send(new DatagramPacket(new byte[0], 0, endpoint));
            log.debug("[C] send to {}", endpoint);
            final DatagramPacket packet = new DatagramPacket(array, array.length);
            socket.receive(packet);
            log.debug("[C] received from {}", packet.getSocketAddress());
        }
        return array;
    }

    /**
     * The endpoint on which the server is listening.
     */
    private final SocketAddress endpoint;
}
