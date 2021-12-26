package com.github.jinahya.hello;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ServiceLoader;

@Slf4j
class HelloWorldServerUdpTest {

    @Test
    void test() throws IOException, InterruptedException {
        final InetAddress host = InetAddress.getLoopbackAddress();
        final IHelloWorldServer server;
        {
            final HelloWorld service = Mockito.spy(ServiceLoader.load(HelloWorld.class).iterator().next());
            Mockito.when(service.set(Mockito.notNull()))
                    .thenAnswer(i -> {
                        final byte[] array = i.getArgument(0);
                        final byte[] src = "hello, world".getBytes(StandardCharsets.US_ASCII);
                        System.arraycopy(src, 0, array, 0, src.length);
                        return array;
                    });
            final SocketAddress endpoint = new InetSocketAddress(host, 0);
            server = new HelloWorldServerUdp(service, endpoint);
        }
        server.open();
        final int port = HelloWorldServerUdp.LOCAL_PORT.get();
        final SocketAddress endpoint = new InetSocketAddress(host, port);
        HelloWorldClientUdp.clients(4, endpoint, b -> {
            Assertions.assertArrayEquals("hello, world".getBytes(StandardCharsets.US_ASCII), b);
        });
        server.close();
    }
}
