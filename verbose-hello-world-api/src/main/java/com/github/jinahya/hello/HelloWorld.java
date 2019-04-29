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
     * @param array the array on which bytes are set
     * @param index the starting index of the array
     * @return the specified array
     * @throws NullPointerException      if the {@code array} is {@code null}
     * @throws IndexOutOfBoundsException if {@code index} is negative or {@code index} + {@value SIZE} is greater than
     *                                   {@code array.length}
     */
    byte[] set(byte[] array, int index);

    /**
     * Sets {@value SIZE} bytes of {@code hello, world} string on specified array starting at {@code 0}. This method
     * returns the result of {@link #set(byte[], int)} invoked with given array and {@code 0} for {@code index}
     * argument.
     *
     * @param array the array on which bytes are set
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
     * specified data output. This method gets the bytes from {@link #set(byte[])} and writes the array to specified
     * data output using {@link DataOutput#write(byte[])} method.
     *
     * @param data the data output to which bytes are written
     * @param <T>  data output type parameter
     * @return the specified data output
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
     * Writes {@value SIZE} bytes of {@code hello, world} string to specified random access file. This method gets the
     * bytes from {@link #set(byte[])} and writes the array to specified random access file using {@link
     * RandomAccessFile#write(byte[])}.
     *
     * @param file the random access file to which bytes are written
     * @param <T>  random access file type parameter
     * @return the specified random access file.
     * @throws NullPointerException if {@code file} argument is {@code null}
     * @throws IOException          if an I/O error occurs.
     * @see #set(byte[])
     * @see RandomAccessFile#write(byte[])
     */
    default <T extends RandomAccessFile> T write(final T file) throws IOException {
        if (file == null) {
            throw new NullPointerException("file is null");
        }
        // TODO: implement!
        return null;
    }

    /**
     * Writes {@value SIZE} bytes of {@code hello, world} string to specified output stream. This method gets the bytes
     * from {@link #set(byte[])} and writes the array to specified output stream using {@link
     * OutputStream#write(byte[])}.
     *
     * @param stream the output stream to which bytes are written
     * @param <T>    output stream type parameter
     * @return the specified output stream
     * @throws NullPointerException if {@code stream} is {@code null}
     * @throws IOException          if an I/O error occurs
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
     * Writes {@value SIZE} bytes of {@code hello, world} string to specified file. This method constructs an output
     * stream using {@link java.io.FileOutputStream#FileOutputStream(File)} with the specified file and invokes {@link
     * #write(OutputStream)} with the output stream.
     *
     * @param file the file to which bytes are written
     * @param <T>  file type parameter
     * @return the specified file
     * @throws IOException if an I/O error occurs.
     * @see java.io.FileOutputStream#FileOutputStream(File)
     * @see #write(OutputStream)
     */
    default <T extends File> T write(final T file) throws IOException {
        if (file == null) {
            throw new NullPointerException("file is null");
        }
        // TODO: implement!
        return null;
    }

    /**
     * Writes {@value SIZE} bytes of {@code hello, world} to the output stream of the specified socket. This method
     * invokes {@link #write(OutputStream)} with the value of {@link Socket#getOutputStream()} invoked on the specified
     * socket.
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
     * Puts {@value SIZE} bytes of {@code hello, world} string on specified byte buffer. The {@code position} of the
     * byte buffer, on successful return, is increased by {@value SIZE}. This method, if the specified buffer has an
     * array, invokes directly {@link #set(byte[], int)} with {@link ByteBuffer#array() buffer.array} and {@link
     * ByteBuffer#arrayOffset() buffer.arrayOffset}. Otherwise, gets the bytes from {@link #set(byte[])} and puts those
     * bytes using {@link ByteBuffer#put(byte[])}.
     *
     * @param buffer the byte buffer to which bytes are put
     * @param <T>    byte buffer type parameter
     * @return the specified byte buffer
     * @throws NullPointerException     if {@code buffer} is {@code null}
     * @throws IllegalArgumentException {@code buffer.remaining()} is less than {@link HelloWorld#SIZE}
     * @see ByteBuffer#hasArray()
     * @see ByteBuffer#array()
     * @see #set(byte[], int)
     * @see #set(byte[])
     * @see ByteBuffer#put(byte[])
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
     * <p>
     * This method gets bytes from {@link #put(ByteBuffer)} and write the buffer to specified channel.
     *
     * @param channel the channel to which bytes are written
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
}
