package com.github.jinahya.hello;

import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;
import java.util.Collection;

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
        // TODO: implement!
        return null;
    }

    /**
     * Writes {@value SIZE} bytes of {@code hello, world} string represented in {@code US-ASCII} character set to
     * specified data output.
     *
     * @param data the data output to which bytes are written.
     * @param <T>  data output type parameter.
     * @return the specified data output.
     * @throws NullPointerException if {@code data} is {@code null}
     * @throws IOException          if an I/O error occurs
     * @see #set(byte[])
     * @see DataOutput#write(byte[])
     */
    default <T extends DataOutput> T write(final T data) throws IOException {
        // TODO: implement!
        return null;
    }

    /**
     * Writes {@value SIZE} bytes to specified random access file.
     *
     * @param file the random access file to which bytes are written.
     * @param <T>  random access file type parameter.
     * @return the specified random access file.
     * @throws NullPointerException if {@code file} argument is {@code null}
     * @throws IOException          if an I/O error occurs.
     */
    default <T extends RandomAccessFile> T write(final T file) throws IOException {
        if (file == null) {
            throw new NullPointerException("file is null");
        }
        // TODO: implement!
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
        if (stream == null) {
            throw new NullPointerException("stream is null");
        }
        // TODO: implement!
        return null;
    }

    /**
     * Writes {@value SIZE} bytes of {@code hello, world} string on specified file.
     *
     * @param file the file to which bytes are written
     * @param <T>  file type parameter
     * @return the specified file
     * @throws IOException if an I/O error occurs.
     */
    default <T extends File> T write(final T file) throws IOException {
        if (file == null) {
            throw new NullPointerException("file is null");
        }
        // TODO: implement!
        return null;
    }

    /**
     * Writes {@value SIZE} bytes of {@code hello, world} to the output stream of the specified socket.
     *
     * @param socket the socket to which bytes are sent
     * @param <T>    socket type parameter.
     * @return the specified socket.
     * @throws NullPointerException if {@code socket} is {@code null}
     * @throws IOException          if an I/O error occurs.
     * @see Socket#getOutputStream()
     * @see #write(OutputStream)
     */
    default <T extends Socket> T send(final T socket) throws IOException {
        if (socket == null) {
            throw new NullPointerException("socket is null");
        }
        // TODO: implement!
        return null;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Puts {@value SIZE} bytes of {@code hello, world} on specified byte buffer. The {@code position} of the byte
     * buffer, on successful return, is increased by {@value SIZE}.
     *
     * @param buffer the byte buffer to which bytes are put
     * @param <T>    byte buffer type parameter
     * @return the specified byte buffer
     * @throws NullPointerException             if {@code buffer} is {@code null}
     * @throws java.nio.BufferOverflowException if {@code buffer.remaining()} is less than {@link HelloWorld#SIZE}
     */
    default <T extends ByteBuffer> T put(final T buffer) {
        if (buffer == null) {
            throw new NullPointerException("buffer is null");
        }
        // TODO: implement!
        return null;
    }

    /**
     * Writes {@value SIZE} bytes, representing {@code hello, world} string in {@code US-ASCII} character set, to
     * specified channel.
     *
     * @param channel the channel to which bytes are written.
     * @param <T>     channel type parameter
     * @return the specified channel
     * @throws NullPointerException if {@code channel} is {@code null}
     * @throws IOException          if an I/O error occurs
     * @see #put(ByteBuffer)
     * @see WritableByteChannel#write(ByteBuffer)
     */
    default <T extends WritableByteChannel> T write(final T channel) throws IOException {
        if (channel == null) {
            throw new NullPointerException("channel is null");
        }
        // TODO: implement!
        return null;
    }

    /**
     * Writes {@value SIZE} bytes of {@code hello, world} string to specified path.
     *
     * @param path the path to which bytes are written.
     * @param <T>  path type parameter
     * @return the specified path
     * @throws NullPointerException if {@code path} is {@code null}
     * @throws IOException          if an I/O error occurs.
     */
    default <T extends Path> T write(final T path) throws IOException {
        if (path == null) {
            throw new NullPointerException("path is null");
        }
        // TODO: implement!
        return null;
    }

    /**
     * Sends {@value SIZE} bytes of {@code hello, world} string to specified socket channel.
     *
     * @param channel the socket channel to which bytes are written.
     * @param <T>     socket channel type parameter
     * @return the specified socket channel
     * @throws NullPointerException if {@code channel} is {@code null}
     * @throws IOException          if an I/O error occurs.
     */
    default <T extends SocketChannel> T send(final T channel) throws IOException {
        if (channel == null) {
            throw new NullPointerException("channel is null");
        }
        // TODO: implement!
        return null;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Adds {@value SIZE} bytes of {@code hello, world} string to specified collection.
     *
     * @param collection the collection to which bytes are added
     * @param <T> collection type parameter
     * @return the specified collection
     */
    default <T extends Collection<? super Byte>> T add(final T collection) {
        if (collection == null) {
            throw new NullPointerException("collection is null");
        }
        // TODO: implement!
        return null;
    }
}
