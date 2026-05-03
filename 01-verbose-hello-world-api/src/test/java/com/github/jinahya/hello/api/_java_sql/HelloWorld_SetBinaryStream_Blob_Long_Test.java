package com.github.jinahya.hello.api._java_sql;

import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A class for testing {@link com.github.jinahya.hello.api.HelloWorld#setBinaryStream(Blob, long)}
 * method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see Blob#setBinaryStream(long)
 */
@Slf4j
class HelloWorld_SetBinaryStream_Blob_Long_Test
        extends HelloWorldTest {

    @DisplayName("(null, pos)NullPointerException")
    @Test
    void _ThrowNullPointerException_BlobIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final Blob blob = null;
        final var pos = ThreadLocalRandom.current().nextLong(1L, Long.MAX_VALUE);
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.setBinaryStream(blob, pos)
        );
    }

    @DisplayName("(blob, non-positive)IllegalArgumentException")
    @Test
    void _ThrowIllegalArgumentException_PosIsNotPositive() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var blob = Mockito.mock(Blob.class);
        final var pos = ThreadLocalRandom.current().nextLong(Long.MIN_VALUE, 1L);
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.setBinaryStream(blob, pos)
        );
    }

    @DisplayName("write(blob.setBinaryStream(pos))")
    @Test
    void __() throws IOException, SQLException {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorldTestUtils.write_outputstream_writes_hello_world_bytes(
                service());
        final var sink = new ByteArrayOutputStream();
        final var blob = Mockito.mock(Blob.class);
        final var pos = ThreadLocalRandom.current().nextLong(1L, Long.MAX_VALUE);
        Mockito.doReturn(sink).when(blob).setBinaryStream(pos);
        // ------------------------------------------------------------------------------------ when
        final var result = service.setBinaryStream(blob, pos);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(blob, Mockito.times(1)).setBinaryStream(pos);
        Mockito.verify(service, Mockito.times(1)).write((OutputStream) sink);
        Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(),
                                     sink.toByteArray());
        Assertions.assertSame(blob, result);
    }
}
