package com.github.jinahya.hello.api;

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

import com.github.jinahya.hello.api.util.*;
import lombok.extern.slf4j.*;
import org.mockito.*;

import java.util.*;

/**
 * Utilities for Mockito.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
@SuppressWarnings({
        "java:S101"
})
public final class MockitoTestUtils {

    // ---------------------------------------------------------------------------------------------

    /**
     * Asserts specified object is a mock.
     *
     * @param object the object to test
     * @param <T>    object's type parameter
     * @return given {@code object}.
     */
    public static <T> T requireMock(final T object) {
        Objects.requireNonNull(object, "object is null");
        if (!Mockito.mockingDetails(object).isMock()) {
            throw new IllegalArgumentException("is not a mock: " + object);
        }
        return object;
    }

    public static <T> T requireNotMock(final T object) {
        Objects.requireNonNull(object, "object is null");
        if (Mockito.mockingDetails(object).isMock()) {
            throw new IllegalArgumentException("is a mock: " + object);
        }
        return object;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Asserts specified object is a mock.
     *
     * @param object the object to test
     * @param <T>    object's type parameter
     * @return given {@code object}.
     */
    static <T extends HelloWorld> T requireMock(final T object) {
        Objects.requireNonNull(object, "object is null");
        if (!Mockito.mockingDetails(object).isMock()) {
            throw new IllegalArgumentException("is not a mock: " + object);
        }
        return object;
    }

    static <T extends HelloWorld> T requireNotMock(final T object) {
        Objects.requireNonNull(object, "object is null");
        if (Mockito.mockingDetails(object).isMock()) {
            throw new IllegalArgumentException("is a mock: " + object);
        }
        return object;
    }

    // ---------------------------------------------------------------------------------------------
    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private MockitoTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
