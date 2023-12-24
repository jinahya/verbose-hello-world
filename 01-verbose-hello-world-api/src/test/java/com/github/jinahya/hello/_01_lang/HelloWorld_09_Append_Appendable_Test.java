package com.github.jinahya.hello._01_lang;

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
import com.github.jinahya.hello._HelloWorldTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

/**
 * A class for testing {@link HelloWorld#append(Appendable) append(appendable)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_09_Append_Appendable_Arguments_Test
 */
@DisplayName("append(appendable)")
@Slf4j
@SuppressWarnings({
        "java:S101"
})
class HelloWorld_09_Append_Appendable_Test extends _HelloWorldTest {

    @BeforeEach
    void beforeEach() {
        setArray_willReturnTheArray();
    }

    /**
     * Verifies {@link HelloWorld#append(Appendable) append(appendable)} method invokes
     * {@link HelloWorld#set(byte[]) set(array)} method with an array of {@value HelloWorld#BYTES}
     * bytes, appends a string create with the {@code array} to {@code appendable}, and returns the
     * {@code appendable}.
     */
    @DisplayName("-> print(char[12]) -> appendable.append(each-char)")
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var appendable = mock(Appendable.class);
        try (var mock = Mockito.mockConstruction(String.class)) {
            // -------------------------------------------------------------------------------- when
            final var result = service.append(appendable);
            // -------------------------------------------------------------------------------- then
            // TODO: verify set(arrayCaptor().capture()) invoked, once
            // TODO: asert arrayCaptor().getValue() is not null
            // TODO: asert arrayCaptor().getValue()'s length is equal to HelloWorld.BYTES
            // TODO: verify new String(array, StandardCharset.US_ASCII) invoked, once
            // TODO: verify appendable.append(string) invoked, once
            assertSame(appendable, result);
        }
    }
}
