package com.github.jinahya.hello.api._java_util_zip;

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.stream.Stream;
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.CRC32C;
import java.util.zip.Checksum;

/**
 * Tests for {@link HelloWorld#update(Checksum)} method.
 *
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/zip/Checksum.html">Checksum</a>
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/zip/CRC32.html">CRC32</a>
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/zip/CRC32C.html">CRC32C</a>
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/zip/Adler32.html">Adler32</a>
 */
@Slf4j
class HelloWorld_Update_Checksum_Test extends HelloWorldTest {

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void __() {
        HelloWorldTestUtils.stub_set_array_will_set_actual_hello_world_bytes(service());
    }

    @Test
    void _ThrowNullPointerException_ChecksumIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(NullPointerException.class, () -> service.update((Checksum) null));
    }

    private static Stream<Checksum> getChecksumStream() {
        return Stream.of(
                Mockito.spy(new CRC32()),
                Mockito.spy(new CRC32C()),
                Mockito.spy(new Adler32())
        );
    }

    @DisplayName("update(Checksum)")
    @MethodSource("getChecksumStream")
    @ParameterizedTest
    void __(final Checksum checksum) {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // ------------------------------------------------------------------------------------ when
        final var result = service.update(checksum);
        // ------------------------------------------------------------------------------------ then
        final var array = verify_set_array12_invoked_once();      // <1>
        Mockito.verify(checksum, Mockito.times(1)).update(array); // <2>
        Assertions.assertSame(checksum, result);                  // <3>
        final var value = checksum.getValue();
        log.debug("{}: 0x{}", String.format("%1$7s", checksum.getClass().getSimpleName()),
                  Long.toHexString(value).toUpperCase());
    }
}
