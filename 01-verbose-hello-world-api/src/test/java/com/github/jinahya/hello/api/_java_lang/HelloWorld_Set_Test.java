package com.github.jinahya.hello.api._java_lang;

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

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

/**
 * A class for testing {@link HelloWorld#byteArray()} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("set()")
@Slf4j
@SuppressWarnings({
        "java:S1481", // unused (yet) local variables
        "java:S1854", // useless (yet) assignments
        "java:S2699"  // no assertions (yet)
})
class HelloWorld_Set_Test
        extends HelloWorldTest {

    /**
     * Verifies that the {@link HelloWorld#byteArray() set()} method invokes
     * {@link HelloWorld#set(byte[]) set(array} method with an array of {@value HelloWorld#BYTES}
     * bytes, and returns the array.
     */
    @DisplayName("""
            should invoke <set(array)> with byte[12]
            and returns the <array>"""
    )
    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        Mockito.doAnswer(i -> i.getArgument(0))
                .when(service)
                .set(ArgumentMatchers.any(byte[].class));
        // ------------------------------------------------------------------------------------ when
        final var result = service.byteArray();
        // ------------------------------------------------------------------------------------ then
        final var captured = HelloWorldTestUtils.set_array12_invoked_once(service);
        Assertions.assertSame(result, captured);
    }
}
