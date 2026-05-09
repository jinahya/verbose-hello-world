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
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * A class for testing
 * {@link AsynchronousHelloWorld#write(AsynchronousByteChannel, Object, CompletionHandler,
 * Executor) write(channel, attachment, handler, executor)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(channel, attachment, handler, executor)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class AsynchronousHelloWorld_Write_AsynchronousByteChannel_Attachment_Handler_Executor_Test
        extends AsynchronousHelloWorldTest {

    /**
     * An {@link Executor} that runs each submitted {@link Runnable} inline on the calling thread.
     */
    private static final Executor INLINE = Runnable::run;

    /**
     * Verifies that the method throws a {@link NullPointerException} when the {@code channel}
     * argument is {@code null}.
     */
    @DisplayName("should throw NullPointerException when channel is null")
    @Test
    @SuppressWarnings({"unchecked"})
    void _ThrowNullPointerException_ChannelIsNull() {
        // ----------------------------------------------------------------------------------- given
        final AsynchronousHelloWorld service = asynchronousService();
        final var channel = (AsynchronousByteChannel) null;
        final var handler = (CompletionHandler<AsynchronousByteChannel, Object>)
                Mockito.mock(CompletionHandler.class);
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.write(channel, null, handler, INLINE)
        );
    }

    /**
     * Verifies that the method throws a {@link NullPointerException} when the {@code handler}
     * argument is {@code null}.
     */
    @DisplayName("should throw NullPointerException when handler is null")
    @Test
    void _ThrowNullPointerException_HandlerIsNull() {
        // ----------------------------------------------------------------------------------- given
        final AsynchronousHelloWorld service = asynchronousService();
        final var channel = Mockito.mock(AsynchronousByteChannel.class);
        final var handler = (CompletionHandler<AsynchronousByteChannel, Object>) null;
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.write(channel, null, handler, INLINE)
        );
    }

    /**
     * Verifies that the method throws a {@link NullPointerException} when the {@code executor}
     * argument is {@code null}.
     */
    @DisplayName("should throw NullPointerException when executor is null")
    @Test
    @SuppressWarnings({"unchecked"})
    void _ThrowNullPointerException_ExecutorIsNull() {
        // ----------------------------------------------------------------------------------- given
        final AsynchronousHelloWorld service = asynchronousService();
        final var channel = Mockito.mock(AsynchronousByteChannel.class);
        final var handler = (CompletionHandler<AsynchronousByteChannel, Object>)
                Mockito.mock(CompletionHandler.class);
        final var executor = (Executor) null;
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.write(channel, null, handler, executor)
        );
    }

    /**
     * Verifies that on successful I/O the method invokes
     * {@link CompletionHandler#completed(Object, Object) handler.completed(channel, attachment)}
     * and the returned {@link java.util.concurrent.CompletableFuture} completes with the
     * {@code channel}.
     */
    @DisplayName("""
            should invoke <handler.completed(channel, attachment)>
            and the returned future should complete with <channel>"""
    )
    @Test
    @SuppressWarnings({"unchecked"})
    void __completed() throws Exception {
        // ----------------------------------------------------------------------------------- given
        HelloWorldTestUtils.put_buffer_will_increase_buffer_position_by_12(service());
        final AsynchronousHelloWorld service = AsynchronousHelloWorld.from(service());
        final var channel = Mockito.mock(AsynchronousByteChannel.class);
        // stub: <channel.write(src, a, h)> drains <src> on a worker thread, invokes <h.completed>
        Mockito.doAnswer(i -> {
            final var src = i.getArgument(0, ByteBuffer.class);
            final var a = i.getArgument(1);
            final var h = i.getArgument(2, CompletionHandler.class);
            Thread.ofPlatform().start(() -> {
                final var n = src.remaining();
                src.position(src.position() + n);
                h.completed(n, a);
            });
            return null;
        }).when(channel).write(
                ArgumentMatchers.argThat(b -> b != null && b.hasRemaining()), // <src>
                ArgumentMatchers.any(),                                       // <attachment>
                ArgumentMatchers.notNull()                                    // <handler>
        );
        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
        final var handler = (CompletionHandler<AsynchronousByteChannel, Object>)
                Mockito.mock(CompletionHandler.class);
        // ------------------------------------------------------------------------------------ when
        final var future = service.write(channel, attachment, handler, INLINE);
        // ------------------------------------------------------------------------------------ then
        // future completes with the channel within the timeout
        final var result = future.get(8L, TimeUnit.SECONDS);
        Assertions.assertSame(channel, result);
        // <handler.completed(channel, attachment)> is invoked exactly once
        Mockito.verify(handler, Mockito.times(1)).completed(channel, attachment);
        Mockito.verify(handler, Mockito.never())
                .failed(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    /**
     * Verifies that on channel-write failure the method invokes
     * {@link CompletionHandler#failed(Throwable, Object) handler.failed(exc, attachment)} and the
     * returned future completes exceptionally with the same cause.
     */
    @DisplayName("""
            should invoke <handler.failed(exc, attachment)>
            and the returned future should complete exceptionally with <exc>"""
    )
    @Test
    @SuppressWarnings({"unchecked"})
    void __failed() {
        // ----------------------------------------------------------------------------------- given
        HelloWorldTestUtils.put_buffer_will_increase_buffer_position_by_12(service());
        final AsynchronousHelloWorld service = AsynchronousHelloWorld.from(service());
        final var channel = Mockito.mock(AsynchronousByteChannel.class);
        final var exc = new RuntimeException("simulated write failure");
        Mockito.doAnswer(i -> {
            final var a = i.getArgument(1);
            final var h = i.getArgument(2, CompletionHandler.class);
            Thread.ofPlatform().start(() -> h.failed(exc, a));
            return null;
        }).when(channel).write(
                ArgumentMatchers.argThat(b -> b != null && b.hasRemaining()),
                ArgumentMatchers.any(),
                ArgumentMatchers.notNull()
        );
        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
        final var handler = (CompletionHandler<AsynchronousByteChannel, Object>)
                Mockito.mock(CompletionHandler.class);
        // ------------------------------------------------------------------------------------ when
        final var future = service.write(channel, attachment, handler, INLINE);
        // ------------------------------------------------------------------------------------ then
        // future completes exceptionally with <exc>
        final var thrown = Assertions.assertThrows(
                ExecutionException.class,
                () -> future.get(8L, TimeUnit.SECONDS)
        );
        Assertions.assertSame(exc, thrown.getCause());
        // <handler.failed(exc, attachment)> is invoked exactly once
        Mockito.verify(handler, Mockito.times(1)).failed(exc, attachment);
        Mockito.verify(handler, Mockito.never())
                .completed(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    /**
     * Verifies that a synchronous failure during buffer preparation routes the throwable to
     * both {@code handler.failed(...)} and {@code future.completeExceptionally(...)}.
     */
    @DisplayName("""
            should route a synchronous prep failure
            to <handler.failed> and to <future.completeExceptionally>"""
    )
    @Test
    @SuppressWarnings({"unchecked"})
    void __failedDuringPrep() {
        // ----------------------------------------------------------------------------------- given
        // make <service.put(buffer)> throw, so the prep stage of write fails synchronously.
        final var exc = new RuntimeException("simulated prep failure");
        Mockito.doThrow(exc)
                .when(service())
                .<ByteBuffer>put(ArgumentMatchers.argThat(
                        b -> b != null && b.remaining() >= HelloWorld.BYTES
                ));
        final AsynchronousHelloWorld service = AsynchronousHelloWorld.from(service());
        final var channel = Mockito.mock(AsynchronousByteChannel.class);
        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
        final var handler = (CompletionHandler<AsynchronousByteChannel, Object>)
                Mockito.mock(CompletionHandler.class);
        // ------------------------------------------------------------------------------------ when
        final var future = service.write(channel, attachment, handler, INLINE);
        // ------------------------------------------------------------------------------------ then
        // future completes exceptionally with <exc> as the cause
        final var thrown = Assertions.assertThrows(
                ExecutionException.class,
                () -> future.get(8L, TimeUnit.SECONDS)
        );
        Assertions.assertSame(exc, thrown.getCause());
        // <handler.failed(exc, attachment)> is invoked exactly once
        Mockito.verify(handler, Mockito.timeout(TimeUnit.SECONDS.toMillis(8L)).times(1))
                .failed(exc, attachment);
        Mockito.verify(handler, Mockito.never())
                .completed(ArgumentMatchers.any(), ArgumentMatchers.any());
        // and <channel.write(...)> is never invoked because prep failed before dispatch
        Mockito.verify(channel, Mockito.never()).write(
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.any()
        );
    }
}
