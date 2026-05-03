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
import java.sql.Clob;
import java.sql.SQLException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A class for testing {@link com.github.jinahya.hello.api.HelloWorld#setAsciiStream(Clob, long)}
 * method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see Clob#setAsciiStream(long)
 */
@Slf4j
class HelloWorld_SetAsciiStream_Clob_Long_Test
        extends HelloWorldTest {

    @DisplayName("(null, pos)NullPointerException")
    @Test
    void _ThrowNullPointerException_ClobIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final Clob clob = null;
        final var pos = ThreadLocalRandom.current().nextLong(1L, Long.MAX_VALUE);
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.setAsciiStream(clob, pos)
        );
    }

    @DisplayName("(clob, non-positive)IllegalArgumentException")
    @Test
    void _ThrowIllegalArgumentException_PosIsNotPositive() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var clob = Mockito.mock(Clob.class);
        final var pos = ThreadLocalRandom.current().nextLong(Long.MIN_VALUE, 1L);
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.setAsciiStream(clob, pos)
        );
    }

    @DisplayName("write(clob.setAsciiStream(pos))")
    @Test
    void __() throws IOException, SQLException {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorldTestUtils.write_outputstream_writes_hello_world_bytes(
                service());
        final var sink = new ByteArrayOutputStream();
        final var clob = Mockito.mock(Clob.class);
        final var pos = ThreadLocalRandom.current().nextLong(1L, Long.MAX_VALUE);
        Mockito.doReturn(sink).when(clob).setAsciiStream(pos);
        // ------------------------------------------------------------------------------------ when
        final var result = service.setAsciiStream(clob, pos);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(clob, Mockito.times(1)).setAsciiStream(pos);
        Mockito.verify(service, Mockito.times(1)).write((OutputStream) sink);
        Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(),
                                     sink.toByteArray());
        Assertions.assertSame(clob, result);
    }
}
