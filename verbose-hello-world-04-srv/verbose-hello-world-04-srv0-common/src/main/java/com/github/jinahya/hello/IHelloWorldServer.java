package com.github.jinahya.hello;

import java.io.Closeable;
import java.io.IOException;

public interface IHelloWorldServer extends Closeable {

    void open() throws IOException;
}
