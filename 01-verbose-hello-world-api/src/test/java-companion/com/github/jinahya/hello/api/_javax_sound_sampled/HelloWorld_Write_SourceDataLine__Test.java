package com.github.jinahya.hello.api._javax_sound_sampled;


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

import com.github.jinahya.hello.api.*;
import com.github.jinahya.hello.api.annotations.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.mockito.*;

import javax.sound.sampled.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.mockito.Mockito.*;

/**
 * A class for exploring
 * {@link com.github.jinahya.hello.api.HelloWorld#write(SourceDataLine) write(line)} method with a
 * real {@link SourceDataLine}.
 * <p>
 * Tests in this class require an audio device, and make an audible click; hence they run only
 * when the {@value #PROPERTY_NAME} system property is {@code true}.
 * {@snippet lang = "sh":
 * ./mvnw test -pl 01-verbose-hello-world-api -Dtest=HelloWorld_Write_SourceDataLine__Test -Dhello.audio=true
 *}
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@EnabledIfSystemProperty(named = HelloWorld_Write_SourceDataLine__Test.PROPERTY_NAME,
                         matches = "true")
@_HideNameFromPublishing
@DisplayName("write(line) / extras")
@Slf4j
class HelloWorld_Write_SourceDataLine__Test extends HelloWorld__Test {

    /**
     * The name of the system property which enables tests in this class.
     */
    static final String PROPERTY_NAME = "hello.audio";

    // ---------------------------------------------------------------------------------------------

    /**
     * Stubs {@code service.write(line)} to write the {@code hello-world-bytes} to the line before
     * each test.
     */
    @BeforeEach
    void __stubService() {
        doAnswer(i -> {
            final var line = i.getArgument(0, SourceDataLine.class);
            line.write(hello_world_byte_array(), 0, HelloWorld.BYTES);
            return line;
        }).when(service()).write(ArgumentMatchers.<SourceDataLine>notNull());
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Writes the {@code hello-world-bytes}, as 8-bit, signed, mono samples at 8,000 Hz, to a real
     * {@link SourceDataLine}; i.e., {@value HelloWorld#BYTES} frames, 1.5 milliseconds.
     */
    @DisplayName("real SourceDataLine")
    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var format = new AudioFormat(8000.0f, Byte.SIZE, 1, true, false);
        final SourceDataLine line;
        try {
            line = AudioSystem.getSourceDataLine(format);
            line.open(format);
        } catch (final LineUnavailableException | IllegalArgumentException e) {
            Assumptions.abort("no source data line available: " + e);
            return;
        }
        try (line) {
            line.start();
            // -------------------------------------------------------------------------------- when
            service().write(line);
            line.drain();
            // -------------------------------------------------------------------------------- then
            log.debug("format: {}", line.getFormat());
            log.debug("frames: {}", line.getLongFramePosition());
        }
    }
}
