package com.github.jinahya.hello.api.util;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2026 Jinahya, Inc.
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
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.LongAdder;

/**
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class JavaUtilConcurrentAtomicLongAdderTest {

    @Test
    void __() {
        final var adder = new LongAdder();
        adder.add(1L);
        adder.add(Long.MAX_VALUE);
        Assertions.assertEquals(Long.MIN_VALUE, adder.sum());
    }
}
