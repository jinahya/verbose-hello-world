package com.github.jinahya.hello.api._java_util_jar;

import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Java_Util_Jar__Test
        extends HelloWorldTest {

    @TempDir
    private static File tempDir;

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void __() throws IOException {
        HelloWorldTestUtils.write_stream_will_write_actual_hello_world_bytes(service());
    }

    @Nested
    class JarOutputStream_Test {

        @Test
        void __JarInputStream() {
            // TODO: create a temp file
            // TODO: write using write(ZipOutputStream)
            // TODO: read using jar inputstream
        }

        @Test
        void __JarFile() {
            // TODO: create a temp file
            // TODO: write using write(ZipOutputStream)
            // TODO: read using jar inputstream
        }
    }
}
