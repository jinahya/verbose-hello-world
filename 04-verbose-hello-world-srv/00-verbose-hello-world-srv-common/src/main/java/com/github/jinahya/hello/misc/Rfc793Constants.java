package com.github.jinahya.hello.misc;

/**
 * .
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc793">RFC 793: TRANSMISSION
 * CONTROL PROTOCOL</a>
 * @see <a href="https://www.rfc-editor.org/rfc/rfc9293">RFC 9293: Transmission
 * Control Protocol (TCP)</a>
 */
public final class Rfc793Constants {

    public static final int HEADER_SOURCE_PORT_BYTES = Short.BYTES;

    public static final int HEADER_DESTINATION_PORT_BYTES = Short.BYTES;

    public static final int HEADER_SEQUENCE_NUMBER_PORT_BYTES = Integer.BYTES;

    public static final int HEADER_ACKNOWLEDGMENT_NUMBER_PORT_BYTES = Integer.BYTES;

    public static final int HEADER_DATA_OFFSET_SIZE = Byte.SIZE >> 1;

    public static final int HEADER_RSRVD_SIZE = Byte.SIZE >> 1;

    public static final int HEADER_CWR_SIZE = Byte.SIZE >> 3;

    public static final int HEADER_ECE_SIZE = Byte.SIZE >> 3;

    public static final int HEADER_URG_SIZE = Byte.SIZE >> 3;

    public static final int HEADER_ACK_SIZE = Byte.SIZE >> 3;

    public static final int HEADER_PSH_SIZE = Byte.SIZE >> 3;

    public static final int HEADER_RST_SIZE = Byte.SIZE >> 3;

    public static final int HEADER_SYN_SIZE = Byte.SIZE >> 3;

    public static final int HEADER_FIN_SIZE = Byte.SIZE >> 3;

    public static final int HEADER_WINDOW_BYTES = Short.BYTES;

    public static final int HEADER_CHECKSUM_BYTES = Short.BYTES;

    public static final int HEADER_URGENT_POINTER_BYTES = Short.BYTES;

    private Rfc793Constants() {
        throw new AssertionError("instantiation is not allowed");
    }
}
