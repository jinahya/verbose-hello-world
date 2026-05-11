package com.github.jinahya.hello.api._java_nio_channels;

import com.github.jinahya.hello.api.AsynchronousHelloWorld;
import com.github.jinahya.hello.api.AsynchronousHelloWorldTest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A class for testing
 * {@link AsynchronousHelloWorld#append(Path, Object, CompletionHandler) append(path, attachment,
 * handler)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("append(path, attachment, handler)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class AsynchronousHelloWorld_Append_Path_Attachment_Handler_Test
        extends AsynchronousHelloWorldTest {

    @DisplayName("should throw NullPointerException when path is null")
    @Test
    @SuppressWarnings({"unchecked"})
    void _ThrowNullPointerException_PathIsNull() {
        final AsynchronousHelloWorld service = asynchronousService();
        final var path = (Path) null;
        final var handler = (CompletionHandler<Path, Object>)
                Mockito.mock(CompletionHandler.class);
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.append(path, null, handler)
        );
    }

    @DisplayName("should throw NullPointerException when handler is null")
    @Test
    void _ThrowNullPointerException_HandlerIsNull(@TempDir final Path dir) {
        final AsynchronousHelloWorld service = asynchronousService();
        final var path = dir.resolve("test.dat");
        final var handler = (CompletionHandler<Path, Object>) null;
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.append(path, null, handler)
        );
    }

    @DisplayName("""
            should open a channel, invoke <write(channel, position=0, attachment, ...)>,
            force + close the channel,
            and invoke <handler.completed(path, attachment)>"""
    )
    @Test
    @SuppressWarnings({"unchecked"})
    void __completed(@TempDir final Path dir) throws Exception {
        final var service = asynchronousService();
        final var file = Files.createTempFile(dir, null, null);
        final var captured = new AtomicReference<AsynchronousFileChannel>();
        Mockito.doAnswer(i -> {
            final var c = i.getArgument(0, AsynchronousFileChannel.class);
            final var a = i.getArgument(2);
            final var h = i.getArgument(3, CompletionHandler.class);
            captured.set(c);
            h.completed(c, a);
            return null;
        }).when(service).write(
                ArgumentMatchers.<AsynchronousFileChannel>notNull(),
                ArgumentMatchers.anyLong(),
                ArgumentMatchers.any(),
                ArgumentMatchers.<CompletionHandler<AsynchronousFileChannel, Object>>notNull()
        );
        final var attachment = new Object();
        final var handler = (CompletionHandler<Path, Object>)
                Mockito.mock(CompletionHandler.class);
        service.append(file, attachment, handler);
        Mockito.verify(handler, Mockito.timeout(TimeUnit.SECONDS.toMillis(8L)).times(1))
                .completed(file, attachment);
        Mockito.verify(handler, Mockito.never())
                .failed(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(service, Mockito.times(1)).write(
                ArgumentMatchers.<AsynchronousFileChannel>notNull(),
                ArgumentMatchers.eq(0L),
                ArgumentMatchers.any(),
                ArgumentMatchers.<CompletionHandler<AsynchronousFileChannel, Object>>notNull()
        );
        Assertions.assertFalse(captured.get().isOpen());
    }

    @DisplayName("""
            should close the channel
            and invoke <handler.failed(exc, attachment)>
            when the inner <write> fails"""
    )
    @Test
    @SuppressWarnings({"unchecked"})
    void __failed(@TempDir final Path dir) throws Exception {
        final var service = asynchronousService();
        final var file = Files.createTempFile(dir, null, null);
        final var exc = new RuntimeException("simulated write failure");
        final var captured = new AtomicReference<AsynchronousFileChannel>();
        Mockito.doAnswer(i -> {
            final var c = i.getArgument(0, AsynchronousFileChannel.class);
            final var a = i.getArgument(2);
            final var h = i.getArgument(3, CompletionHandler.class);
            captured.set(c);
            h.failed(exc, a);
            return null;
        }).when(service).write(
                ArgumentMatchers.<AsynchronousFileChannel>notNull(),
                ArgumentMatchers.anyLong(),
                ArgumentMatchers.any(),
                ArgumentMatchers.<CompletionHandler<AsynchronousFileChannel, Object>>notNull()
        );
        final var attachment = new Object();
        final var handler = (CompletionHandler<Path, Object>)
                Mockito.mock(CompletionHandler.class);
        service.append(file, attachment, handler);
        Mockito.verify(handler, Mockito.timeout(TimeUnit.SECONDS.toMillis(8L)).times(1))
                .failed(exc, attachment);
        Mockito.verify(handler, Mockito.never())
                .completed(ArgumentMatchers.any(), ArgumentMatchers.any());
        Assertions.assertFalse(captured.get().isOpen());
    }
}
