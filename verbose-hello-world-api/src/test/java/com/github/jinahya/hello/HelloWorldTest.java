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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * An abstract class for testing methods defined in {@link HelloWorld} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@ExtendWith({MockitoExtension.class})
@TestInstance(TestInstance.Lifecycle.PER_METHOD) // default, implicitly.
@Slf4j
abstract class HelloWorldTest {

    @BeforeEach
    void beforeEach() {
        final byte[] array = ArgumentMatchers.any(byte[].class);
        final int index = ArgumentMatchers.anyInt();
        Mockito.lenient().when(helloWorld.set(array, index)) // <1>
                .thenAnswer(i -> i.getArgument(0));          // <2>
    }

    HelloWorld helloWorld() {
        return helloWorld;
    }

    @Spy
    private HelloWorld helloWorld;
}
