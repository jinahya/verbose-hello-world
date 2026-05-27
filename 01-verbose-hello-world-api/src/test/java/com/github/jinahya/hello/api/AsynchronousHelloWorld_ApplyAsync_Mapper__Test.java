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

class AsynchronousHelloWorld_ApplyAsync_Mapper__Test
        extends AsynchronousHelloWorld__Test<HelloWorld> {

    // ---------------------------------------------------------------------------------------------
    AsynchronousHelloWorld_ApplyAsync_Mapper__Test() {
        super(HelloWorld.class);
    }

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void __() {
        AsynchronousHelloWorldTestUtils.applyAsync_mapper_applies_(
                synchronousService(),
                asynchronousService()
        );
    }

    // ----------------------------------------------------------------------------------- java.lang
    @Nested
    class SetArrayIndex_Test {
        // empty
    }

    @Nested
    class SetArray_Test {

        @Test
        void __() throws Exception {
            // ------------------------------------------------------------------------------- given
            HelloWorldTestUtils.set_array_sets_actual_hello_world_bytes(synchronousService());
            // -------------------------------------------------------------------------------- when
            final var stage = asynchronousService().applyAsync(
                    ss -> ss.set(new byte[HelloWorld.BYTES])
            );
            final var array = stage.toCompletableFuture().get(5L, TimeUnit.SECONDS);
            // -------------------------------------------------------------------------------- then
            Assertions.assertArrayEquals(
                    HelloWorldTestUtils.hello_world_byte_array(),
                    array
            );
        }
    }

    @Nested
    class ByteArray_Test {

        @Test
        void __() {
        }
    }

    @Nested
    class String_Test {

        @Test
        void __() {
        }
    }

    @Nested
    class Appendable_Test {

        @Test
        void __() {
        }
    }

    // --------------------------------------------------------------------------- java.lang.foreign
    @Nested
    class Segment_Test {

        @Test
        void __() {
        }
    }

    // ------------------------------------------------------------------------------------- java.io
    @Nested
    class OutputStream_Test {

        @Test
        void __() {
        }
    }

    @Nested
    class InputStream_Test {

        @Test
        void __() {
        }
    }

    @Nested
    class File_Test {

        @Test
        void __() {
        }
    }

    @Nested
    class DataOutput_Test {

        @Test
        void __() {
        }
    }

    @Nested
    class Writer_Test {

        @Test
        void __() {
        }
    }

    @Nested
    class Reader_Test {

        @Test
        void __() {
        }
    }

    @Nested
    class File_Charset_Test {

        @Test
        void __() {
        }
    }

    @Nested
    class AsInputStream_Test {

        @Test
        void __() {
        }
    }

    // ------------------------------------------------------------------------------------ java.net
    @Nested
    class DatagramPacket_Test {

        @Test
        void __() {
        }
    }

    @Nested
    class DatagramSocket_SocketAddress_Test {

        @Test
        void __() {
        }
    }

    @Nested
    class DatagramSocket_Test {

        @Test
        void __() {
        }
    }

    @Nested
    class Socket_Test {

        @Test
        void __() {
        }
    }

    @Nested
    class URLConnection_Test {

        @Test
        void __() {
        }
    }

    // ------------------------------------------------------------------------------- java.net.http
    @Nested
    class HttpRequestBuilder_String_Test {

        @Test
        void __() {
        }
    }

    // -------------------------------------------------------------------------------- java.net.ssl

    // ------------------------------------------------------------------------------------ java.nio
    @Nested
    class ByteBuffer_Test {

        @Test
        void __() {
        }
    }

    @Nested
    class Supplier_Test {

        @Test
        void __() {
        }
    }

    @Nested
    class ByteBuffer__Test {

        @Test
        void __() {
        }
    }

    @Nested
    class Put_Test {

        @Test
        void __() {
        }
    }

    // --------------------------------------------------------------------------- java.nio.channels
    @Nested
    class WritableByteChannel_Test {

        @Test
        void __() {
        }
    }

    @Nested
    class DatagramChannel_SocketAddress_Test {

        @Test
        void __() {
        }
    }

    @Nested
    class DatagramChannel_Test {

        @Test
        void __() {
        }
    }

    @Nested
    class AsynchronousByteChannel_Test {

        @Test
        void __() {
        }
    }

    @Nested
    class AsynchronousFileChannel_Long_Test {

        @Test
        void __() {
        }
    }

    // ------------------------------------------------------------------------------- java.nio.file
    @Nested
    class Path_Test {

        @Test
        void __() {
        }
    }

    // ------------------------------------------------------------------------------- java.security
    @Nested
    class MessageDigest_Test {

        @Test
        void __() {
        }
    }

    @Nested
    class Signature_Test {

        @Test
        void __() {
        }
    }

    // ------------------------------------------------------------------------------------ java.sql
    @Nested
    class SetAsciiStream_PreparedStatement_Int_Test {

        @Test
        void __() {
        }
    }

    @Nested
    class SetBinaryStream_PreparedStatement_Int_Test {

        @Test
        void __() {
        }
    }

    @Nested
    class SetBytes_PreparedStatement_Int_Test {

        @Test
        void __() {
        }
    }

    @Nested
    class SetCharacterStream_PreparedStatement_Int_Test {

        @Test
        void __() {
        }
    }

    @Nested
    class SetBinaryStream_Blob_Long_Test {

        @Test
        void __() {
        }
    }

    @Nested
    class SetBytes_Blob_Long_Test {

        @Test
        void __() {
        }
    }

    @Nested
    class SetAsciiStream_Clob_Long_Test {

        @Test
        void __() {
        }
    }

    @Nested
    class SetCharacterStream_Clob_Long_Test {

        @Test
        void __() {
        }
    }

    @Nested
    class SetString_Clob_Long_Test {

        @Test
        void __() {
        }
    }

    // ----------------------------------------------------------------------------------- java.util
    @Nested
    class BitSet_Int_Test {

        @Test
        void __() {
        }
    }

    @Nested
    class SequencedCollection_Function_Test {

        @Test
        void __() {
        }
    }

    @Nested
    class SequencedCollection_IntFunction_Test {

        @Test
        void __() {
        }
    }

    @Nested
    class SequencedMap_IntFunction_Function_Test {

        @Test
        void __() {
        }
    }

    // -------------------------------------------------------------------------- java.util.function
    @Nested
    class Consumer_Test {

        @Test
        void __() {
        }
    }

    @Nested
    class Consumer_Function_Test {

        @Test
        void __() {
        }
    }

    @Nested
    class IntConsumer_Test {

        @Test
        void __() {
        }
    }

    // ------------------------------------------------------------------------------- java.util.jar

    // ------------------------------------------------------------------------------- java.util.zip
    @Nested
    class Checksum_Test {

        @Test
        void __() {
        }
    }

    @Nested
    class Deflater_Test {

        @Test
        void __() {
        }
    }

    // ---------------------------------------------------------------------------- java.util.stream

    // -------------------------------------------------------------------------------- javax.crypto
    @Nested
    class Cipher_Consumer_Test {

        @Test
        void __() {
        }
    }

    @Nested
    class Mac_Test {

        @Test
        void __() {
        }
    }
}
