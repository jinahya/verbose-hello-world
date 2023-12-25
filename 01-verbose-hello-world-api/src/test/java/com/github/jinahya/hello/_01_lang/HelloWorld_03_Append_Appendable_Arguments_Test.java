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
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * A class for testing {@link HelloWorld#append(Appendable) append(appendable)} method regarding
 * arguments verification.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_03_Append_Appendable_Test
 */
@DisplayName("append(appendable) arguments")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({
        "java:S101"
})
class HelloWorld_03_Append_Appendable_Arguments_Test extends _HelloWorldTest {

    /**
     * Verifies {@link HelloWorld#append(Appendable) append(appendable)} method throws a
     * {@link NullPointerException} when the {@code appendable} argument is {@code null}.
     */
    @DisplayName("[appendable == null] -> NullPointerException")
    @Test
    void _ThrowNullPointerException_AppendableNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var appendable = (Appendable) null;
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.append(appendable)
        );
    }
}
