package com.github.jinahya.hello.api._java_io;

import com.github.jinahya.hello.api.*;
import com.github.jinahya.hello.api.annotations.*;
import org.junit.jupiter.api.*;

import java.io.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;

@_HideFromPublishing
class _HelloWorld_Java_Io__Test extends HelloWorld__Test {

    @BeforeEach
    void __stubService() throws IOException {
        write_outputstream_writes_hello_world_bytes(service());
    }
}
