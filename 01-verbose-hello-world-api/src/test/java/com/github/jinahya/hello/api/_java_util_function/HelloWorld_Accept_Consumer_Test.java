package com.github.jinahya.hello.api._java_util_function;

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

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import com.github.jinahya.hello.api.畵蛇添足;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.util.function.Consumer;

/**
 * A class for testing {@link HelloWorld#accept(Consumer) accept(consumer)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("accept(Consumer)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Accept_Consumer_Test
        extends HelloWorldTest {

    @DisplayName("""
            should throw a <NullPointerException>
            when the <consumer> argument is <null>""")
    @Test
    void _ThrowNullPointerException_ConsumerIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var consumer = (Consumer<Byte>) null;
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.accept(consumer)
        );
    }

    @DisplayName("should invoke <set(byte[])>, and <consumer.accept(b)> for each byte")
    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorldTestUtils.set_array_returns_the_array(service());
        @SuppressWarnings("unchecked")
        final var consumer = (Consumer<Byte>) Mockito.mock(Consumer.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.accept(consumer);
        // ------------------------------------------------------------------------------------ then
        final var array = HelloWorldTestUtils.set_array12_invoked_once(service);
        final var inOrder = Mockito.inOrder(consumer);
        for (final var b : array) {
            inOrder.verify(consumer, Mockito.calls(1)).accept(b);
        }
        inOrder.verifyNoMoreInteractions();
        Assertions.assertSame(consumer, result);
    }

    @畵蛇添足
    @Test
    void _添足_畵蛇() {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorldTestUtils.set_array_sets_actual_hello_world_bytes(service());
        final var baos = new ByteArrayOutputStream();
        final Consumer<Number> consumer = b -> {
            baos.write(b.byteValue());
        };
        // ------------------------------------------------------------------------------------ when
        service.accept(consumer);
        // ------------------------------------------------------------------------------------ then
        Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(),
                                     baos.toByteArray());
    }
}
