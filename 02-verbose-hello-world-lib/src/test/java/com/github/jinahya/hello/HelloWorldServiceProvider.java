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

interface HelloWorldServiceProvider {

    /**
     * Returns an instance of {@link HelloWorld} interface.
     *
     * @return an instance of {@link HelloWorld} interface.
     */
    HelloWorld getService();

    /**
     * Sets the {@code hello, world} bytes on specified array starting at specified index.
     *
     * @param array the array on which the {@code hello, world} bytes are set.
     * @param index the starting index in the {@code array}.
     * @return given {@code array}.
     * @implSpec default implementation invokes
     * {@link HelloWorld#set(byte[], int) set(array, index)} method on a service returned from
     * {@link #getService()}, with {@code array} and {@code index}, and returns the result.
     * @see HelloWorld#set(byte[], int)
     */
    default byte[] set(final byte[] array, final int index) {
        return getService().set(array, index);
    }
}
