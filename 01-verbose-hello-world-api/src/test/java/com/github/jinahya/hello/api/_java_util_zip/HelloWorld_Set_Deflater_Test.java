package com.github.jinahya.hello.api._java_util_zip;

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorld_Set_Deflater_Test
        extends HelloWorldTest {

    @Test
    void _ThrowNullPointerException_DeflaterIsNull() {
        final var service = service();
        // -----------------------------------------------------------------------------------------
        Assertions.assertThrows(NullPointerException.class, () -> service.input(null));
    }

    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, -1})
    @ParameterizedTest
    void __(final int level)
            throws DataFormatException {
        // -----------------------------------------------------------------------------------------
        final var service = service();
        Mockito.doAnswer(i -> i.getArgument(0, byte[].class))
                .when(service)
                .set(Mockito.any(byte[].class));
        // -----------------------------------------------------------------------------------------
        try (var deflater = new Deflater(level)) {
            service.input(deflater);
            deflater.finish();
            final var deflationOutput = ByteBuffer.allocate(HelloWorld.BYTES << 2);
            while (!deflater.finished()) {
                final var written = deflater.deflate(deflationOutput);
                if (written == 0) {
                    Assertions.assertFalse(deflater.needsInput());
                }
            }
            // -------------------------------------------------------------------------------------
            log.debug("level: {}", level);
            log.debug("\tdeflationLength: {}", deflationOutput.position());
            try (var inflater = new Inflater()) {
                inflater.setInput(deflationOutput.flip());
                final var inflationOutput = ByteBuffer.allocate(HelloWorld.BYTES);
                while (!inflater.finished()) {
                    final var written = inflater.inflate(inflationOutput);
                    if (written == 0) {
                        Assertions.assertFalse(inflater.needsInput());
                        Assertions.assertFalse(inflater.needsDictionary());
                    }
                }
                Assertions.assertTrue(inflater.finished());
                log.debug("\tinflationLength: {}", inflationOutput.position());
                Assertions.assertEquals(HelloWorld.BYTES, inflationOutput.position());
            }
        }
    }
}
