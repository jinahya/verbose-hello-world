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

import java.text.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
class HelloWorld_SetText_BreakIterator_Test extends HelloWorld__Test {

    @DisplayName("(null)NullPointerException")
    @Test
    void _ThrowNullPointerException_IteratorIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final BreakIterator iterator = null;
        // ----------------------------------------------------------------------------- when / then
        assertThrows(NullPointerException.class, () -> service.setText(iterator));
    }

    @DisplayName("iterator.setText(<string from set(byte[12])>)")
    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = set_array_sets_hello_world_bytes(service());
        final var iterator = mock(BreakIterator.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.setText(iterator);
        // ------------------------------------------------------------------------------------ then
        HelloWorld__TestUtils.set_array12_invoked_once(service);
        verify(iterator, times(1)).setText(hello_world_string());
        assertSame(iterator, result);
    }
}
