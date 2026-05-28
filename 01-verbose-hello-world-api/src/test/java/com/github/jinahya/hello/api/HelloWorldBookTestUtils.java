package com.github.jinahya.hello.api;

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

import lombok.extern.slf4j.*;
import org.mockito.*;

/**
 * Test-scoped helpers that compose {@link HelloWorldBookUtils}' logging proxies with
 * {@link Mockito} so a single value is *both* logged (every call printed at {@code DEBUG} via the
 * JDK dynamic proxy) and verifiable (every call recorded for
 * {@link Mockito#verify(Object) Mockito.verify}, {@link Mockito#inOrder(Object...) inOrder}, and
 * {@link org.mockito.ArgumentCaptor ArgumentCaptor}).
 * <p>
 * The pattern is
 * {@code Mockito.mock(Interface.class, defaultAnswer(delegatesTo(loggingProxy(real))))}: the
 * returned object is the Mockito mock (so verifications work on it directly), and every invocation
 * is forwarded to the logging proxy, which logs and then forwards to {@code real}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
final class HelloWorldBookTestUtils {

    private HelloWorldBookTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
