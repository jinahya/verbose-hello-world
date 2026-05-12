package com.github.jinahya.hello.api._java_nio_channels;

import com.github.jinahya.hello.api.AsynchronousHelloWorld;
import com.github.jinahya.hello.api.AsynchronousHelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * A class for testing
 * {@link AsynchronousHelloWorld#write(AsynchronousFileChannel, long, Object) write(channel,
 * position, attachment)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(channel, position, attachment)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class AsynchronousHelloWorld_Write_AsynchronousFileChannel_Long_Attachment_Test
        extends AsynchronousHelloWorldTest {

    @DisplayName("should throw NullPointerException when channel is null")
    @Test
    void _ThrowNullPointerException_ChannelIsNull() {
        final var asynchronousService = asynchronousService();
        final var channel = (AsynchronousFileChannel) null;
        final var position = 0L;
        Assertions.assertThrows(
                NullPointerException.class,
                () -> asynchronousService.write(channel, position, null)
        );
    }

    @DisplayName("should throw IllegalArgumentException when position is negative")
    @Test
    void _ThrowIllegalArgumentException_PositionIsNegative() {
        final var asynchronousService = asynchronousService();
        final var channel = Mockito.mock(AsynchronousFileChannel.class);
        final var position = ThreadLocalRandom.current().nextLong() | Long.MIN_VALUE;
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> asynchronousService.write(channel, position, null)
        );
    }

    @DisplayName("""
            should complete the returned stage with the <attachment>
            once all <hello-world-bytes> have been written"""
    )
    @Test
    @SuppressWarnings({"unchecked"})
    void __completed() throws Exception {
        HelloWorldTestUtils.put_buffer_will_increase_buffer_position_by_12(synchronousService());
        final var asynchronousService = asynchronousService();
        final var channel = Mockito.mock(AsynchronousFileChannel.class);
        Mockito.doAnswer(i -> {
            final var src = i.getArgument(0, ByteBuffer.class);
            final var position = i.getArgument(1, Long.class);
            final var attachment = i.getArgument(2);
            final var handler = i.getArgument(3, CompletionHandler.class);
            Thread.ofPlatform().start(() -> {
                final var n = ThreadLocalRandom.current().nextInt(src.remaining()) + 1;
                src.position(src.position() + n);
                handler.completed(n, attachment);
            });
            return null;
        }).when(channel).write(
                ArgumentMatchers.argThat(b -> b != null && b.hasRemaining()),
                ArgumentMatchers.anyLong(),
                ArgumentMatchers.any(),
                ArgumentMatchers.notNull()
        );
        final var position = ThreadLocalRandom.current().nextLong(1024L);
        final var attachment = new Object();
        final var stage = asynchronousService.write(channel, position, attachment);
        Assertions.assertSame(attachment, stage.toCompletableFuture().get(8L, TimeUnit.SECONDS));
    }

    @DisplayName("""
            should complete the returned stage exceptionally
            when the <channel> fails"""
    )
    @Test
    @SuppressWarnings({"unchecked"})
    void __failed() {
        HelloWorldTestUtils.put_buffer_will_increase_buffer_position_by_12(synchronousService());
        final var asynchronousService = asynchronousService();
        final var channel = Mockito.mock(AsynchronousFileChannel.class);
        final var exc = new RuntimeException("simulated write failure");
        Mockito.doAnswer(i -> {
            final var attachment = i.getArgument(2);
            final var handler = i.getArgument(3, CompletionHandler.class);
            Thread.ofPlatform().start(() -> handler.failed(exc, attachment));
            return null;
        }).when(channel).write(
                ArgumentMatchers.argThat(b -> b != null && b.hasRemaining()),
                ArgumentMatchers.anyLong(),
                ArgumentMatchers.any(),
                ArgumentMatchers.notNull()
        );
        final var position = ThreadLocalRandom.current().nextLong(1024L);
        final var attachment = new Object();
        final var stage = asynchronousService.write(channel, position, attachment);
        final var cause = Assertions.assertThrows(
                ExecutionException.class,
                () -> stage.toCompletableFuture().get(8L, TimeUnit.SECONDS)
        ).getCause();
        Assertions.assertSame(exc, cause);
    }
}
