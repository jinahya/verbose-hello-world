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
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.Writer;

/**
 * A class for testing {@link HelloWorld#write(Writer) write(writer)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(writer)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_05_Write_Writer_Test extends HelloWorldTest {

    /**
     * Verifies that the {@link HelloWorld#write(Writer) write(writer)} method throws a
     * {@link NullPointerException} when the {@code writer} argument is {@code null}.
     */
    @DisplayName("""
            should throw a NullPointerException
            when the writer argument is null"""
    )
    @Test
    void _ThrowNullPointerException_WriterIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var writer = (Writer) null;
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.write(writer)
        );
    }

    /**
     * Verifies that the {@link HelloWorld#write(Writer) write(writer)} method invokes
     * {@link HelloWorld#append(Appendable) append(appendable)} method with given {@code writer},
     * and returns the {@code writer}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("should invoke append(writer)")
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        BDDMockito.willAnswer(i -> i.getArgument(0))
                .given(service())
                .append(ArgumentMatchers.any(Appendable.class));
        final var writer = Mockito.mock(Writer.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.write(writer);
        // ------------------------------------------------------------------------------------ then
        // TODO: verify, service.append(writer) invoked, once
        Assertions.assertSame(writer, result);
    }
}
