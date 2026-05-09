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
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

/**
 * A class for testing
 * {@link AsynchronousHelloWorld#write(AsynchronousByteChannel, Object, CompletionHandler)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(channel, attachment, handler)")
@Slf4j
class AsynchronousHelloWorld_Write_AsynchronousByteChannel_Attachment_Handler_Test
        extends AsynchronousHelloWorldTest {

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
                () -> service.write(channel, null, handler)
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
                () -> service.write(channel, null, handler)
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
        // stub <service.put(buffer)> to advance the buffer's <position> by HelloWorld.BYTES, so
        // that the impl's <service.put(allocate).flip()> chain yields a flipped buffer with
        // <position=0, limit=BYTES, remaining=BYTES>.
        HelloWorldTestUtils.put_buffer_will_increase_buffer_position_by_12(service());
        // bypass the parent's <Mockito.spy(...)> wrapping and use AsynchronousHelloWorld.from()
        // directly with our @Spy HelloWorld; this avoids the double-spy chain that interferes
        // with self-invocation interception.
        final AsynchronousHelloWorld service = AsynchronousHelloWorld.from(service());
        final var channel = Mockito.mock(AsynchronousByteChannel.class);
        // total bytes acknowledged across all partial writes
        final var written = new LongAdder();
        // stub: each <channel.write(src, a, h)> kicks off a thread that
        //       advances <src> by a random 1..remaining bytes,
        //       then invokes <h.completed(n, a)>.
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
        service.write(channel, attachment, handler);
        // ------------------------------------------------------------------------------------ then
        // <handler.completed(channel, attachment)> is invoked exactly once within the timeout
        Mockito.verify(handler, Mockito.timeout(TimeUnit.SECONDS.toMillis(8L)).times(1))
                .completed(channel, attachment);
        // <handler.failed(?, ?)> is never invoked
        Mockito.verify(handler, Mockito.never())
                .failed(ArgumentMatchers.any(), ArgumentMatchers.any());
        // total bytes acknowledged equals HelloWorld.BYTES
        Assertions.assertEquals(HelloWorld.BYTES, written.intValue());
        // <channel.write(...)> is invoked at least once,
        // and the same <src> buffer instance is reused across all invocations
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
        service.write(channel, attachment, handler);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(handler, Mockito.timeout(TimeUnit.SECONDS.toMillis(8L)).times(1))
                .failed(exc, attachment);
        Mockito.verify(handler, Mockito.never())
                .completed(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    /**
     * Verifies that the method invokes
     * {@link CompletionHandler#failed(Throwable, Object) handler.failed(exc, attachment)} when the
     * {@code channel} fails — possibly synchronously on the first invocation, or asynchronously
     * after one or more partial writes have already been acknowledged.
     */
    @DisplayName("""
            should invoke <handler.failed(exc, attachment)>
            when the <channel> fails on or after partial writes"""
    )
    @Test
    @SuppressWarnings({"unchecked"})
    void __failed2() {
        // ----------------------------------------------------------------------------------- given
        HelloWorldTestUtils.put_buffer_will_increase_buffer_position_by_12(service());
        final AsynchronousHelloWorld service = AsynchronousHelloWorld.from(service());
        final var channel = Mockito.mock(AsynchronousByteChannel.class);
        final var exc = new RuntimeException("simulated write failure");
        Mockito.doAnswer(i -> {
            final var src = i.getArgument(0, ByteBuffer.class);
            final var a = i.getArgument(1);
            final var h = i.getArgument(2, CompletionHandler.class);
            Thread.ofPlatform().start(() -> {
                if (ThreadLocalRandom.current().nextBoolean()) {
                    h.failed(exc, a);
                    return;
                }
                final var n = ThreadLocalRandom.current().nextInt(src.remaining()) + 1;
                if (n == src.remaining()) {
                    h.failed(exc, a);
                    return;
                }
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
        service.write(channel, attachment, handler);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(handler, Mockito.timeout(TimeUnit.SECONDS.toMillis(8L)).times(1))
                .failed(exc, attachment);
        Mockito.verify(handler, Mockito.never())
                .completed(ArgumentMatchers.any(), ArgumentMatchers.any());
    }
}
