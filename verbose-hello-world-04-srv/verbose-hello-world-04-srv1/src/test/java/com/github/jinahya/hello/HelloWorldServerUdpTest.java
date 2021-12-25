package com.github.jinahya.hello;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ServiceLoader;

@Slf4j
class HelloWorldServerUdpTest {

    @Test
    void test() throws IOException, InterruptedException {
        final InetAddress host = InetAddress.getLoopbackAddress();
        final IHelloWorldServer server;
        {
            final HelloWorld service = ServiceLoader.load(HelloWorld.class).iterator().next();
            final SocketAddress endpoint = new InetSocketAddress(host, 0);
            server = new HelloWorldServerUdp(service, endpoint);
        }
        server.open();
        final int port = HelloWorldServerUdp.LOCAL_PORT.get();
        final SocketAddress endpoint = new InetSocketAddress(host, port);
        HelloWorldClientUdp.clients(endpoint, 8, b -> {
            // TODO: Implement!
        });
        server.close();
    }
}
