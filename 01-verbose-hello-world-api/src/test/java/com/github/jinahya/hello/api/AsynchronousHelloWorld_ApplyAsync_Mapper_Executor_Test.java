package com.github.jinahya.hello.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.concurrent.Executor;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
class AsynchronousHelloWorld_ApplyAsync_Mapper_Executor_Test
        extends AsynchronousHelloWorldTest {

    @Test
    void _ThrowNullPointerException_MapperIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = asynchronousService();
        final var mapper = (Function<HelloWorld, Object>) null;
        final var executor = Mockito.mock(Executor.class);
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.applyAsync(mapper, executor)
        );
    }

    @Test
    void _ThrowNullPointerException_ExecutorIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = asynchronousService();
        final var mapper = Mockito.mock(Function.class);
        final var executor = (Executor) null;
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.applyAsync(mapper, executor)
        );
    }

    @Test
    @SuppressWarnings({"unchecked"})
    void __() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var service = asynchronousService();
        final var result = 42;
        final var mapper = (Function<HelloWorld, Integer>) Mockito.mock(Function.class);
        Mockito.when(mapper.apply(ArgumentMatchers.notNull()))
                .thenReturn(result);
        final var executor = Mockito.mock(Executor.class);
        Mockito.doAnswer(i -> {
            i.getArgument(0, Runnable.class).run();
            return null;
        }).when(executor).execute(ArgumentMatchers.notNull());
        // ------------------------------------------------------------------------------------ when
        final var stage = service.applyAsync(mapper, executor);
        // ------------------------------------------------------------------------------------ then
        Assertions.assertSame(result, stage.toCompletableFuture().get());
        Mockito.verify(executor, Mockito.times(1)).execute(ArgumentMatchers.notNull());
        Mockito.verify(mapper, Mockito.times(1))
                .apply(ArgumentMatchers.notNull());
    }

    // ----------------------------------------------------------------------------------- java.lang
    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class ArrayIndex_Test {

        @Test
        void __() {
        }
    }

    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class Array_Test {

        @Test
        void __() {
        }
    }

    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class ByteArray_Test {

        @Test
        void __() {
        }
    }

    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class String_Test {

        @Test
        void __() {
        }
    }

    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class Appendable_Test {

        @Test
        void __() {
        }
    }

    // --------------------------------------------------------------------------- java.lang.foreign
    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class Segment_Test {

        @Test
        void __() {
        }
    }

    // ------------------------------------------------------------------------------------- java.io
    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class OutputStream_Test {

        @Test
        void __() {
        }
    }

    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class InputStream_Test {

        @Test
        void __() {
        }
    }

    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class File_Test {

        @Test
        void __() {
        }
    }

    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class DataOutput_Test {

        @Test
        void __() {
        }
    }

    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class Writer_Test {

        @Test
        void __() {
        }
    }

    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class Reader_Test {

        @Test
        void __() {
        }
    }

    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class File_Charset_Test {

        @Test
        void __() {
        }
    }

    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class AsInputStream_Test {

        @Test
        void __() {
        }
    }

    // ------------------------------------------------------------------------------------ java.net
    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class DatagramPacket_Test {

        @Test
        void __() {
        }
    }

    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class DatagramSocket_SocketAddress_Test {

        @Test
        void __() {
        }
    }

    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class DatagramSocket_Test {

        @Test
        void __() {
        }
    }

    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class Socket_Test {

        @Test
        void __() {
        }
    }

    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class URLConnection_Test {

        @Test
        void __() {
        }
    }

    // ------------------------------------------------------------------------------- java.net.http
    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class HttpRequestBuilder_String_Test {

        @Test
        void __() {
        }
    }

    // -------------------------------------------------------------------------------- java.net.ssl

    // ------------------------------------------------------------------------------------ java.nio
    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class ByteBuffer_Test {

        @Test
        void __() {
        }
    }

    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class Supplier_Test {

        @Test
        void __() {
        }
    }

    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class ByteBuffer__Test {

        @Test
        void __() {
        }
    }

    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class Put_Test {

        @Test
        void __() {
        }
    }

    // --------------------------------------------------------------------------- java.nio.channels
    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class WritableByteChannel_Test {

        @Test
        void __() {
        }
    }

    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class DatagramChannel_SocketAddress_Test {

        @Test
        void __() {
        }
    }

    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class DatagramChannel_Test {

        @Test
        void __() {
        }
    }

    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class AsynchronousByteChannel_Test {

        @Test
        void __() {
        }
    }

    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class AsynchronousFileChannel_Long_Test {

        @Test
        void __() {
        }
    }

    // ------------------------------------------------------------------------------- java.nio.file
    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class Path_Test {

        @Test
        void __() {
        }
    }

    // ------------------------------------------------------------------------------- java.security
    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class MessageDigest_Test {

        @Test
        void __() {
        }
    }

    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class Signature_Test {

        @Test
        void __() {
        }
    }

    // ------------------------------------------------------------------------------------ java.sql
    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class SetAsciiStream_PreparedStatement_Int_Test {

        @Test
        void __() {
        }
    }

    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class SetBinaryStream_PreparedStatement_Int_Test {

        @Test
        void __() {
        }
    }

    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class SetBytes_PreparedStatement_Int_Test {

        @Test
        void __() {
        }
    }

    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class SetCharacterStream_PreparedStatement_Int_Test {

        @Test
        void __() {
        }
    }

    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class SetBinaryStream_Blob_Long_Test {

        @Test
        void __() {
        }
    }

    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class SetBytes_Blob_Long_Test {

        @Test
        void __() {
        }
    }

    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class SetAsciiStream_Clob_Long_Test {

        @Test
        void __() {
        }
    }

    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class SetCharacterStream_Clob_Long_Test {

        @Test
        void __() {
        }
    }

    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class SetString_Clob_Long_Test {

        @Test
        void __() {
        }
    }

    // ----------------------------------------------------------------------------------- java.util
    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class BitSet_Int_Test {

        @Test
        void __() {
        }
    }

    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class SequencedCollection_Function_Test {

        @Test
        void __() {
        }
    }

    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class SequencedCollection_IntFunction_Test {

        @Test
        void __() {
        }
    }

    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class SequencedMap_IntFunction_Function_Test {

        @Test
        void __() {
        }
    }

    // -------------------------------------------------------------------------- java.util.function
    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class Consumer_Test {

        @Test
        void __() {
        }
    }

    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class Consumer_Function_Test {

        @Test
        void __() {
        }
    }

    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class IntConsumer_Test {

        @Test
        void __() {
        }
    }

    // ------------------------------------------------------------------------------- java.util.jar

    // ------------------------------------------------------------------------------- java.util.zip
    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class Checksum_Test {

        @Test
        void __() {
        }
    }

    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class Deflater_Test {

        @Test
        void __() {
        }
    }

    // ---------------------------------------------------------------------------- java.util.stream

    // -------------------------------------------------------------------------------- javax.crypto
    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class Cipher_Consumer_Test {

        @Test
        void __() {
        }
    }

    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class Mac_Test {

        @Test
        void __() {
        }
    }
}
