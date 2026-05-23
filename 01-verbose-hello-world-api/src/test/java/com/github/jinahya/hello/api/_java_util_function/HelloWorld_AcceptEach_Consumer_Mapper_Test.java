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

import com.github.jinahya.hello.api.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.function.*;

/**
 * A class for testing {@link HelloWorld#acceptEach(Consumer, Function)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("acceptEach(Consumer, Function)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_AcceptEach_Consumer_Mapper_Test
        extends HelloWorldTest {

    @DisplayName("""
            should throw a <NullPointerException>
            when the <consumer> argument is <null>""")
    @Test
    void _ThrowNullPointerException_ConsumerIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var consumer = (Consumer<Byte>) null;
        final Function<Byte, Byte> mapper = b -> b;
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.acceptEach(consumer, mapper)
        );
    }

    @DisplayName("""
            should throw a <NullPointerException>
            when the <mapper> argument is <null>""")
    @Test
    void _ThrowNullPointerException_MapperIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        @SuppressWarnings("unchecked")
        final var consumer = (Consumer<Byte>) Mockito.mock(Consumer.class);
        final var mapper = (Function<Byte, Byte>) null;
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.acceptEach(consumer, mapper)
        );
    }

    @DisplayName(
            "should invoke <set(byte[])>, and <consumer.accept(mapper.apply(b))> for each byte")
    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorldTestUtils.set_array_sets_random_bytes(service());
        @SuppressWarnings("unchecked")
        final var consumer = (Consumer<String>) Mockito.mock(Consumer.class);
        @SuppressWarnings("unchecked")
        final var mapper = (Function<Byte, String>) Mockito.mock(Function.class);
        Mockito.when(mapper.apply(Mockito.anyByte()))
                .thenAnswer(i -> "m:" + i.<Byte>getArgument(0));
        // ------------------------------------------------------------------------------------ when
        final var result = service.acceptEach(consumer, mapper);
        // ------------------------------------------------------------------------------------ then
        final var array = HelloWorldTestUtils.set_array12_invoked_once(service);
        final var inOrder = Mockito.inOrder(mapper, consumer);
        for (final var b : array) {
            inOrder.verify(mapper, Mockito.calls(1)).apply(b);
            inOrder.verify(consumer, Mockito.calls(1)).accept("m:" + b);
        }
        inOrder.verifyNoMoreInteractions();
        Assertions.assertSame(consumer, result);
    }
}
