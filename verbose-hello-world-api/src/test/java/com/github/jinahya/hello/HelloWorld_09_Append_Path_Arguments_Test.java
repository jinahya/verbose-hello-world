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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

/**
 * A class for testing {@link HelloWorld#append(Path)} method regarding arguments verification.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_09_Append_Path_Test
 */
@Slf4j
class HelloWorld_09_Append_Path_Arguments_Test
        extends HelloWorldTest {

    /**
     * Asserts {@link HelloWorld#append(Path) append(path)} method throws a {@link NullPointerException} when the {@code
     * path} argument is {@code null}.
     */
    @DisplayName("append((Path) null) throws NullPointerException")
    @Test
    void append_ThrowNullPointerException_PathIsNull() {
        final Path path = null;
        Assertions.assertThrows(NullPointerException.class, () -> helloWorld().append(path));
    }
}
