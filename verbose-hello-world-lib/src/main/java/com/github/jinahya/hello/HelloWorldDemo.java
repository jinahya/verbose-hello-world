package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-lib
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

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.nio.charset.StandardCharsets;

/**
 * A class implements the {@link HelloWorld} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class HelloWorldDemo implements HelloWorld {

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public @NotNull byte[] set(@NotNull final byte[] array, @PositiveOrZero final int index) {
        final byte[] source = "hello, world".getBytes(StandardCharsets.US_ASCII); // <1>
        assert source.length == BYTES;
        System.arraycopy(
                source,       // <1>
                0,            // <2>
                array,        // <3>
                index,        // <4>
                source.length // <5>
        );
        return array; // <1>
    }
}
