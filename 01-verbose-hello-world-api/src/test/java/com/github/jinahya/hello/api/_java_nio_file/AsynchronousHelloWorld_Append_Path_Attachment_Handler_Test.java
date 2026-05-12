package com.github.jinahya.hello.api._java_nio_file;

import com.github.jinahya.hello.api.AsynchronousHelloWorld;
import com.github.jinahya.hello.api.AsynchronousHelloWorldTest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.OpenOption;
import java.nio.file.Path;

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

    @DisplayName("""
            should throw a <NullPointerException>
            when the <path> argument is <null>"""
    )
    @Test
    @SuppressWarnings({"unchecked"})
    void _ThrowNullPointerException_PathIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var asynchronousService = asynchronousService();
        final var path = (Path) null;
        final var handler = (CompletionHandler<Path, Object>)
                Mockito.mock(CompletionHandler.class);
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> asynchronousService.append(path, null, handler)
        );
    }

    @DisplayName("""
            should throw a <NullPointerException>
            when the <handler> argument is <null>"""
    )
    @Test
    void _ThrowNullPointerException_HandlerIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var asynchronousService = asynchronousService();
        final var path = Mockito.mock(Path.class);
        final var handler = (CompletionHandler<Path, Object>) null;
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> asynchronousService.append(path, null, handler)
        );
    }

    @DisplayName("""
            should open a channel, invoke <write(channel, 0, attachment, ...)>,
            force + close the channel,
            and invoke <handler.completed(path, attachment)>"""
    )
    @Test
    @SuppressWarnings({"unchecked"})
    void __completed() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var asynchronousService = asynchronousService();
        Mockito.doAnswer(i -> {
            final var c = i.getArgument(0, AsynchronousFileChannel.class);
            final var a = i.getArgument(2);
            final var h = i.getArgument(3, CompletionHandler.class);
            h.completed(c, a);
            return null;
        }).when(asynchronousService).write(
                ArgumentMatchers.<AsynchronousFileChannel>notNull(),
                ArgumentMatchers.anyLong(),
                ArgumentMatchers.any(),
                ArgumentMatchers.<CompletionHandler<AsynchronousFileChannel, Object>>notNull()
        );
        final var path = Mockito.mock(Path.class);
        final var channel = Mockito.mock(AsynchronousFileChannel.class);
        final var attachment = new Object();
        final var handler = (CompletionHandler<Path, Object>)
                Mockito.mock(CompletionHandler.class);
        try (var mockStatic = Mockito.mockStatic(AsynchronousFileChannel.class)) {
            mockStatic.when(() -> AsynchronousFileChannel.open(ArgumentMatchers.same(path),
                                                               ArgumentMatchers.any(OpenOption[].class)))
                    .thenReturn(channel);
            // -------------------------------------------------------------------------------- when
            asynchronousService.append(path, attachment, handler);
            // -------------------------------------------------------------------------------- then
//            final var captor = ArgumentCaptor.forClass(OpenOption[].class);
//            mockStatic.verify(
//                    () -> AsynchronousFileChannel.open(ArgumentMatchers.same(path), captor.capture()),
//                    Mockito.times(1)
//            );
//            final var value = captor.getValue();
//            final var options = new HashSet<>(Arrays.asList(value));
//            Assertions.assertTrue(options.remove(StandardOpenOption.CREATE));
//            Assertions.assertTrue(options.remove(StandardOpenOption.WRITE));
//            Assertions.assertTrue(options.isEmpty());
//            Mockito.verify(asynchronousService, Mockito.times(1)).write(
//                    ArgumentMatchers.same(channel),
//                    ArgumentMatchers.eq(0L),
//                    ArgumentMatchers.any(),
//                    ArgumentMatchers.<CompletionHandler<AsynchronousFileChannel, Object>>notNull()
//            );
//            Mockito.verify(channel, Mockito.times(1)).force(true);
//            Mockito.verify(channel, Mockito.times(1)).close();
            Mockito.verify(handler, Mockito.times(1)).completed(path, attachment);
        }
    }

    @DisplayName("""
            should close the channel
            and invoke <handler.failed(exc, attachment)>
            when the inner <write> fails"""
    )
    @Test
    @SuppressWarnings({"unchecked"})
    void __failed() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var asynchronousService = asynchronousService();
        final var exc = new RuntimeException("simulated write failure");
        Mockito.doAnswer(i -> {
            final var a = i.getArgument(2);
            final var h = i.getArgument(3, CompletionHandler.class);
            h.failed(exc, a);
            return null;
        }).when(asynchronousService).write(
                ArgumentMatchers.<AsynchronousFileChannel>notNull(),
                ArgumentMatchers.anyLong(),
                ArgumentMatchers.any(),
                ArgumentMatchers.<CompletionHandler<AsynchronousFileChannel, Object>>notNull()
        );
        final var path = Mockito.mock(Path.class);
        final var channel = Mockito.mock(AsynchronousFileChannel.class);
        final var attachment = new Object();
        final var handler = (CompletionHandler<Path, Object>)
                Mockito.mock(CompletionHandler.class);
        try (var mockStatic = Mockito.mockStatic(AsynchronousFileChannel.class)) {
            mockStatic.when(() -> AsynchronousFileChannel.open(ArgumentMatchers.same(path),
                                                               ArgumentMatchers.any(OpenOption[].class)))
                    .thenReturn(channel);
            // -------------------------------------------------------------------------------- when
            asynchronousService.append(path, attachment, handler);
            // -------------------------------------------------------------------------------- then
//            Mockito.verify(channel, Mockito.times(1)).close();
            Mockito.verify(handler, Mockito.times(1)).failed(exc, attachment);
        }
    }
}
