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

import java.util.ArrayList;
import java.util.SequencedCollection;
import java.util.function.Function;

/**
 * A class for testing {@link HelloWorld#add(SequencedCollection, Function) add(collection, mapper)}
 * method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("add(SequencedCollection, Function)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Add_Collection_Function_Test
        extends HelloWorldTest {

    @DisplayName("""
            should throw a <NullPointerException>
            when the <collection> argument is <null>""")
    @Test
    void _ThrowNullPointerException_CollectionIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var collection = (SequencedCollection<Byte>) null;
        final Function<? super Byte, ? extends Byte> mapper = b -> b;
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.add(collection, mapper)
        );
    }

    @DisplayName("""
            should throw a <NullPointerException>
            when the <mapper> argument is <null>""")
    @Test
    void _ThrowNullPointerException_MapperIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var collection = new ArrayList<Byte>();
        final Function<? super Byte, ? extends Byte> mapper = null;
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.add(collection, mapper)
        );
    }

    @DisplayName("should invoke <set(byte[])>, and <collection.add(mapper.apply(b))> for each byte")
    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorldTestUtils.set_array_sets_random_bytes(service());
        @SuppressWarnings("unchecked")
        final var collection = (SequencedCollection<Byte>) Mockito.mock(SequencedCollection.class);
        @SuppressWarnings("unchecked")
        final Function<Byte, Byte> mapper = Mockito.mock(Function.class);
        Mockito.when(mapper.apply(Mockito.any())).thenAnswer(i -> i.getArgument(0));
        // ------------------------------------------------------------------------------------ when
        final var result = service.add(collection, mapper);
        // ------------------------------------------------------------------------------------ then
        final var array = HelloWorldTestUtils.set_array12_invoked_once(service);
        final var inOrder = Mockito.inOrder(mapper, collection);
        for (final var b : array) {
            inOrder.verify(mapper, Mockito.calls(1)).apply(b);
            inOrder.verify(collection, Mockito.calls(1)).add(b);
        }
        inOrder.verifyNoMoreInteractions();
        Assertions.assertSame(collection, result);
    }
}
