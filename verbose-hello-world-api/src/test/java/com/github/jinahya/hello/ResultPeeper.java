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

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * An answer for peeping the invocation result.
 *
 * @param <T> result type parameter.
 * @see <a href="https://stackoverflow.com/a/25694262/330457">Jeff Fairley's answoer on StackOverflow</a>
 */
class ResultPeeper<T> implements Answer<T> {

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public T answer(final InvocationOnMock invocationOnMock) throws Throwable {
        @SuppressWarnings({"unchecked"})
        final T result = (T) invocationOnMock.callRealMethod();
        return (this.result = result);
    }

    // -----------------------------------------------------------------------------------------------------------------
    public T getResult() {
        return result;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private T result;
}
