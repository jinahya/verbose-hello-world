package com.github.jinahya.hello.util.javax.crypto;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2023 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.github.jinahya.hello.util._ExcludeFromCoverage_PrivateConstructor_Obviously;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Objects;

@Slf4j
public final class CipherUtils {

    public static long update(final Cipher cipher, final InputStream inputStream,
                              final byte[] inputBuffer, final OutputStream outputStream,
                              byte[] outputBuffer)
            throws IOException {
        Objects.requireNonNull(cipher, "cipher is null");
        Objects.requireNonNull(inputStream, "inputStream is null");
        if (Objects.requireNonNull(inputBuffer, "inputBuffer is null").length == 0) {
            throw new IllegalArgumentException(
                    "zero-length inputBuffer: " + Objects.toString(inputBuffer));
        }
        Objects.requireNonNull(outputStream, "outputStream is null");
        if (Objects.requireNonNull(outputBuffer, "outputBuffer is null").length == 0) {
            throw new IllegalArgumentException(
                    "zero-length outputBuffer: " + Objects.toString(outputBuffer));
        }
        var count = 0L;
        for (int r, l; (r = inputStream.read(inputBuffer)) != -1; count += r) {
            while (true) {
                try {
                    l = cipher.update(inputBuffer, 0, r, outputBuffer);
                    break;
                } catch (final ShortBufferException sbe) {
                    outputBuffer = new byte[outputBuffer.length << 1];
                }
            }
            outputStream.write(outputBuffer, 0, l);
        }
        return count;
    }

    public static long update(final Cipher cipher, final InputStream inputStream,
                              final byte[] inputBuffer, final OutputStream outputStream)
            throws IOException {
        Objects.requireNonNull(cipher, "cipher is null");
        if (Objects.requireNonNull(inputBuffer, "inputBuffer is null").length == 0) {
            throw new IllegalArgumentException(
                    "zero-length inputBuffer: " + Objects.toString(inputBuffer));
        }
        return update(
                cipher,
                inputStream,
                inputBuffer,
                outputStream,
                new byte[cipher.getOutputSize(inputBuffer.length)]
        );
    }

    public static long update(final Cipher cipher, final ReadableByteChannel readableChannel,
                              final ByteBuffer readBuffer,
                              final WritableByteChannel writableChannel,
                              ByteBuffer writeBuffer)
            throws IOException {
        Objects.requireNonNull(cipher, "cipher is null");
        Objects.requireNonNull(readableChannel, "readableChannel is null");
        if (Objects.requireNonNull(readBuffer, "readBuffer is null").capacity() == 0) {
            throw new IllegalArgumentException("zero-capacity readBuffer: " + readBuffer);
        }
        Objects.requireNonNull(writableChannel, "writableChannel is null");
        if (Objects.requireNonNull(writeBuffer, "writeBuffer is null").capacity() == 0) {
            throw new IllegalArgumentException("zero-capacity writeBuffer: " + writeBuffer);
        }
        var count = 0L;
        for (; readableChannel.read(readBuffer.clear()) != -1; count += readBuffer.position()) {
            readBuffer.flip();
            while (true) {
                try {
                    cipher.update(readBuffer, writeBuffer.clear());
                    break;
                } catch (final ShortBufferException sbe) {
                    writeBuffer = ByteBuffer.allocate(writeBuffer.capacity() << 1);
                }
            }
            for (writeBuffer.flip(); writeBuffer.hasRemaining(); ) {
                writableChannel.write(writeBuffer);
            }
        }
        return count;
    }

    public static long update(final Cipher cipher, final ReadableByteChannel readableChannel,
                              final ByteBuffer readBuffer,
                              final WritableByteChannel writableChannel)
            throws IOException {
        Objects.requireNonNull(cipher, "cipher is null");
        if (Objects.requireNonNull(readBuffer, "readBuffer is null").capacity() == 0) {
            throw new IllegalArgumentException("zero-capacity readBuffer: " + readBuffer);
        }
        return update(
                cipher,
                readableChannel,
                readBuffer,
                writableChannel,
                ByteBuffer.allocate(cipher.getOutputSize(readBuffer.capacity()))
        );
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private CipherUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
