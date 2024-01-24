package com.github.jinahya.hello.misc.c03calc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.ThreadLocalRandom;

class _CalcMessage2Test {

    @Nested
    class CalculateTest {

        @Test
        void __() {
            // ------------------------------------------------------------------------------- given
            final var message = __CalcMessage2.newRandomInstance();
            // -------------------------------------------------------------------------------- when
            final var result = message.calculate();
            // -------------------------------------------------------------------------------- then
            Assertions.assertSame(message, result);
            Assertions.assertEquals(
                    message.operator().applyAsInt(message.operand1(), message.operand2()),
                    message.result()
            );
        }
    }

    @Nested
    class StreamTest {

        @Test
        void __() throws IOException {
            // ------------------------------------------------------------------------------- given
            final var message = __CalcMessage2.newRandomInstance();
            // -------------------------------------------------------------------------------- when
            final ByteArrayOutputStream output = new ByteArrayOutputStream(__CalcMessage2.BYTES);
            message.write(output);
            final var copy = __CalcMessage2.copyOf(message);
            final ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
            message.read(input);
            // -------------------------------------------------------------------------------- then
            Assertions.assertEquals(copy, message);
        }
    }

    @Nested
    class ChannelTest {

        @Test
        void __() throws IOException {
            // ------------------------------------------------------------------------------- given
            final var message = __CalcMessage2.newRandomInstance();
            // -------------------------------------------------------------------------------- when
            final ByteArrayOutputStream output = new ByteArrayOutputStream(__CalcMessage2.BYTES);
            final WritableByteChannel writable;
            {
                writable = Mockito.spy(Channels.newChannel(output));
                BDDMockito.willAnswer(i -> {
                            final var buffer = i.getArgument(0, ByteBuffer.class);
                            final var w =
                                    ThreadLocalRandom.current().nextInt(buffer.remaining()) + 1;
                            buffer.position(buffer.position() + w);
                            return w;
                        })
                        .given(writable)
                        .write(ArgumentMatchers.argThat(b -> b != null && b.hasRemaining()));
            }
            message.write(writable);
            final var copy = __CalcMessage2.copyOf(message);
            final ReadableByteChannel readable;
            {
                final ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
                readable = Mockito.spy(Channels.newChannel(input));
                BDDMockito.willAnswer(i -> {
                            final var buffer = i.getArgument(0, ByteBuffer.class);
                            final var r =
                                    ThreadLocalRandom.current().nextInt(buffer.remaining()) + 1;
                            buffer.position(buffer.position() + r);
                            return r;
                        })
                        .given(readable)
                        .read(ArgumentMatchers.argThat(b -> b != null && b.hasRemaining()));
            }
            message.read(readable);
            // -------------------------------------------------------------------------------- then
            Assertions.assertEquals(copy, message);
        }
    }
}
