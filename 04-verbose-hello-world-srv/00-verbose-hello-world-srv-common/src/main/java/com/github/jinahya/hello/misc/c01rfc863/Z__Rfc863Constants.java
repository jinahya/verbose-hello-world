package com.github.jinahya.hello.misc.c01rfc863;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;

final class Z__Rfc863Constants {

    private static final int RFC863_PORT = 9;

    private static final int PORT = RFC863_PORT + 50000;

    public static final InetSocketAddress ADDR = new InetSocketAddress(
            InetAddress.getLoopbackAddress(),
            PORT
    );

    // ---------------------------------------------------------------------------------------------
    static final int SERVER_THREADS = 128;

    static final int SERVER_BACKLOG = SERVER_THREADS << 1;

    static final int SERVER_BUFLEN = 1024;

    static final int CLIENT_BUFLEN = 1024;

    static final int CLIENT_BYTES = 65536;

    static final int CLIENT_COUNT = 131072;

    // -------------------------------------------------------------------------------- READ_TIMEOUT
    static final long READ_TIMEOUT = 16L;

    static final TimeUnit READ_TIMEOUT_UNIT = TimeUnit.SECONDS;

    static final long READ_TIMEOUT_MILLIS = READ_TIMEOUT_UNIT.toMillis(READ_TIMEOUT);

    // ---------------------------------------------------------------------------------------------
    static final int CLIENT_THREADS = 64;

    static final long SELECT_TIMEOUT = TimeUnit.SECONDS.toMillis(4L);

    /**
     * An unmodifiable list of client classes.
     */
    static final List<Class<?>> CLIENT_CLASSES = List.of(
            Rfc863Tcp0Client.class,
            Rfc863Tcp1Client.class,
            Rfc863Tcp2Client.class,
            Rfc863Tcp3Client.class,
            Rfc863Tcp4Client.class,
            Rfc863Tcp5Client.class
    );

    private Z__Rfc863Constants() {
        throw new AssertionError("instantiation is not allowed");
    }
}
