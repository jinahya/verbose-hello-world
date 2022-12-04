package com.github.jinahya.hello.misc;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class ArgumentCaptorTest {

    private static class Some {

        void a(int a) {
        }

        void b() {
            a(0);
        }
    }

    @Test
    void test() {
        var some = spy(new Some());
        some.b();
        var captor = ArgumentCaptor.forClass(int.class);
        verify(some, times(1)).a(captor.capture());
        var a = captor.getValue();
        assertEquals(0, a);
    }
}
