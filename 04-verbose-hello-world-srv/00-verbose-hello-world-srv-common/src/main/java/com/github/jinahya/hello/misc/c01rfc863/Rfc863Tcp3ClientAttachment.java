package com.github.jinahya.hello.misc.c01rfc863;

import java.nio.channels.AsynchronousByteChannel;
import java.util.Objects;
import java.util.concurrent.Future;

final class Rfc863Tcp3ClientAttachment extends _Rfc863Attachment.Client {

    /**
     * Creates a new instance.
     */
    Rfc863Tcp3ClientAttachment() {
        super();
    }

    /**
     * Writes a sequence of bytes from {@link #buffer} to specified channel.
     *
     * @param channel the channel to which bytes are written.
     * @return a future representing a number of bytes written.
     * @see Rfc863Tcp3ServerAttachment#readFrom(AsynchronousByteChannel)
     */
    Future<Integer> writeTo(final AsynchronousByteChannel channel) {
        Objects.requireNonNull(channel, "channel is null");
        return channel.write(getBufferForWriting());
    }
}
