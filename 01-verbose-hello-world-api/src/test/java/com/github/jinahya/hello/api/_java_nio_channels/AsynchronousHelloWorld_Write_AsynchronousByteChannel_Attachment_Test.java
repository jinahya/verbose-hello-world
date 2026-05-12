package com.github.jinahya.hello.api._java_nio_channels;

import com.github.jinahya.hello.api.AsynchronousHelloWorld;
import com.github.jinahya.hello.api.AsynchronousHelloWorldTest;
import com.github.jinahya.hello.api.HelloWorld;
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
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * A class for testing
 * {@link AsynchronousHelloWorld#write(AsynchronousByteChannel, Object) write(channel, attachment)}
 * method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(channel, attachment)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class AsynchronousHelloWorld_Write_AsynchronousByteChannel_Attachment_Test
        extends AsynchronousHelloWorldTest {

    /**
     * Verifies that the method throws a {@link NullPointerException} when the {@code channel}
     * argument is {@code null}.
     */
    @DisplayName("should throw NullPointerException when channel is null")
    @Test
    void _ThrowNullPointerException_ChannelIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var asynchronousService = asynchronousService();
        final var channel = (AsynchronousByteChannel) null;
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> asynchronousService.write(channel, null)
        );
    }

    /**
     * Verifies that the returned {@link java.util.concurrent.CompletionStage stage} completes with
     * the supplied {@code attachment} once all {@value HelloWorld#BYTES} bytes have been written
     * across one or more partial writes.
     */
    @DisplayName("""
            should complete the returned stage with the <attachment>
            once all <hello-world-bytes> have been written"""
    )
    @Test
    @SuppressWarnings({"unchecked"})
    void __completed() throws Exception {
        // ----------------------------------------------------------------------------------- given
        HelloWorldTestUtils.put_buffer_will_increase_buffer_position_by_12(synchronousService());
        final var asynchronousService = asynchronousService();
        final var channel = Mockito.mock(AsynchronousByteChannel.class);
        Mockito.doAnswer(i -> {
            final var src = i.getArgument(0, ByteBuffer.class);
            final var a = i.getArgument(1);
            final var h = i.getArgument(2, CompletionHandler.class);
            Thread.ofPlatform().start(() -> {
                final var n = ThreadLocalRandom.current().nextInt(src.remaining()) + 1;
                src.position(src.position() + n);
                h.completed(n, a);
            });
            return null;
        }).when(channel).write(
                ArgumentMatchers.argThat(b -> b != null && b.hasRemaining()), // <src>
                ArgumentMatchers.any(),                                       // <attachment>
                ArgumentMatchers.notNull()                                    // <handler>
        );
        final var attachment = new Object();
        // ------------------------------------------------------------------------------------ when
        final var stage = asynchronousService.write(channel, attachment);
        // ------------------------------------------------------------------------------------ then
        Assertions.assertSame(attachment, stage.toCompletableFuture().get(8L, TimeUnit.SECONDS));
    }

    /**
     * Verifies that the returned {@link java.util.concurrent.CompletionStage stage} completes with
     * {@code null} when {@code null} is passed as the attachment.
     */
    @DisplayName("""
            should complete the returned stage with <null>
            when the <attachment> is <null>"""
    )
    @Test
    @SuppressWarnings({"unchecked"})
    void __completedNullAttachment() throws Exception {
        // ----------------------------------------------------------------------------------- given
        HelloWorldTestUtils.put_buffer_will_increase_buffer_position_by_12(synchronousService());
        final var asynchronousService = asynchronousService();
        final var channel = Mockito.mock(AsynchronousByteChannel.class);
        Mockito.doAnswer(i -> {
            final var src = i.getArgument(0, ByteBuffer.class);
            final var a = i.getArgument(1);
            final var h = i.getArgument(2, CompletionHandler.class);
            Thread.ofPlatform().start(() -> {
                final var n = ThreadLocalRandom.current().nextInt(src.remaining()) + 1;
                src.position(src.position() + n);
                h.completed(n, a);
            });
            return null;
        }).when(channel).write(
                ArgumentMatchers.argThat(b -> b != null && b.hasRemaining()),
                ArgumentMatchers.any(),
                ArgumentMatchers.notNull()
        );
        // ------------------------------------------------------------------------------------ when
        final var stage = asynchronousService.write(channel, null);
        // ------------------------------------------------------------------------------------ then
        Assertions.assertNull(stage.toCompletableFuture().get(8L, TimeUnit.SECONDS));
    }

    /**
     * Verifies that the returned {@link java.util.concurrent.CompletionStage stage} completes
     * exceptionally when the {@code channel} fails — possibly synchronously on the first
     * invocation, or asynchronously after one or more partial writes.
     */
    @DisplayName("""
            should complete the returned stage exceptionally
            when the <channel> fails on or after partial writes"""
    )
    @Test
    @SuppressWarnings({"unchecked"})
    void __failed() {
        // ----------------------------------------------------------------------------------- given
        HelloWorldTestUtils.put_buffer_will_increase_buffer_position_by_12(synchronousService());
        final var asynchronousService = asynchronousService();
        final var channel = Mockito.mock(AsynchronousByteChannel.class);
        final var exc = new RuntimeException("simulated write failure");
        Mockito.doAnswer(i -> {
            final var src = i.getArgument(0, ByteBuffer.class);
            final var a = i.getArgument(1);
            final var h = i.getArgument(2, CompletionHandler.class);
            Thread.ofPlatform().start(() -> {
                final var n = ThreadLocalRandom.current().nextInt(src.remaining()) + 1;
                src.position(src.position() + n);
                if (!src.hasRemaining() || ThreadLocalRandom.current().nextBoolean()) {
                    h.failed(exc, a);
                    return;
                }
                h.completed(n, a);
            });
            return null;
        }).when(channel).write(
                ArgumentMatchers.argThat(b -> b != null && b.hasRemaining()),
                ArgumentMatchers.any(),
                ArgumentMatchers.notNull()
        );
        final var attachment = new Object();
        // ------------------------------------------------------------------------------------ when
        final var stage = asynchronousService.write(channel, attachment);
        // ------------------------------------------------------------------------------------ then
        final var cause = Assertions.assertThrows(
                ExecutionException.class,
                () -> stage.toCompletableFuture().get(8L, TimeUnit.SECONDS)
        ).getCause();
        Assertions.assertSame(exc, cause);
    }

    /**
     * Verifies that passing the {@code channel} itself as the {@code attachment} yields a stage
     * that completes with the same channel — the idiom that takes the place of the (removed)
     * stage-of-channel overload.
     */
    @DisplayName("""
            stage completes with the <channel> when the <channel> is passed as the <attachment>"""
    )
    @Test
    @SuppressWarnings({"unchecked"})
    void __completedChannelAsAttachment() throws Exception {
        // ----------------------------------------------------------------------------------- given
        HelloWorldTestUtils.put_buffer_will_increase_buffer_position_by_12(synchronousService());
        final var asynchronousService = asynchronousService();
        final var channel = Mockito.mock(AsynchronousByteChannel.class);
        Mockito.doAnswer(i -> {
            final var src = i.getArgument(0, ByteBuffer.class);
            final var a = i.getArgument(1);
            final var h = i.getArgument(2, CompletionHandler.class);
            Thread.ofPlatform().start(() -> {
                final var n = ThreadLocalRandom.current().nextInt(src.remaining()) + 1;
                src.position(src.position() + n);
                h.completed(n, a);
            });
            return null;
        }).when(channel).write(
                ArgumentMatchers.argThat(b -> b != null && b.hasRemaining()),
                ArgumentMatchers.any(),
                ArgumentMatchers.notNull()
        );
        // ------------------------------------------------------------------------------------ when
        final var stage = asynchronousService.write(channel, channel);
        // ------------------------------------------------------------------------------------ then
        Assertions.assertSame(channel, stage.toCompletableFuture().get(8L, TimeUnit.SECONDS));
    }
}
