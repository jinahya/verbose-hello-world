package com.github.jinahya.hello.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
class JavaNetTest {

    @Test
    void __() throws IOException {
        final var addrIPv4 = InetAddress.getByName("127.0.0.1");
        log.debug("addrIPv4: {} {}", addrIPv4, addrIPv4.getClass());
        final var addrIPv6 = InetAddress.getByName("::1");
        log.debug("addrIPv6: {} {}", addrIPv6, addrIPv6.getClass());
        final var port = 63000;
        // -----------------------------------------------------------------------------------------
        {
            final var serverIPv4 = new Thread(() -> {
                try {
                    try (var server = new ServerSocket()) {
                        server.bind(new InetSocketAddress(addrIPv4, port));
                        log.debug("bound to {}", server.getLocalSocketAddress());
                        try (var client = server.accept()) {
                            log.debug("accepted from {}", client.getRemoteSocketAddress());
                        }
                    }
                } catch (final IOException ioe) {
                    throw new RuntimeException(ioe);
                }
            });
            serverIPv4.start();
        }
        // -----------------------------------------------------------------------------------------
        {
            final var serverIPv6 = new Thread(() -> {
                try {
                    try (var server = new ServerSocket()) {
                        server.bind(new InetSocketAddress(addrIPv6, port));
                        log.debug("bound to {}", server.getLocalSocketAddress());
                        try (var client = server.accept()) {
                            log.debug("accepted from {}", client.getRemoteSocketAddress());
                        }
                    }
                } catch (final IOException ioe) {
                    throw new RuntimeException(ioe);
                }
            });
            serverIPv6.start();
        }
        // -----------------------------------------------------------------------------------------
        {
            final var clientIPv4 = new Thread(() -> {
                try {
                    try (var client = new Socket()) {
                        client.connect(new InetSocketAddress(addrIPv4, port));
                        log.debug("connected to {}", client.getRemoteSocketAddress());
                    }
                } catch (final IOException ioe) {
                    throw new RuntimeException(ioe);
                }
            });
            clientIPv4.start();
        }
        // -----------------------------------------------------------------------------------------
        {
            final var clientIPv6 = new Thread(() -> {
                try {
                    try (var client = new Socket()) {
                        client.connect(new InetSocketAddress(addrIPv6, port));
                        log.debug("connected to {}", client.getRemoteSocketAddress());
                    }
                } catch (final IOException ioe) {
                    throw new RuntimeException(ioe);
                }
            });
            clientIPv6.start();
        }
    }
}
