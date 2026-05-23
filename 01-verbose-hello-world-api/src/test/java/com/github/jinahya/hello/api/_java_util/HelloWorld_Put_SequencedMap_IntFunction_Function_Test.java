package com.github.jinahya.hello.api._java_util;

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

import java.util.*;
import java.util.function.*;

/**
 * A class for testing
 * {@link HelloWorld#put(SequencedMap, IntFunction, Function) put(map, keyMapper, valueMapper)}
 * method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("put(SequencedMap, IntFunction, Function)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Put_SequencedMap_IntFunction_Function_Test
        extends HelloWorldTest {

    @DisplayName("""
            should throw a <NullPointerException>
            when the <map> argument is <null>""")
    @Test
    void _ThrowNullPointerException_MapIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var map = (SequencedMap<Integer, Byte>) null;
        final IntFunction<Integer> keyMapper = i -> i;
        final Function<Byte, Byte> valueMapper = b -> b;
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.put(map, keyMapper, valueMapper)
        );
    }

    @DisplayName("""
            should throw a <NullPointerException>
            when the <keyMapper> argument is <null>""")
    @Test
    void _ThrowNullPointerException_KeyMapperIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var map = new LinkedHashMap<Integer, Byte>();
        final IntFunction<Integer> keyMapper = null;
        final Function<Byte, Byte> valueMapper = b -> b;
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.put(map, keyMapper, valueMapper)
        );
    }

    @DisplayName("""
            should throw a <NullPointerException>
            when the <valueMapper> argument is <null>""")
    @Test
    void _ThrowNullPointerException_ValueMapperIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var map = new LinkedHashMap<Integer, Byte>();
        final IntFunction<Integer> keyMapper = i -> i;
        final Function<Byte, Byte> valueMapper = null;
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.put(map, keyMapper, valueMapper)
        );
    }

    @DisplayName("""
            should invoke <set(byte[])>, and \
            <map.put(keyMapper.apply(i), valueMapper.apply(array[i]))> for each i""")
    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorldTestUtils.set_array_sets_random_bytes(service());
        @SuppressWarnings("unchecked")
        final var map = (SequencedMap<Integer, Byte>) Mockito.mock(SequencedMap.class);
        @SuppressWarnings("unchecked")
        final IntFunction<Integer> keyMapper = Mockito.mock(IntFunction.class);
        Mockito.when(keyMapper.apply(Mockito.anyInt())).thenAnswer(i -> (int) i.getArgument(0));
        @SuppressWarnings("unchecked")
        final Function<Byte, Byte> valueMapper = Mockito.mock(Function.class);
        Mockito.when(valueMapper.apply(Mockito.any())).thenAnswer(i -> i.getArgument(0));
        // ------------------------------------------------------------------------------------ when
        final var result = service.put(map, keyMapper, valueMapper);
        // ------------------------------------------------------------------------------------ then
        final var array = HelloWorldTestUtils.set_array12_invoked_once(service);
        final var inOrder = Mockito.inOrder(keyMapper, valueMapper, map);
        for (var i = 0; i < array.length; i++) {
            final var b = array[i];
            inOrder.verify(keyMapper, Mockito.calls(1)).apply(i);
            inOrder.verify(valueMapper, Mockito.calls(1)).apply(b);
            inOrder.verify(map, Mockito.calls(1)).put(i, b);
        }
        inOrder.verifyNoMoreInteractions();
        Assertions.assertSame(map, result);
    }
}
