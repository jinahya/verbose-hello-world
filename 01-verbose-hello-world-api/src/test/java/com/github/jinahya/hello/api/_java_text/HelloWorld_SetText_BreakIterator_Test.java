package com.github.jinahya.hello.api._java_text;

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

import java.text.*;

/**
 * A class for testing
 * {@link com.github.jinahya.hello.api.HelloWorld#setText(BreakIterator) setText(iterator)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see BreakIterator#setText(String)
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/text/BreakIterator.html">java.text.BreakIterator</a>
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_SetText_BreakIterator_Test extends HelloWorldTest {

    @DisplayName("(null)NullPointerException")
    @Test
    void _ThrowNullPointerException_IteratorIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final BreakIterator iterator = null;
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.setText(iterator)
        );
    }

    @DisplayName("iterator.setText(<string from set(byte[12])>)")
    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorldTestUtils.set_array_sets_actual_hello_world_bytes(service());
        final var iterator = Mockito.mock(BreakIterator.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.setText(iterator);
        // ------------------------------------------------------------------------------------ then
        HelloWorldTestUtils.set_array12_invoked_once(service);
//        Mockito.verify(iterator, Mockito.times(1))
//                .setText(HelloWorldTestUtils.hello_world_string());
        Assertions.assertSame(iterator, result);
    }
}
