package com.github.jinahya.hello;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@Slf4j
class HelloWorldMainUdpTest {

    @Test
    void main__() throws IOException {
        IHelloWorldServerUtils.writeQuitToClose(() -> {
            HelloWorldMainUdp.main();
            HelloWorldServerUdpTest.connect(16);
            return null;
        });
    }
}
