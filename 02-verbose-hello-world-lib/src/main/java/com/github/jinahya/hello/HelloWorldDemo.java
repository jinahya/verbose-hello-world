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

import static java.lang.System.arraycopy;
import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * A class implements the {@link HelloWorld} interface for a demonstration
 * purpose.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public class HelloWorldDemo
        implements HelloWorld {

    @Override
    public byte[] set(final byte[] array, final int index) {
        final byte[] source = "hello, world".getBytes(US_ASCII);
        arraycopy(source, 0, array, index, source.length);
        return array;
    }
}
