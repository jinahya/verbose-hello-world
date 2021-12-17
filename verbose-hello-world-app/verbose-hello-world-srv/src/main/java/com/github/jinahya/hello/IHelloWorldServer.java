package com.github.jinahya.hello;

import java.io.IOException;

public interface IHelloWorldServer {

    void start() throws IOException;

    void stop() throws IOException;

    boolean isStarted();
}
