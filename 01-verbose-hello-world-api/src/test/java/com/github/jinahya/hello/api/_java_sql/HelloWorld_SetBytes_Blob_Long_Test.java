package com.github.jinahya.hello.api._java_sql;

import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A class for testing {@link com.github.jinahya.hello.api.HelloWorld#setBytes(Blob, long)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see Blob#setBytes(long, byte[])
 */
@Slf4j
class HelloWorld_SetBytes_Blob_Long_Test
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
                () -> service.setBytes(blob, pos)
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
                () -> service.setBytes(blob, pos)
        );
    }

    @DisplayName("blob.setBytes(pos, set(byte[12]))")
    @Test
    void __() throws SQLException {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorldTestUtils.set_array_returns_the_array(service());
        final var blob = Mockito.mock(Blob.class);
        final var pos = ThreadLocalRandom.current().nextLong(1L, Long.MAX_VALUE);
        // ------------------------------------------------------------------------------------ when
        final var result = service.setBytes(blob, pos);
        // ------------------------------------------------------------------------------------ then
        final var array = HelloWorldTestUtils.set_array12_invoked_once(service);
        Mockito.verify(blob, Mockito.times(1)).setBytes(pos, array);
        Assertions.assertSame(blob, result);
    }
}
