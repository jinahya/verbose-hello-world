package com.github.jinahya.hello.api._java_util_zip;

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.zip.Checksum;

/**
 * A class for testing {@link HelloWorld#update(Checksum) update(checksum)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("update(Checksum)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Update_Checksum_Test
        extends HelloWorldTest {

    @DisplayName("""
            should throw a <NullPointerException>
            when the <checksum> argument is <null>""")
    @Test
    void _ThrowNullPointerException_ChecksumIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final Checksum checksum = null;
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.update(checksum)
        );
    }

    @DisplayName("checksum.update(set(byte[12]))")
    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorldTestUtils.set_array_sets_random_bytes(service());
        final var checksum = Mockito.mock(Checksum.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.update(checksum);
        // ------------------------------------------------------------------------------------ then
        final var array = HelloWorldTestUtils.set_array12_invoked_once(service);
        Mockito.verify(checksum, Mockito.times(1)).update(array);
        Mockito.verifyNoMoreInteractions(checksum);
        Assertions.assertSame(checksum, result);
    }
}
