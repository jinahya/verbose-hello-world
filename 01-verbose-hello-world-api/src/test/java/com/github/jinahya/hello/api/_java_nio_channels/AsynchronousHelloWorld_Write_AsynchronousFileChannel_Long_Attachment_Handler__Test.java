package com.github.jinahya.hello.api._java_nio_channels;

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

import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@畵蛇添足
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class AsynchronousHelloWorld_Write_AsynchronousFileChannel_Long_Attachment_Handler__Test
        extends AsynchronousHelloWorldTest {

    @BeforeEach
    @SuppressWarnings({"unchecked"})
    void __() { // @formatter:off
        Mockito.doAnswer(i -> {
            var channel = i.getArgument(0, AsynchronousFileChannel.class);
            var position = i.getArgument(1, Long.class);
            var attachment = i.getArgument(2);
            var handler = i.getArgument(3, CompletionHandler.class);
            var src = HelloWorldTestUtils.hello_world_byte_buffer();
            channel.write(src, position, position, new CompletionHandler<Integer, Long>() {
                @Override
                public void completed(Integer n, Long p) {
                    if (src.hasRemaining()) {
                        long next = p + n;
                        channel.write(src, next, next, this);
                        return;
                    }
                    handler.completed(channel, attachment);
                }
                @Override
                public void failed(Throwable t, Long p) {
                    handler.failed(t, attachment);
                }
            });
            return null;
        }).when(asynchronousService()).write(
                ArgumentMatchers.<AsynchronousFileChannel>notNull(),
                ArgumentMatchers.anyLong(),
                ArgumentMatchers.any(),
                ArgumentMatchers.<CompletionHandler<AsynchronousFileChannel, Object>>notNull()
        ); // @formatter:on
    }

    @Test
    void __(@TempDir final Path dir) throws Exception { // @formatter:off
        var file = Files.createTempFile(dir, null, null);
        var done = new CompletableFuture<AsynchronousFileChannel>();
        try (var channel = AsynchronousFileChannel.open(file, StandardOpenOption.WRITE)) {
            asynchronousService().write(channel, 0L, null, new CompletionHandler<>() {
                @Override
                public void completed(AsynchronousFileChannel c, Object a) {
                    done.complete(c);
                }
                @Override
                public void failed(Throwable t, Object a) {
                    done.completeExceptionally(t);
                }
            });
            Assertions.assertSame(channel, done.get(8L, TimeUnit.SECONDS));
        }
        Assertions.assertEquals(HelloWorld.BYTES, Files.size(file)); // @formatter:on
    }
}
