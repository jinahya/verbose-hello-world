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

import java.util.*;
import java.util.function.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing
 * {@link HelloWorld#put(SequencedMap, Function, Function) put(map, keyMapper, valueMapper)}
 * method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("put(SequencedMap, Function, Function)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Put_SequencedMap_Function_Function_Test extends HelloWorld__Test {

    @DisplayName("""
            should throw a <NullPointerException>
            when the <map> argument is <null>""")
    @Test
    void _ThrowNullPointerException_MapIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var map = (SequencedMap<Integer, Byte>) null;
        final Function<Byte, Integer> keyMapper = Byte::intValue;
        final Function<Byte, Byte> valueMapper = b -> b;
        // ----------------------------------------------------------------------------- when / then
        assertThrows(NullPointerException.class, () -> service.put(map, keyMapper, valueMapper));
    }

    @DisplayName("""
            should throw a <NullPointerException>
            when the <keyMapper> argument is <null>""")
    @Test
    void _ThrowNullPointerException_KeyMapperIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var map = new LinkedHashMap<Integer, Byte>();
        final Function<Byte, Integer> keyMapper = null;
        final Function<Byte, Byte> valueMapper = b -> b;
        // ----------------------------------------------------------------------------- when / then
        assertThrows(NullPointerException.class, () -> service.put(map, keyMapper, valueMapper));
    }

    @DisplayName("""
            should throw a <NullPointerException>
            when the <valueMapper> argument is <null>""")
    @Test
    void _ThrowNullPointerException_ValueMapperIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var map = new LinkedHashMap<Integer, Byte>();
        final Function<Byte, Integer> keyMapper = Byte::intValue;
        final Function<Byte, Byte> valueMapper = null;
        // ----------------------------------------------------------------------------- when / then
        assertThrows(NullPointerException.class, () -> service.put(map, keyMapper, valueMapper));
    }

    @DisplayName("""
            should invoke <set(byte[])>, and \
            <map.putLast(keyMapper.apply(b), valueMapper.apply(b))> for each byte b""")
    @Test
    @SuppressWarnings({"unchecked"})
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = set_array_sets_hello_world_bytes(service());
        final var map = mock(SequencedMap.class);
        final var keyMapper = mock(Function.class);
        when(keyMapper.apply(any())).thenAnswer(i -> (byte) ((Byte) i.getArgument(0) + 1));
        final var valueMapper = mock(Function.class);
        when(valueMapper.apply(any())).thenAnswer(i -> (byte) ((Byte) i.getArgument(0) + 2));
        // ------------------------------------------------------------------------------------ when
        final var result = service.put(map, keyMapper, valueMapper);
        // ------------------------------------------------------------------------------------ then
        final var array = set_array12_invoked_once(service);
        final var inOrder = inOrder(map);
        for (final var b : array) {
            inOrder.verify(map, calls(1)).putLast((byte) (b + 1), (byte) (b + 2));
        }
        assertSame(map, result);
    }
}
