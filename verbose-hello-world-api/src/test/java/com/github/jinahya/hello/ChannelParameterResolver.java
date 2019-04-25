package com.github.jinahya.hello;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.invoke.MethodHandles;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

import static java.util.concurrent.ThreadLocalRandom.current;

/**
 * A parameter resolver for writable byte channels.
 */
class ChannelParameterResolver implements ParameterResolver {

    // -----------------------------------------------------------------------------------------------------------------
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A marker annotation for emulating non-blocking channel.
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER})
    @interface NonBlocking {

    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A writable byte channel for emulating non-blocking mode.
     */
    static class CountableByteChannel implements WritableByteChannel {

        /**
         * Creates a new instance.
         *
         * @param blocking a flag for emulating non-blocking mode; {@code true} for blocking, {@code false} for
         *                 non-blocking.
         */
        CountableByteChannel(final boolean blocking) {
            super();
            this.blocking = blocking;
        }

        // -------------------------------------------------------------------------------------------------------------

        /**
         * Writes a sequence of bytes to this channel from the given buffer.
         *
         * @param src the buffer from which bytes are read
         * @return the number of count bytes; possibly zero if this channel is non-blocking emulated.
         * @throws IOException if an I/O error occurs.
         */
        @Override
        public int write(final ByteBuffer src) throws IOException {
            if (src.remaining() == 0) {
                return 0;
            }
            final int written = blocking ? src.remaining() : current().nextInt(src.remaining() + 1);
            logger.debug("writing {} byte(s)", written);
            src.position(src.position() + written);
            count += written;
            return written;
        }

        /**
         * Checks whether this channel is open or not. The {@code isOpen} method of {@code CountableByteChannel} class
         * always returns {@code true}.
         *
         * @return {@code true}
         */
        @Override
        public boolean isOpen() {
            return true;
        }

        /**
         * Closes this channel. The {@code close} method of {@code CountableByteChannel} class does nothing.
         *
         * @throws IOException if an I/O error occurs.
         */
        @Override
        public void close() throws IOException {
            // does nothing
        }

        // -------------------------------------------------------------------------------------------------------------

        /**
         * Returns a flag for blocking mode.
         *
         * @return {@code true} if this channel is in blocking mode; {@code false} otherwise.
         */
        boolean isBlocking() {
            return blocking;
        }

        /**
         * Returns the number of bytes written to this channel so far.
         *
         * @return the number of bytes written to this channel so far.
         */
        long getCount() {
            return count;
        }

        // -------------------------------------------------------------------------------------------------------------
        private final boolean blocking;

        private long count;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public boolean supportsParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return WritableByteChannel.class.isAssignableFrom(parameterContext.getParameter().getType());
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public Object resolveParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return new CountableByteChannel(!parameterContext.isAnnotated(NonBlocking.class));
    }
}
