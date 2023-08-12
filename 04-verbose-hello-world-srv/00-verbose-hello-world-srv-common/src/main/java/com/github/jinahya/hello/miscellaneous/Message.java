package com.github.jinahya.hello.miscellaneous;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class Message {

    /**
     * Creates a new instance.
     */
    public Message() {
        super();
        array = new byte[ChatTcpConstants.MESSAGE_LENGTH];
        buffer = ByteBuffer.wrap(array);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Message message)) return false;
        return Arrays.equals(array, message.array);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(array);
    }

    private int getAddrLength() {
        if (ThreadLocalRandom.current().nextBoolean()) {
            return buffer.get(ChatTcpConstants.MESSAGE_ADDR_LENGTH_OFFSET) & 0xFF;
        }
        return array[ChatTcpConstants.MESSAGE_ADDR_LENGTH_OFFSET] & 0xFF;
    }

    private void setAddrLength(int addrLength) {
        if (ThreadLocalRandom.current().nextBoolean()) {
            buffer.put(ChatTcpConstants.MESSAGE_ADDR_LENGTH_OFFSET, (byte) addrLength);
            return;
        }
        array[ChatTcpConstants.MESSAGE_ADDR_LENGTH_OFFSET] = (byte) addrLength;
    }

    byte[] getAddr() {
        if (ThreadLocalRandom.current().nextBoolean()) {
            var addr = new byte[getAddrLength()];
            buffer.get(ChatTcpConstants.MESSAGE_ADDR_OFFSET, addr);
            return addr;
        }
        return Arrays.copyOfRange(
                array,
                ChatTcpConstants.MESSAGE_ADDR_OFFSET,
                ChatTcpConstants.MESSAGE_ADDR_OFFSET + getAddrLength()
        );
    }

    private void setAddr(byte[] addr) {
        Objects.requireNonNull(addr, "addr is null");
        setAddrLength(addr.length);
        if (ThreadLocalRandom.current().nextBoolean()) {
            buffer.put(ChatTcpConstants.MESSAGE_ADDR_OFFSET, addr);
            return;
        }
        System.arraycopy(
                addr,
                0,
                array,
                ChatTcpConstants.MESSAGE_ADDR_OFFSET, addr.length
        );
    }

    int getPort() {
        if (ThreadLocalRandom.current().nextBoolean()) {
            assert buffer.order() == ByteOrder.BIG_ENDIAN;
            return buffer.getShort(ChatTcpConstants.MESSAGE_PORT_OFFSET) & 0xFFFF;
        }
        return ((array[ChatTcpConstants.MESSAGE_PORT_OFFSET] & 0xFF) << Byte.SIZE)
               | (array[ChatTcpConstants.MESSAGE_PORT_OFFSET + 1] & 0xFF);
    }

    private void setPort(int port) {
        if (ThreadLocalRandom.current().nextBoolean()) {
            assert buffer.order() == ByteOrder.BIG_ENDIAN;
            buffer.putShort(ChatTcpConstants.MESSAGE_PORT_OFFSET, (short) port);
            return;
        }
        array[ChatTcpConstants.MESSAGE_PORT_OFFSET] = (byte) ((port >> Byte.SIZE) & 0xFF);
        array[ChatTcpConstants.MESSAGE_PORT_OFFSET + 1] = (byte) (port & 0xFF);
    }

    public InetSocketAddress getAddress() throws UnknownHostException {
        return new InetSocketAddress(
                InetAddress.getByAddress(getAddr()),
                getPort()
        );
    }

    public void setAddress(InetSocketAddress address) {
        setAddr(address.getAddress().getAddress());
        setPort(address.getPort());
    }

    private int getContentLength() {
        if (ThreadLocalRandom.current().nextBoolean()) {
            return buffer.get(ChatTcpConstants.MESSAGE_CONTENT_LENGTH_OFFSET) & 0xFF;
        }
        return array[ChatTcpConstants.MESSAGE_CONTENT_LENGTH_OFFSET] & 0xFF;
    }

    private void setContentLength(int contentLength) {
        if (contentLength < 0) {
            throw new IllegalArgumentException("negative contentLength: " + contentLength);
        }
        if (contentLength > ChatTcpConstants.MESSAGE_CONTENT_LENGTH) {
            throw new IllegalArgumentException(
                    "contentLength(" + contentLength + ") > "
                    + ChatTcpConstants.MESSAGE_CONTENT_LENGTH);
        }
        if (ThreadLocalRandom.current().nextBoolean()) {
            buffer.put(ChatTcpConstants.MESSAGE_CONTENT_LENGTH_OFFSET, (byte) contentLength);
            return;
        }
        array[ChatTcpConstants.MESSAGE_CONTENT_LENGTH_OFFSET] = (byte) contentLength;
    }

    private byte[] getContent() {
        if (ThreadLocalRandom.current().nextBoolean()) {
            var content = new byte[getContentLength()];
            buffer.get(ChatTcpConstants.MESSAGE_CONTENT_OFFSET, content);
            return content;
        }
        return Arrays.copyOfRange(
                array,
                ChatTcpConstants.MESSAGE_CONTENT_OFFSET,
                ChatTcpConstants.MESSAGE_CONTENT_OFFSET + getContentLength()
        );
    }

    private void setContent(byte[] content) {
        Objects.requireNonNull(content, "content is null");
        setContentLength(content.length);
        Arrays.fill(
                array,
                ChatTcpConstants.MESSAGE_CONTENT_OFFSET + content.length,
                array.length,
                (byte) 0
        );
        if (ThreadLocalRandom.current().nextBoolean()) {
            buffer.put(ChatTcpConstants.MESSAGE_CONTENT_OFFSET, content);
            return;
        }
        System.arraycopy(
                content,
                0,
                array,
                ChatTcpConstants.MESSAGE_CONTENT_OFFSET,
                content.length
        );
    }

    public String getCotentAsString() {
        return new String(getContent(), StandardCharsets.UTF_8);
    }

    public void setContentAsString(String contentAsString) {
        Objects.requireNonNull(contentAsString, "contentAsString is null");
        setContent(contentAsString.getBytes(StandardCharsets.UTF_8));
    }

    public void read(InputStream stream) throws IOException {
        Objects.requireNonNull(stream, "stream is null");
        for (int off = 0, len = array.length; len > 0; ) {
            int r = stream.read(array, off, len);
            if (r == -1) {
                throw new EOFException("unexpected eof");
            }
            assert r > 0;
            off += r;
            len -= r;
        }
    }

    public void write(OutputStream stream) throws IOException {
        Objects.requireNonNull(stream, "stream is null");
        stream.write(array);
    }

    public void read(ReadableByteChannel channel) throws IOException {
        for (buffer.clear(); buffer.hasRemaining(); ) {
            int r = channel.read(buffer);
            if (r == -1) {
                throw new EOFException("unexpected eof");
            }
        }
    }

    public void write(WritableByteChannel channel) throws IOException {
        for (buffer.clear(); buffer.hasRemaining(); ) {
            channel.write(buffer);
        }
    }

    private final byte[] array;

    private final ByteBuffer buffer;
}
