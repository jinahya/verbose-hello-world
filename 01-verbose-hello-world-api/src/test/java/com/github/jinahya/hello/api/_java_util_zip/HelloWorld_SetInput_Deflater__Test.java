package com.github.jinahya.hello.api._java_util_zip;

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_SetInput_Deflater__Test
        extends HelloWorldTest {

    @BeforeEach
    void __() {
        Mockito.doAnswer(i -> {
            final var deflator = i.getArgument(0, Deflater.class);
            deflator.setInput(HelloWorldTestUtils.hello_world_byte_array());
            return deflator;
        }).when(service()).setInput(ArgumentMatchers.<Deflater>notNull());
    }

    // ---------------------------------------------------------------------------------------------
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, -1})
    @ParameterizedTest
    void __(final int level) throws DataFormatException {
        // ----------------------------------------------------------------------------------- given
        // -----------------------------------------------------------------------------------------
        try (var deflater = new Deflater(level)) {
            service().setInput(deflater);
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
