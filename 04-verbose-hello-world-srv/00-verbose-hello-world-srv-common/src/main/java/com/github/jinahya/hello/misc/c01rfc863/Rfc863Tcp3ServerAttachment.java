package com.github.jinahya.hello.misc.c01rfc863;

import java.nio.channels.AsynchronousByteChannel;
import java.util.Objects;
import java.util.concurrent.Future;

final class Rfc863Tcp3ServerAttachment extends _Rfc863Attachment.Server {

    /**
     * Creates a new instance.
     */
    Rfc863Tcp3ServerAttachment() {
        super();
    }

    /**
     * .
     *
     * @param channel
     * @return
     * @see Rfc863Tcp3ClientAttachment#writeTo(AsynchronousByteChannel)
     */
    Future<Integer> readFrom(final AsynchronousByteChannel channel) {
        Objects.requireNonNull(channel, "channel is null");
        return channel.read(getBufferForReading());
    }
}
