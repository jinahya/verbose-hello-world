package com.github.jinahya.hello.api._java_util_zip;

import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.stream.Stream;
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.CRC32C;
import java.util.zip.Checksum;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Update_Checksum__Test
        extends HelloWorldTest {

    private static Stream<Checksum> getChecksumStream() {
        return Stream.of(
                Mockito.spy(new CRC32()),
                Mockito.spy(new CRC32C()),
                Mockito.spy(new Adler32())
        );
    }

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void __() {
        Mockito.doAnswer(i -> {
            final var checksum = i.getArgument(0, Checksum.class);
            checksum.update(HelloWorldTestUtils.hello_world_byte_array());
            return checksum;
        }).when(service()).update(ArgumentMatchers.<Checksum>notNull());
    }

    // ---------------------------------------------------------------------------------------------
    @MethodSource("getChecksumStream")
    @ParameterizedTest
    void __(final Checksum checksum) {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // ------------------------------------------------------------------------------------ when
        final var result = service.update(checksum);
        // ------------------------------------------------------------------------------------ then
        Assertions.assertSame(checksum, result);
        final var value = checksum.getValue();
        System.out.printf("%7s: 0x%s%n", checksum.getClass().getSimpleName(),
                          Long.toHexString(value));
    }
}
