package com.github.jinahya.hello.api._java_nio_channels;

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 * A class for testing {@link HelloWorld#send(DatagramSocket) send(socket)} method with
 * {@link MulticastSocket}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/net/MulticastSocket.html">MulticastSocket</a>
 */
@Slf4j
class HelloWorld_Send_MulticastSocket_Test extends HelloWorldTest {

    private static final String MULTICAST_HOST = "228.5.6.7";

    private static final int MULTICAST_PORT = 6789;

    // ---------------------------------------------------------------------------------------------
    @DisplayName("send via connected MulticastSocket to multiple receivers")
    @Test
    void __() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        stub_set_array_will_set_actual_hello_world_bytes();
        final var mcastaddr = new InetSocketAddress(
                InetAddress.getByName(MULTICAST_HOST),
                MULTICAST_PORT
        );
        final var netIf = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
        assert netIf != null;
        final var count = ThreadLocalRandom.current().nextInt(2, 6);
        final var latch = new CountDownLatch(count);
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, count).forEach(i -> executor.execute(() -> {
                try (var receiver = new MulticastSocket(MULTICAST_PORT)) {
                    receiver.setReuseAddress(true);
                    receiver.joinGroup(mcastaddr, netIf);
                    latch.countDown();
                    final var packet = new DatagramPacket(new byte[HelloWorld.BYTES],
                                                          HelloWorld.BYTES);
                    receiver.receive(packet);
                    final var message = new String(
                            packet.getData(),
                            0,
                            packet.getLength(),
                            StandardCharsets.US_ASCII
                    );
                    log.debug("[{}] received: {}", i, message);
                    receiver.leaveGroup(mcastaddr, netIf);
                } catch (final IOException ioe) {
                    log.error("[{}] failed to receive", i, ioe);
                }
            }));
            latch.await();
            try (var sender = new MulticastSocket()) {
                sender.connect(mcastaddr);
                assert sender.isConnected();
                // ---------------------------------------------------------------------------- when
                final var result = service.send(sender);
                // ---------------------------------------------------------------------------- then
                Assertions.assertSame(sender, result);
            }
        }
    }
}
