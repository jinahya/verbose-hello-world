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
            HelloWorldMainUdp.main();
            final SocketAddress endpoint = new InetSocketAddress(
                    InetAddress.getLoopbackAddress(),
                    ((InetSocketAddress) HelloWorldServerUdp.ENDPOINT.get()).getPort());
            HelloWorldServerUdpTest.connect(endpoint, 16);
            return null;
        });
    }
}
