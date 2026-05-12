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
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class AsynchronousHelloWorld_Append_Path_Attachment_Handler__Test
        extends AsynchronousHelloWorldTest {

    @Test
    @SuppressWarnings({"unchecked"})
    void __(final @TempDir Path tempDir) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var asynchronousService = asynchronousService();
        Mockito.doAnswer(i -> {
            final var c = i.getArgument(0, AsynchronousFileChannel.class);
            var p = i.getArgument(1, Long.class);
            final var a = i.getArgument(2);
            final var h = i.getArgument(3, CompletionHandler.class);
            for (final var b = ByteBuffer.allocate(HelloWorld.BYTES); b.hasRemaining(); ) {
                p += c.write(b, p).get();
            }
            h.completed(c, a);
            return null;
        }).when(asynchronousService).write(
                ArgumentMatchers.notNull(),
                ArgumentMatchers.anyLong(),
                ArgumentMatchers.any(),
                ArgumentMatchers.<CompletionHandler<AsynchronousFileChannel, Object>>notNull()
        );
        final var path = Files.createTempFile(tempDir, null, null);
        HelloWorldTestUtils.writeSome(path);
        final var size = Files.size(path);
        final var attachment = new Object();
        final var handler = (CompletionHandler<Path, Object>) Mockito.mock(CompletionHandler.class);
        // ------------------------------------------------------------------------------------ when
        asynchronousService.append(path, attachment, handler);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(handler, Mockito.timeout(TimeUnit.SECONDS.toMillis(8L)).times(1))
                .completed(path, attachment);
        Assertions.assertEquals(
                size + HelloWorld.BYTES,
                Files.size(path)
        );
    }
}
