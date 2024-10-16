package com.github.jinahya.hello._02_java_io;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2019 Jinahya, Inc.
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

import com.github.jinahya.hello.HelloWorld;
import com.github.jinahya.hello.HelloWorldTest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A class for testing {@link HelloWorld#write(OutputStream) write(stream)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(stream)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_01_Write_OutputStream_Test extends HelloWorldTest {

    /**
     * Verifies that the {@link HelloWorld#write(OutputStream) write(stream)} method throws a
     * {@link NullPointerException} when the {@code stream} argument is {@code null}.
     */
    @DisplayName("""
            should throw a <NullPointerException>
            when the <stream> argument is <null>"""
    )
    @Test
    void _ThrowNullPointerException_StreamIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var stream = (OutputStream) null;
        // ------------------------------------------------------------------------------- when/then
        // assert, <service.write(stream)> throws a <NullPointerException>
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.write(stream)
        );
    }

    /**
     * Asserts {@link HelloWorld#write(OutputStream) write(stream)} method invokes
     * {@link HelloWorld#set(byte[]) set(array)} method with an array of {@value HelloWorld#BYTES}
     * bytes, {@link OutputStream#write(byte[]) writes} the {@code array} to the {@code stream}, and
     * returns the {@code stream}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("""
            should invoke <set[array[12])>
            and writes the <array> to the <stream>"""
    )
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // stub, <service.set(array)> will return the <array>
        Mockito.doAnswer(i -> i.getArgument(0))
                .when(service)
                .set(ArgumentMatchers.any());
        final var stream = Mockito.mock(OutputStream.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.write(stream);
        // ------------------------------------------------------------------------------------ then
        // verify, <set(byte[12])> invoked, once
        final var array = verify_set_array12_invoked_once();
        // verify, <stream.write(array)> invoked, once

        // verify, no more interactions with the <stream>

        // verify, <result> is same as <stream>
        Assertions.assertSame(stream, result);
    }
}
