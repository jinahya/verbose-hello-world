package com.github.jinahya.hello;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
class ResultCaptor<T>
        implements Answer<T> {

    // ---------------------------------------------------------------------------------------------
    @Override
    @SuppressWarnings({"unchecked"})
    public T answer(final InvocationOnMock invocation) throws Throwable {
        result = (T) invocation.callRealMethod();
        return result;
    }

    // ---------------------------------------------------------------------------------------------
    public T getResult() {
        return result;
    }

    // ---------------------------------------------------------------------------------------------
    private T result;
}
