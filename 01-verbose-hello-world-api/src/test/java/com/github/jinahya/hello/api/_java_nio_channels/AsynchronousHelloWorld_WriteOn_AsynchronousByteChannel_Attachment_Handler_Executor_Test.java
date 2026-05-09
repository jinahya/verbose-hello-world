package com.github.jinahya.hello.api._java_nio_channels;

import com.github.jinahya.hello.api.AsynchronousHelloWorld;
import com.github.jinahya.hello.api.AsynchronousHelloWorldTest;
import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

/**
 * A class for testing
 * {@link AsynchronousHelloWorld#writeOn(AsynchronousByteChannel, Object, CompletionHandler,
 * Executor)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("writeOn(channel, attachment, handler, executor)")
@SuppressWarnings({"removal"})
@Slf4j
class AsynchronousHelloWorld_WriteOn_AsynchronousByteChannel_Attachment_Handler_Executor_Test
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
                () -> service.writeOn(channel, null, handler, INLINE)
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
                () -> service.writeOn(channel, null, handler, INLINE)
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
                () -> service.writeOn(channel, null, handler, executor)
        );
    }

    /**
     * Verifies that the method writes all {@value HelloWorld#BYTES} bytes to the {@code channel}
     * across one or more partial writes, and finally invokes
     * {@link CompletionHandler#completed(Object, Object) handler.completed(channel, attachment)}.
     */
    @DisplayName("""
            should write all <hello-world-bytes> across partial writes,
            and invoke <handler.completed(channel, attachment)>"""
    )
    @Test
    @SuppressWarnings({"unchecked"})
    void __completed() {
        // ----------------------------------------------------------------------------------- given
        HelloWorldTestUtils.put_buffer_will_increase_buffer_position_by_12(service());
        final AsynchronousHelloWorld service = AsynchronousHelloWorld.from(service());
        final var channel = Mockito.mock(AsynchronousByteChannel.class);
        final var written = new LongAdder();
        Mockito.doAnswer(i -> {
            final var src = i.getArgument(0, ByteBuffer.class);
            final var a = i.getArgument(1);
            final var h = i.getArgument(2, CompletionHandler.class);
            Thread.ofPlatform().start(() -> {
                final var n = ThreadLocalRandom.current().nextInt(src.remaining()) + 1;
                src.position(src.position() + n);
                written.add(n);
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
        service.writeOn(channel, attachment, handler, INLINE);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(handler, Mockito.timeout(TimeUnit.SECONDS.toMillis(8L)).times(1))
                .completed(channel, attachment);
        Mockito.verify(handler, Mockito.never())
                .failed(ArgumentMatchers.any(), ArgumentMatchers.any());
        Assertions.assertEquals(HelloWorld.BYTES, written.intValue());
        final var srcCaptor = ArgumentCaptor.forClass(ByteBuffer.class);
        Mockito.verify(channel, Mockito.atLeastOnce()).write(
                srcCaptor.capture(),
                ArgumentMatchers.any(),
                ArgumentMatchers.notNull()
        );
        final var srcs = srcCaptor.getAllValues();
        srcs.forEach(s -> Assertions.assertSame(srcs.getFirst(), s));
    }

    /**
     * Verifies that the method invokes
     * {@link CompletionHandler#failed(Throwable, Object) handler.failed(exc, attachment)} when the
     * {@code channel} fails to write.
     */
    @DisplayName("""
            should invoke <handler.failed(exc, attachment)>
            when the <channel> fails to write"""
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
                ArgumentMatchers.argThat(b -> b != null && b.hasRemaining()), // <src>
                ArgumentMatchers.any(),                                       // <attachment>
                ArgumentMatchers.notNull()                                    // <handler>
        );
        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
        final var handler = (CompletionHandler<AsynchronousByteChannel, Object>)
                Mockito.mock(CompletionHandler.class);
        // ------------------------------------------------------------------------------------ when
        service.writeOn(channel, attachment, handler, INLINE);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(handler, Mockito.timeout(TimeUnit.SECONDS.toMillis(8L)).times(1))
                .failed(exc, attachment);
        Mockito.verify(handler, Mockito.never())
                .completed(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    /**
     * Verifies that {@code .exceptionally(...)} routes a synchronous failure during buffer
     * preparation (inside the {@code applyAsync} mapper) to
     * {@link CompletionHandler#failed(Throwable, Object) handler.failed(exc, attachment)}.
     */
    @DisplayName("""
            should invoke <handler.failed(exc, attachment)>
            when buffer preparation fails synchronously"""
    )
    @Test
    @SuppressWarnings({"unchecked"})
    void __failedDuringPrep() {
        // ----------------------------------------------------------------------------------- given
        // make <service.put(buffer)> throw, so the prep stage of writeOn fails synchronously.
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
        service.writeOn(channel, attachment, handler, INLINE);
        // ------------------------------------------------------------------------------------ then
        // <handler.failed(...)> is invoked exactly once with the underlying cause; the
        // CompletionStage pipeline's <.exceptionally(...)> may unwrap or wrap the throwable, so
        // we assert on the cause chain rather than identity.
        final var thrownCaptor = ArgumentCaptor.forClass(Throwable.class);
        Mockito.verify(handler, Mockito.timeout(TimeUnit.SECONDS.toMillis(8L)).times(1))
                .failed(thrownCaptor.capture(), ArgumentMatchers.any());
        final var thrown = thrownCaptor.getValue();
        Assertions.assertTrue(
                thrown == exc || thrown.getCause() == exc,
                "expected " + exc + " to be the (cause of the) thrown: " + thrown
        );
        Mockito.verify(handler, Mockito.never())
                .completed(ArgumentMatchers.any(), ArgumentMatchers.any());
    }
}
