package com.github.jinahya.hello;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;

/**
 * An interface for generating bytes of {@code hello, world} string.
 *
 * @author Jin Kwon &lt;jinahya_at_gmail.com&gt;
 */
public interface HelloWorld {

    /**
     * The number of bytes to represent the {@code hello, world} string in {@code US-ASCII} character set. The value is
     * {@value SIZE}.
     */
    int SIZE = 12;

    /**
     * Sets {@value SIZE} bytes of {@code hello, world} string on specified array starting at specified position.
     *
     * @param array the array to which {@code hello, world} bytes are set.
     * @param index the starting index of the array.
     * @return the specified array
     * @throws NullPointerException      if the {@code array} is {@code null}
     * @throws IndexOutOfBoundsException if {@code index} is negative or {@code index} + {@value SIZE} is greater than
     *                                   {@code array.length}
     */
    byte[] set(byte[] array, int index);

    /**
     * Sets {@value SIZE} bytes of {@code hello, world} string on specified array starting at {@code 0}.
     *
     * @param array the array to which bytes are set
     * @return the specified array
     * @throws NullPointerException      if {@code array} is {@code null}
     * @throws IndexOutOfBoundsException if {@code array.length} is less than {@value SIZE}
     * @see #set(byte[], int)
     */
    default byte[] set(final byte[] array) {
        // @todo: implement!
        return null;
    }

    /**
     * Writes {@value SIZE} bytes of {@code hello, world} string on specified output stream.
     *
     * @param stream the output stream to which bytes are written.
     * @param <T>    output stream type parameter.
     * @return given output stream.
     * @throws IOException          if an I/O error occurs.
     * @throws NullPointerException if {@code stream} is {@code null}
     * @see #set(byte[])
     * @see OutputStream#write(byte[])
     */
    default <T extends OutputStream> T write(final T stream) throws IOException {
        // @todo: implement!
        return null;
    }

    default <T extends File> T write(final T file) throws IOException {
        return null;
    }

    default <T extends Socket> T send(final T socket) throws IOException {
        // @todo: implement!
        return null;
    }

    default <T extends ByteBuffer> T put(final T buffer) {
        // @todo: implement!
        return null;
    }

    default <T extends WritableByteChannel> T write(final T channel) throws IOException {
        // @todo: implement!
        return null;
    }

    default <T extends Path> T write(final T path) throws IOException {
        // @todo: implement!
        return null;
    }

    default <T extends SocketChannel> T send(final T socket) throws IOException {
        // @todo: implement!
        return null;
    }
}
