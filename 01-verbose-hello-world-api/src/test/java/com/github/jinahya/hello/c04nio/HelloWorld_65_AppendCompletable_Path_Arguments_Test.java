package com.github.jinahya.hello.c04nio;

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
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * A class for testing {@link HelloWorld#appendCompletable(Path) appendCompletable(path)} method
 * regarding arguments verification.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_65_AppendCompletable_Path_Test
 */
@DisplayName("appendCompletable(path) arguments")
@Slf4j
class HelloWorld_65_AppendCompletable_Path_Arguments_Test extends _HelloWorldTest {

    /**
     * Asserts {@link HelloWorld#appendCompletable(Path) appendCompletable(path)} method throws a
     * {@link NullPointerException} when the {@code path} argument is {@code null}.
     */
    @DisplayName("(null)NullPointerException")
    @Test
    void _ThrowNullPointerException_PathIsNull() {
        // ----------------------------------------------------------------------------------- GIVEN
        var service = serviceInstance();
        var path = (Path) null;
        // ------------------------------------------------------------------------------- WHEN/THEN
        assertThrows(
                NullPointerException.class,
                () -> service.appendCompletable(path)
        );
    }
}
