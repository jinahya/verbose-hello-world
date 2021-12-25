package com.github.jinahya.hello;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ServiceLoader;

@Slf4j
class HelloWorldServerTcpTest {

    @Test
    void test() throws IOException, InterruptedException {
        final InetAddress host = InetAddress.getLocalHost();
        final IHelloWorldServer server;
        {
            final HelloWorld service = ServiceLoader.load(HelloWorld.class).iterator().next();
            final SocketAddress endpoint = new InetSocketAddress(host, 0);
            final int backlog = 50;
            server = new HelloWorldServerTcp(service, endpoint, backlog);
        }
        server.open();
        final int port = HelloWorldServerTcp.LOCAL_PORT.get();
        final SocketAddress endpoint = new InetSocketAddress(host, port);
        HelloWorldClientTcp.clients(4, endpoint, b -> {
            // TODO: Implement!
        });
        server.close();
    }
}
