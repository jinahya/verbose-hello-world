package com.github.jinahya.hello;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

@Slf4j
class HelloWorldMainTcpTest {

    @Test
    void main__() throws IOException {
        IHelloWorldServerUtils.writeQuitToClose(() -> {
            HelloWorldMainTcp.main("0.0.0.0", "0");
            final InetAddress host = InetAddress.getLocalHost();
            final int port = HelloWorldServerTcp.LOCAL_PORT.get();
            final SocketAddress endpoint = new InetSocketAddress(host, port);
            HelloWorldClientTcp.clients(8, endpoint, b -> {
                // TODO: Verify array!
            });
            return null;
        });
    }
}
