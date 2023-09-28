package com.github.jinahya.hello.misc.c02rfc862;

import com.github.jinahya.hello.misc._Rfc86_Constants;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Objects;

class Rfc862Tcp4ServerAttachment extends _Rfc862Attachment.Server {

    Rfc862Tcp4ServerAttachment(final AsynchronousSocketChannel client) {
        super();
        this.client = Objects.requireNonNull(client, "client is null");
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public void close() throws IOException {
        client.close();
        super.close();
    }

    int read() throws Exception {
        final var r = client.read(buffer)
                .get(_Rfc86_Constants.READ_TIMEOUT, _Rfc86_Constants.READ_TIMEOUT_UNIT);
        if (r != -1) {
            increaseBytes(r);
        }
        return r;
    }

    int write() throws Exception {
        buffer.flip();
        final var w = client.write(buffer)
                .get(_Rfc86_Constants.WRITE_TIMEOUT, _Rfc86_Constants.WRITE_TIMEOUT_UNIT);
        updateDigest(w);
        buffer.compact();
        return w;
    }

    private final AsynchronousSocketChannel client;
}
