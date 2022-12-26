package com.github.jinahya.hello;

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

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

/**
 * A class for testing {@link HelloWorld#write(OutputStream) write(stream)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_02_Write_OutputStream_Arguments_Test
 */
@DisplayName("write(stream)")
@Slf4j
class HelloWorld_02_Write_OutputStream_Test extends HelloWorldTest {

    /**
     * Stubs {@link HelloWorld#set(byte[]) set(array)} method to just return the {@code array}
     * argument.
     */
    @BeforeEach
    void _ReturnArray_SetArray() {
        // GIVEN
        var service = service();
        // WHEN service.set(array) invoked THEN just return array
        doAnswer(i -> i.getArgument(0))
                .when(service())
                .set(any());
    }

    /**
     * Asserts {@link HelloWorld#write(OutputStream) write(stream)} method invokes
     * {@link HelloWorld#set(byte[]) set(array)} method with an array of {@value HelloWorld#BYTES}
     * bytes, and invokes {@link OutputStream#write(byte[])} method on {@code stream} with the
     * {@code array}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("invokes set(array[12])"
                 + ", invokes stream.write(array)")
    @Test
    void _InvokeSetArrayAndWriteArrayToStream_() throws IOException {
        // GIVEN: HelloWorld
        var service = service();
        // GIVEN: OutputStream
        // TODO: Create a mock object of java.io.OutputStream, say 'stream'
        // WHEN
        // TODO: Invoke service.write(stream)
        // THEN: once, set(array[12]) invoked
        // TODO: Verify set(array[12]) invoked
        // THEN: once, write(array) invoked
        // TODO: Verify stream.write(array) invoked
    }

    /**
     * Asserts {@link HelloWorld#write(OutputStream) write(stream)} method returns given
     * {@code stream} argument.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("returns stream")
    @Test
    void _ReturnStream_() throws IOException {
        // GIVEN: HelloWorld
        var service = service();
        // GIVEN: OutputStream
        var stream = mock(OutputStream.class);
        // WHEN
        var actual = service.write(stream);
        // THEN: actual is same as stream
        // TODO: Verify actual is same as stream
    }
}
