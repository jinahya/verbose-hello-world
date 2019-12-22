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

/**
 * A class implements the {@link HelloWorld} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public class HelloWorldImpl implements HelloWorld {

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public @NotNull byte[] set(@NotNull final byte[] array, @PositiveOrZero final int index) {
        // TODO: implement!
        return null;
    }
}
