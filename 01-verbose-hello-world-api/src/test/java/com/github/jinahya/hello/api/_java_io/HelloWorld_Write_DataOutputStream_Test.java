package com.github.jinahya.hello.api._java_io;

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

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A class for testing {@link HelloWorld#write(java.io.DataOutputStream)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(data)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Write_DataOutputStream_Test
        extends HelloWorldTest {

    /**
     * Asserts {@link HelloWorld#write(DataOutputStream)} method throws a
     * {@link NullPointerException} when the {@code data} argument is {@code null}.
     */
    @DisplayName("""
            should throw a <NullPointerException>
            when the <output> argument is <null>"""
    )
    @Test
    void _ThrowNullPointerException_DataIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var output = (DataOutputStream) null;
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.write(output)
        );
    }

    /**
     * Asserts {@link HelloWorld#write(DataOutputStream)} method invokes
     * {@link HelloWorld#write(DataOutput)} method with {@code stream}, and returns the
     * {@code stream}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("should invoke <set(array[12])>, and invoke output.write(array)")
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        Mockito.doAnswer(i -> {
                    final var output = i.getArgument(0, DataOutput.class);
                    output.write(new byte[HelloWorld.BYTES]);
                    return output;
                })
                .when(service)
                .write(ArgumentMatchers.<DataOutput>notNull());
        final var output = Mockito.mock(DataOutputStream.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.write(output);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(service, Mockito.times(1)).write((DataOutput) output);
        Assertions.assertSame(output, result);
    }
}
