package com.github.jinahya.hello;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

@Slf4j
class HelloWorldMainUdpTest {

    @Test
    void main__() throws IOException {
        IHelloWorldServerUtils.writeQuitToClose(() -> {
            HelloWorldMainUdp.main("0.0.0.0", "0");
            final InetAddress host = InetAddress.getLocalHost();
            final int port = HelloWorldServerUdp.LOCAL_PORT.get();
            final SocketAddress endpoint = new InetSocketAddress(host, port);
            HelloWorldClientUdp.clients(8, endpoint, array -> {
                // TODO: Verify array!
            });
            return null;
        });
    }
}
