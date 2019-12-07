package com.github.jinahya.hello;

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
        @SuppressWarnings({"unchecked"}) final T result = (T) invocationOnMock.callRealMethod();
        return (this.result = result);
    }

    public T getResult() {
        return result;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private T result;
}
