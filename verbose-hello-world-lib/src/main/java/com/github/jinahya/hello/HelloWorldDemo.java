package com.github.jinahya.hello;

import static java.lang.System.arraycopy;
import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * A class implements the {@link HelloWorld} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class HelloWorldDemo implements HelloWorld {

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public byte[] set(byte[] array, final int index) {
        arraycopy("hello, world".getBytes(US_ASCII), 0, array, index, BYTES); // <1>
        return array;                                                         // <6>
    }
}
