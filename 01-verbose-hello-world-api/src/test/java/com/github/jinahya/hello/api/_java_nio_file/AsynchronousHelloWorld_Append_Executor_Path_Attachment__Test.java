package com.github.jinahya.hello.api._java_nio_file;

import com.github.jinahya.hello.api.AsynchronousHelloWorldTest;
import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class AsynchronousHelloWorld_Append_Executor_Path_Attachment__Test
        extends AsynchronousHelloWorldTest {

    @Test
    void __(final @TempDir Path tempDir) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var asynchronousService = asynchronousService();
        HelloWorldTestUtils.append_path_appends_hello_world(synchronousService());
        final var path = Files.createTempFile(tempDir, null, null);
        HelloWorldTestUtils.writeSome(path);
        final var size = Files.size(path);
        final var executor = Executors.newSingleThreadExecutor();
        try {
            final var attachment = new Object();
            // -------------------------------------------------------------------------------- when
            final var future = asynchronousService.append(executor, path, attachment);
            // -------------------------------------------------------------------------------- then
            Assertions.assertSame(attachment, future.toCompletableFuture().get(8L, TimeUnit.SECONDS));
            Assertions.assertEquals(
                    size + HelloWorld.BYTES,
                    Files.size(path)
            );
        } finally {
            executor.shutdown();
        }
    }
}
