package com.github.jinahya.hello.miscellaneous;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;

class _MessageTest {

    private static InetAddress randomAddrIpv4() throws UnknownHostException {
        var addr = new byte[4];
        ThreadLocalRandom.current().nextBytes(addr);
        return InetAddress.getByAddress(addr);
    }

    private static InetAddress randomAddrIpv6() throws UnknownHostException {
        var addr = new byte[16];
        ThreadLocalRandom.current().nextBytes(addr);
        return InetAddress.getByAddress(addr);
    }

    private static InetAddress randomAddr() throws UnknownHostException {
        return ThreadLocalRandom.current().nextBoolean() ? randomAddrIpv4() : randomAddrIpv6();
    }

    private static int randomPort() {
        return ThreadLocalRandom.current().nextInt(65536);
    }

    private static InetSocketAddress randomAddress() throws UnknownHostException {
        return new InetSocketAddress(randomAddr(), randomPort());
    }

    private static byte[] randomContent() {
        var content = new byte[ThreadLocalRandom.current()
                .nextInt(ChatTcpConstants.MESSAGE_CONTENT_LENGTH)];
        ThreadLocalRandom.current().nextBytes(content);
        return content;
    }

    private static Message randomizedMessage() throws UnknownHostException {
        var message = new Message();
        message.setAddress(randomAddress());
        message.setContent(randomContent());
        return message;
    }

    @Test
    void __IPv4() throws UnknownHostException {
        var addr = randomAddrIpv4();
        var port = randomPort();
        var address = new InetSocketAddress(addr, port);
        var message = new Message();
        message.setAddress(address);
        assertArrayEquals(address.getAddress().getAddress(), message.getAddr());
        assertEquals(port, message.getPort());
        assertEquals(address, message.getAddress());
        var content = randomContent();
        message.setContent(content);
        assertArrayEquals(content, message.getContent());
    }

    @Test
    void __IPv6() throws UnknownHostException {
        var addr = randomAddrIpv6();
        var port = randomPort();
        var address = new InetSocketAddress(addr, port);
        var message = new Message();
        message.setAddress(address);
        assertEquals(address, message.getAddress());
        var content = randomContent();
        message.setContent(content);
        assertArrayEquals(content, message.getContent());
    }

    @Test
    void __WriteStream() throws IOException {
        var expected = randomizedMessage();
        var baos = new ByteArrayOutputStream();
        expected.write(baos);
        var binary = baos.toByteArray();
        assertEquals(ChatTcpConstants.MESSAGE_LENGTH, binary.length);
    }

    @Test
    void __ReadStream() throws IOException {
        var bais = new ByteArrayInputStream(new byte[ChatTcpConstants.MESSAGE_LENGTH]);
        var message = new Message().read(bais);
    }

    @Test
    void __WriteReadStream() throws IOException {
        var expected = randomizedMessage();
        var baos = new ByteArrayOutputStream();
        expected.write(baos);
        var binary = baos.toByteArray();
        var bais = new ByteArrayInputStream(binary);
        var actual = new Message().read(bais);
        assertEquals(expected, actual);
    }

    @Test
    void __WriteChannel() throws IOException {
        var expected = randomizedMessage();
        var output = new ByteArrayOutputStream();
        var writable = Channels.newChannel(output);
        var writableSpy = spy(writable);
        doAnswer(i -> {
            var src = i.getArgument(0, ByteBuffer.class);
            var limit = src.limit();
            var toBeWritten = ThreadLocalRandom.current().nextInt(src.remaining() + 1);
            src.limit(src.position() + toBeWritten);
            var written = writable.write(src);
            assert written == toBeWritten;
            src.limit(limit);
            return written;
        }).when(writableSpy).write(notNull());
        var actual = expected.write(writableSpy);
        assertEquals(expected, actual);
    }

    @Test
    void __ReadChannel() throws IOException {
        var input = new ByteArrayInputStream(new byte[ChatTcpConstants.MESSAGE_LENGTH]);
        var readable = Channels.newChannel(input);
        var readableSpy = spy(readable);
        doAnswer(i -> {
            var dst = i.getArgument(0, ByteBuffer.class);
            var limit = dst.limit();
            var toBeRead = ThreadLocalRandom.current().nextInt(dst.remaining() + 1);
            dst.limit(dst.position() + toBeRead);
            var read = readable.read(dst);
            assert read == toBeRead;
            dst.limit(limit);
            return read;
        }).when(readableSpy).read(notNull());
        var expected = new Message();
        var actual = expected.read(readable);
        assertEquals(expected, actual);
    }

    @Test
    void __ReadWriteChannel() throws IOException {
        var expected = randomizedMessage();
        var output = new ByteArrayOutputStream();
        WritableByteChannel writable;
        {
            var channel = Channels.newChannel(output);
            writable = spy(channel);
            doAnswer(i -> {
                var src = i.getArgument(0, ByteBuffer.class);
                var limit = src.limit();
                var toBeWritten = ThreadLocalRandom.current().nextInt(src.remaining() + 1);
                src.limit(src.position() + toBeWritten);
                var written = channel.write(src);
                assert written == toBeWritten;
                src.limit(limit);
                return written;
            }).when(writable).write(notNull());
        }
        // ------------------------------------------------------------------------------------ WHEN
        expected.write(writable);
        // ------------------------------------------------------------------------------------ THEN
        var binary = output.toByteArray();
        ReadableByteChannel readable;
        {
            var input = new ByteArrayInputStream(binary);
            var channel = Channels.newChannel(input);
            readable = spy(channel);
            doAnswer(i -> {
                var dst = i.getArgument(0, ByteBuffer.class);
                var limit = dst.limit();
                var toBeRead = ThreadLocalRandom.current().nextInt(dst.remaining() + 1);
                dst.limit(dst.position() + toBeRead);
                var read = channel.read(dst);
                assert read == toBeRead;
                dst.limit(limit);
                return read;
            }).when(readable).read(notNull());
        }
        var actual = new Message().read(readable);
        assertEquals(expected, actual);
    }
}
