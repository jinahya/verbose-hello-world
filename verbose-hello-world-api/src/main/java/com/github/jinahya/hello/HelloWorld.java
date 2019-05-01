package com.github.jinahya.hello;

import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * An interface for generating <a href="#hello-world-bytes">hello-world-bytes</a> to various targets.
 *
 * <h2 id="hello-world-bytes">hello-world-bytes</h2>
 * A sequence of {@value SIZE} bytes, representing the "{@code hello, world}" string encoded in {@code US-ASCII}
 * character set, which consists of {@code 0x68('h1')} followed by {@code 0x65}, {@code 0x6C}, {@code 0x6C}, {@code
 * 0x6F}, {@code 0x2C}, {@code 0x20}, {@code 0x77}, {@code 0x6F}, {@code 0x72}, {@code 0x6C}, and {@code 0x64}.
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
     * Sets <a href="#hello-world-byte">hello-world-bytes</a> on specified array starting at specified position and
     * returns the specified array.
     * <p>
     * The elements in the specified array, on successful return, will be set as follows.
     * <pre>{@code
     *   0                                                               array.length
     *   |                                                               |
     * |   |...|'h'|'e'|'l'|'l'|'o'|','|' '|'w'|'o'|'r'|'l'|'d'|   |...|
     *           |                                               |
     *           index                                           index + SIZE
     * }</pre>
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
     * Sets {@value SIZE} <a href="#hello-world-bytes">hello-world-bytes</a> on specified array starting at {@code 0}
     * and returns the specified array.
     * <p>
     * This method invokes {@link #set(byte[], int)} method with given array and {@code 0} for the {@code index}
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
     * Writes {@value SIZE} <a href="#hello-world-bytes">hello-world-bytes</a> to specified data output and returns the
     * specified data output.
     * <p>
     * This method gets the bytes from {@link #set(byte[])} and writes the array to specified data output using {@link
     * DataOutput#write(byte[])} method.
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
     * Writes {@value SIZE} <a href="#hello-world-bytes">hello-world-bytes</a> to specified random access file and
     * returns the random access file.
     * <p>
     * This method gets the bytes from {@link #set(byte[])} and writes the array to specified random access file using
     * {@link RandomAccessFile#write(byte[])}.
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
     * Writes {@value SIZE} <a href="#hello-world-bytes">hello-world-bytes</a> to specified output stream.
     * <p>
     * This method invokes {@link #set(byte[])} with an array whose length is equals to {@value SIZE} and writes the
     * returned array to specified output stream using {@link OutputStream#write(byte[])}.
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
     * Writes {@value SIZE} <a href="#hello-world-bytes">hello-world-bytes</a> to specified file and returns the
     * specified file.
     * <p>
     * This method constructs an output stream using {@link java.io.FileOutputStream#FileOutputStream(File)} with
     * specified file and invokes {@link #write(OutputStream)} with the output stream.
     *
     * @param file the file to which bytes are written
     * @param <T>  file type parameter
     * @return the specified file
     * @throws NullPointerException if {@code file} is {@code null}
     * @throws IOException          if an I/O error occurs.
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
     * Writes {@value SIZE} <a href="#hello-world-bytes">hello-world-bytes</a> to specified socket.
     * <p>
     * This method invokes {@link #write(OutputStream)} with the value of {@link Socket#getOutputStream()} invoked on
     * the specified socket.
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
     * Puts {@value SIZE} <a href="#hello-world-bytes">hello-world-bytes</a> on specified byte buffer.
     * <p>
     * This method, if the buffer has a backing-array, invokes {@link #set(byte[], int)} with the value of {@code
     * buffer.array()} and {@code buffer.arrayOffset() + buffer.position()} and manually increments the value of {@code
     * buffer.position} by {@value SIZE}.
     * <p>
     * Otherwise, this method invokes {@link #set(byte[])} with an array of {@value SIZE} elements and put the array on
     * the buffer using {@link ByteBuffer#put(byte[])}.
     *
     * @param buffer the byte buffer on which bytes are put
     * @param <T>    byte buffer type parameter
     * @return the specified byte buffer
     * @throws NullPointerException     if {@code buffer} is {@code null}
     * @throws IllegalArgumentException {@code buffer.remaining()} is less than {@link HelloWorld#SIZE}
     * @see ByteBuffer#hasArray()
     * @see ByteBuffer#array()
     * @see ByteBuffer#arrayOffset()
     * @see ByteBuffer#position()
     * @see ByteBuffer#position(int)
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
     * Writes {@value SIZE} <a href="#hello-world-bytes">hello-world-bytes</a> to specified channel.
     * <p>
     * This method invokes {@link #put(ByteBuffer)} with a byte buffer and write the buffer to specified channel.
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
     * Writes {@value SIZE} <a href="#hello-world-bytes">hello-world-bytes</a> to specified path.
     * <p>
     * This method opens a file channel on specified path with {@link StandardOpenOption#CREATE}, {@link
     * StandardOpenOption#WRITE} and {@link StandardOpenOption#APPEND} and invokes {@link #write(WritableByteChannel)}
     * with it.
     *
     * @param path the path to which bytes are written.
     * @param <T>  path type parameter
     * @return the specified path
     * @throws NullPointerException if {@code path} is {@code null}
     * @throws IOException          if an I/O error occurs.
     * @see FileChannel#open(Path, OpenOption...)
     * @see #write(WritableByteChannel)
     */
    default <T extends Path> T write(final T path) throws IOException {
        if (path == null) {
            throw new NullPointerException("path is null");
        }
        // TODO: implement!
        return null;
    }
}
