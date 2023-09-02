package com.github.jinahya.hello;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Slf4j
class HelloWorldTcp0Client {

    public static void main(String... args) throws Exception {
        InetAddress host;
        try {
            host = InetAddress.getByName(args[0]);
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            host = InetAddress.getLoopbackAddress();
        }
        try (Socket client = new Socket()) {
            client.connect(new InetSocketAddress(host, HelloWorldServerConstants.PORT),
                           (int) HelloWorldServerConstants.CONNECT_TIMEOUT_IN_MILLIS);
            log.debug("connected to {}, through {}", client.getRemoteSocketAddress(),
                      client.getLocalSocketAddress());
            client.setSoTimeout((int) HelloWorldServerConstants.READ_TIMEOUT_IN_MILLIS);
            var array = client.getInputStream().readNBytes(HelloWorld.BYTES);
            Optional.ofNullable(System.console()).ifPresent(
                    c -> c.printf("%1$s%n", new String(array, StandardCharsets.US_ASCII))
            );
        }
    }

    private HelloWorldTcp0Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
