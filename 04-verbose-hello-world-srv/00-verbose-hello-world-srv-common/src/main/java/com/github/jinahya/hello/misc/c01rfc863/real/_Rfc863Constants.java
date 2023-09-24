package com.github.jinahya.hello.misc.c01rfc863.real;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

final class _Rfc863Constants {

    private static final int RFC863_PORT = 9;

    private static final int PORT = RFC863_PORT + 50000;

    public static final InetSocketAddress ADDR = new InetSocketAddress(
            InetAddress.getLoopbackAddress(),
            PORT
    );

    static final int SERVER_BUFLEN = 1024;

    static final int CLIENT_BUFLEN = 1024;

    static final int CLIENT_BYTES = 65536;

    static final int CLIENT_COUNT = 131072;

    static final int SERVER_THREADS = 256;

    static final long READ_TIMEOUT = 2L;

    static final TimeUnit READ_TIMEOUT_UNIT = TimeUnit.SECONDS;

    static final long READ_TIMEOUT_MILLIS = READ_TIMEOUT_UNIT.toMillis(READ_TIMEOUT);

    static final int CLIENT_THREADS = 64;

    static final long SELECT_TIMEOUT = TimeUnit.SECONDS.toMillis(4L);

    private _Rfc863Constants() {
        throw new AssertionError("instantiation is not allowed");
    }
}
