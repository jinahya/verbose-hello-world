package com.github.jinahya.hello.miscellaneous;

/**
 * .
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc768">RFC 768: User Datagram Protocol</a>
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc768>RFC 768: User Datagram Protocol</a>
 */
public final class Rfc768Constants {

    public static final int HEADER_SOURCE_PORT_SIZE = Short.SIZE;

    public static final int HEADER_DESTINATION_PORT_SIZE = Short.SIZE;

    public static final int HEADER_LENGTH_SIZE = Short.SIZE;

    public static final int HEADER_CHECKSUM_SIZE = Short.SIZE;

    public static final int HEADER_SIZE =
            HEADER_SOURCE_PORT_SIZE
            + HEADER_DESTINATION_PORT_SIZE
            + HEADER_LENGTH_SIZE
            + HEADER_CHECKSUM_SIZE;

    public static final int HEADER_BYTES = HEADER_SIZE >> 3;

    public static final int PROTOCOL_NUMBER = 0x17;

    private Rfc768Constants() {
        throw new AssertionError("instantiation is not allowed");
    }
}
