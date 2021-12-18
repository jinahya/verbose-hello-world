package com.github.jinahya.hello;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@Slf4j
class HelloWorldMainTcpTest {

    @Disabled
    @Test
    void main__() throws IOException {
        IHelloWorldServerUtils.writeQuitToClose(() -> {
            HelloWorldMainTcp.main();
            HelloWorldServerTcpTest.connect(16);
            return null;
        });
    }
}
