package com.github.jinahya.hello.api;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2026 Jinahya, Inc.
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

import org.junit.jupiter.api.*;

import java.util.concurrent.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * A class exercising
 * {@link StructuredConcurrencyHelloWorld#applyAsync(java.util.function.Function)
 * applyAsync(mapper)} on each {@link HelloWorld} method, organized as one {@link Nested} class per
 * target method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("applyAsync(mapper)")
class StructuredConcurrencyHelloWorld_ApplyAsync_Mapper__Test
        extends
        AsynchronousHelloWorld__Test<HelloWorld, StructuredConcurrencyHelloWorld<HelloWorld>> {

    // ---------------------------------------------------------------------------------------------
    StructuredConcurrencyHelloWorld_ApplyAsync_Mapper__Test() {
        super(HelloWorld.class, s -> new StructuredConcurrencyHelloWorld<>(s,
                                                                           java.util.function.UnaryOperator.identity()));
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Stubs {@code applyAsync(mapper)} on the asynchronous service to invoke the mapper on the
     * synchronous service before each test.
     */
    @BeforeEach
    void __() {
        AsynchronousHelloWorldTestUtils.applyAsync_mapper_applies_(
                synchronousService(),
                asynchronousService()
        );
    }

    // ----------------------------------------------------------------------------------- java.lang
    @DisplayName("set(array, index)")
    @Nested
    class SetArrayIndex_Test {
        // empty
    }

    @DisplayName("set(array)")
    @Nested
    class SetArray_Test {

        @DisplayName("should apply <mapper> via the <executor> and return the <hello-world-bytes>")
        @Test
        void __() throws Exception {
            // ------------------------------------------------------------------------------- given
            HelloWorld__TestUtils.set_array_sets_hello_world_bytes(synchronousService());
            // -------------------------------------------------------------------------------- when
            final var stage = asynchronousService().applyAsync(
                    ss -> ss.set(new byte[HelloWorld.BYTES])
            );
            final var array = stage.toCompletableFuture().get(5L, TimeUnit.SECONDS);
            // -------------------------------------------------------------------------------- then
            assertArrayEquals(hello_world_byte_array(), array);
        }
    }

    @DisplayName("byte array")
    @Nested
    class ByteArray_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    @DisplayName("string")
    @Nested
    class String_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    @DisplayName("append(appendable)")
    @Nested
    class Appendable_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    // --------------------------------------------------------------------------- java.lang.foreign
    @DisplayName("set(segment)")
    @Nested
    class Segment_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    // ------------------------------------------------------------------------------------- java.io
    @DisplayName("write(stream)")
    @Nested
    class OutputStream_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    @DisplayName("append(stream)")
    @Nested
    class InputStream_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    @DisplayName("append(file)")
    @Nested
    class File_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    @DisplayName("write(output)")
    @Nested
    class DataOutput_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    @DisplayName("write(writer)")
    @Nested
    class Writer_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    @DisplayName("append(reader)")
    @Nested
    class Reader_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    @DisplayName("append(file, charset)")
    @Nested
    class File_Charset_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    @DisplayName("asInputStream(...)")
    @Nested
    class AsInputStream_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    // ------------------------------------------------------------------------------------ java.net
    @DisplayName("set(packet)")
    @Nested
    class DatagramPacket_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    @DisplayName("send(socket, address)")
    @Nested
    class DatagramSocket_SocketAddress_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    @DisplayName("send(socket)")
    @Nested
    class DatagramSocket_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    @DisplayName("send(socket)")
    @Nested
    class Socket_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    @DisplayName("write(connection)")
    @Nested
    class URLConnection_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    // ------------------------------------------------------------------------------- java.net.http
    @DisplayName("body(builder, string)")
    @Nested
    class HttpRequestBuilder_String_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    // -------------------------------------------------------------------------------- java.net.ssl

    // ------------------------------------------------------------------------------------ java.nio
    @DisplayName("put(buffer)")
    @Nested
    class ByteBuffer_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    @DisplayName("buffer(supplier)")
    @Nested
    class Supplier_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    @DisplayName("buffer()")
    @Nested
    class ByteBuffer__Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    @DisplayName("put(buffer)")
    @Nested
    class Put_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    // --------------------------------------------------------------------------- java.nio.channels
    @DisplayName("write(channel)")
    @Nested
    class WritableByteChannel_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    @DisplayName("send(channel, address)")
    @Nested
    class DatagramChannel_SocketAddress_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    @DisplayName("write(channel)")
    @Nested
    class DatagramChannel_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    @DisplayName("write(channel)")
    @Nested
    class AsynchronousByteChannel_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    @DisplayName("write(channel, position)")
    @Nested
    class AsynchronousFileChannel_Long_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    // ------------------------------------------------------------------------------- java.nio.file
    @DisplayName("append(path)")
    @Nested
    class Path_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    // ------------------------------------------------------------------------------- java.security
    @DisplayName("update(digest)")
    @Nested
    class MessageDigest_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    @DisplayName("update(signature)")
    @Nested
    class Signature_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    // ------------------------------------------------------------------------------------ java.sql
    @DisplayName("setAsciiStream(statement, index)")
    @Nested
    class SetAsciiStream_PreparedStatement_Int_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    @DisplayName("setBinaryStream(statement, index)")
    @Nested
    class SetBinaryStream_PreparedStatement_Int_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    @DisplayName("setBytes(statement, index)")
    @Nested
    class SetBytes_PreparedStatement_Int_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    @DisplayName("setCharacterStream(statement, index)")
    @Nested
    class SetCharacterStream_PreparedStatement_Int_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    @DisplayName("setBinaryStream(blob, position)")
    @Nested
    class SetBinaryStream_Blob_Long_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    @DisplayName("setBytes(blob, position)")
    @Nested
    class SetBytes_Blob_Long_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    @DisplayName("setAsciiStream(clob, position)")
    @Nested
    class SetAsciiStream_Clob_Long_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    @DisplayName("setCharacterStream(clob, position)")
    @Nested
    class SetCharacterStream_Clob_Long_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    @DisplayName("setString(clob, position)")
    @Nested
    class SetString_Clob_Long_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    // ----------------------------------------------------------------------------------- java.util
    @DisplayName("set(bitset, index)")
    @Nested
    class BitSet_Int_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    @DisplayName("add(collection, function)")
    @Nested
    class SequencedCollection_Function_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    @DisplayName("add(collection, intfunction)")
    @Nested
    class SequencedCollection_IntFunction_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    @DisplayName("put(map, keyfunction, valuefunction)")
    @Nested
    class SequencedMap_IntFunction_Function_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    // -------------------------------------------------------------------------- java.util.function
    @DisplayName("accept(consumer)")
    @Nested
    class Consumer_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    @DisplayName("accept(consumer, function)")
    @Nested
    class Consumer_Function_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    @DisplayName("accept(intconsumer)")
    @Nested
    class IntConsumer_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    // ------------------------------------------------------------------------------- java.util.jar

    // ------------------------------------------------------------------------------- java.util.zip
    @DisplayName("update(checksum)")
    @Nested
    class Checksum_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    @DisplayName("set(deflater)")
    @Nested
    class Deflater_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }

    // ---------------------------------------------------------------------------- java.util.stream

    // -------------------------------------------------------------------------------- javax.crypto
    @DisplayName("update(mac)")
    @Nested
    class Mac_Test {

        @DisplayName("should apply <mapper> via the <executor>")
        @Test
        void __() {
        }
    }
}
