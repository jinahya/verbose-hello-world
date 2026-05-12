package com.github.jinahya.hello.api._java_nio_channels;

import com.github.jinahya.hello.api.AsynchronousHelloWorld;
import com.github.jinahya.hello.api.AsynchronousHelloWorldTest;
import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import com.github.jinahya.hello.api.畵蛇添足;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@畵蛇添足
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class AsynchronousHelloWorld_Write_Executor_AsynchronousFileChannel_Long_Attachment__Test
        extends AsynchronousHelloWorldTest {

    @BeforeEach
    void __() { // @formatter:off
        Mockito.doAnswer(i -> {
            var channel = i.getArgument(1, AsynchronousFileChannel.class);
            var position = i.getArgument(2, Long.class);
            var attachment = i.getArgument(3);
            var future = new CompletableFuture<>();
            var src = HelloWorldTestUtils.hello_world_byte_buffer();
            channel.write(src, position, position, new CompletionHandler<>() {
                @Override
                public void completed(Integer n, Long p) {
                    if (src.hasRemaining()) {
                        long next = p + n;
                        channel.write(src, next, next, this);
                        return;
                    }
                    future.complete(attachment);
                }
                @Override
                public void failed(Throwable t, Long p) {
                    future.completeExceptionally(t);
                }
            });
            return future;
        }).when(asynchronousService()).write(
                ArgumentMatchers.notNull(),
                ArgumentMatchers.notNull(),
                ArgumentMatchers.anyLong(),
                ArgumentMatchers.any()
        ); // @formatter:on
    }

    @Test
    void __(@TempDir final Path dir) throws Exception { // @formatter:off
        var file = Files.createTempFile(dir, null, null);
        try (var executor = Executors.newSingleThreadExecutor(
                Thread.ofPlatform().name("exec-", 0).factory());
             var channel = AsynchronousFileChannel.open(file, StandardOpenOption.WRITE)) {
            AsynchronousHelloWorld asynchronousService = asynchronousService();
            var attachment = new Object();
            var result = asynchronousService.write(executor, channel, 0L, attachment).toCompletableFuture().get(8L, TimeUnit.SECONDS);
            Assertions.assertSame(attachment, result);
        }
        Assertions.assertEquals(HelloWorld.BYTES, Files.size(file)); // @formatter:on
    }
}
