package com.github.jinahya.hello.api._java_nio;

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
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

/**
 * A class for testing {@link HelloWorld#put(CharBuffer) put(buffer)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("put(CharBuffer)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Put_CharBuffer_Test
        extends HelloWorldTest {

    @Test
    void _ThrowNullPointerException_BufferIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var buffer = (CharBuffer) null;
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.put(buffer)
        );
    }

    @DisplayName("should invoke append(buffer)")
    @Test
    void _ShouldInvokeAppendWithBuffer_() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorldTestUtils.append_appendable_appends_12_chars(service());
        final var buffer = CharBuffer.allocate(HelloWorld.BYTES);
        // ------------------------------------------------------------------------------------ when
        final var result = service.put(buffer);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(service, Mockito.times(1)).append(buffer);
        Assertions.assertSame(buffer, result);
    }

    @Test
    void _添足_畵蛇() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorldTestUtils.append_appendable_appends_12_chars(service());
        final var buffer = ByteBuffer.allocate(HelloWorld.BYTES << 1);
        // ------------------------------------------------------------------------------------ when
        final var result = service.put(buffer.asCharBuffer());
        // ------------------------------------------------------------------------------------ then
        Assertions.assertEquals(0, result.remaining());
        assert buffer.remaining() == HelloWorld.BYTES << 1;
    }
}
