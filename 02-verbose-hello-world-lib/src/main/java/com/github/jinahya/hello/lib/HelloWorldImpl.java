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

import com.github.jinahya.hello.api.*;

import java.util.*;

/**
 * A {@link HelloWorld} whose {@link #set(byte[], int) set(array, index)} writes the
 * {@value HelloWorld#BYTES} {@code US-ASCII} bytes of {@code "hello, world"} into {@code array}
 * starting at {@code index}, byte-by-byte via direct array assignments.
 *
 * <p>Validates its arguments before writing: throws {@link NullPointerException} when
 * {@code array} is {@code null}, and {@link IndexOutOfBoundsException} when {@code index} is
 * negative or when {@code array.length < index + }{@value HelloWorld#BYTES}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public class HelloWorldImpl implements HelloWorld {

    /**
     * Creates a new instance.
     */
    public HelloWorldImpl() {
        super();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '@' + String.format("%08x", hashCode());
    }

    @Override
    public byte[] set(final byte[] array, final int index) {
        Objects.requireNonNull(array, "array is null");
        if (index < 0) {
            throw new IndexOutOfBoundsException("index(" + index + ") is negative");
        }
        if (array.length < index + BYTES) {
            throw new IndexOutOfBoundsException(
                    "array.length(" + array.length + ")" +
                    " < index(" + index + ") + " + HelloWorld.BYTES
            );
        }
        array[index] = 0x68;      // 'h'
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
