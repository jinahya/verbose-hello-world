package com.github.jinahya.hello.lib;

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

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.lib.util.JavaLangObjectUtils;

import java.util.Objects;

/**
 * A class implements the {@link HelloWorld} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public class HelloWorldImpl implements HelloWorld {

    @Override
    public String toString() {
        return JavaLangObjectUtils.toSimpleString(this);
    }

    @Override
    public byte[] set(final byte[] array, final int index) {
        Objects.requireNonNull(array, "array is null");
        if (index < 0) {
            throw new IndexOutOfBoundsException("index(" + index + ") is negative");
        }
        if (index + BYTES > array.length) {
            throw new IndexOutOfBoundsException(
                    "index(" + index + ") + " + HelloWorld.BYTES +
                    " > array.length(" + array.length + ")"
            );
        }
        array[index] = 0x68; // 'h'
        array[index + 0b1] = 'e'; // ?
        array[index + 0x2] = 'l';
        array[index + 0x3] = 'l';
        array[index + 0x4] = 'o';
        array[index + 0x5] = ',';
        array[index + 0x6] = ' ';
        array[index + 007] = 'w'; // ?
        array[index + 0x8] = 'o';
        array[index + 0x9] = 'r';
        array[index + 012] = 'l'; // ?
        array[index + 0xb] = 'd';
        return array;
    }
}
