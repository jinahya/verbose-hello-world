package com.github.jinahya.hello;

/**
 * An interface for generating bytes of {@code hello, world} string.
 *
 * @author Jin Kwon &lt;jinahya_at_gmail.com&gt;
 */
public interface HelloWorld {

    /**
     * The number of bytes to represent the {@code hello, world} string in {@code US-ASCII} character set. The value is
     * {@value SIZE}.
     */
    int SIZE = 12;

    /**
     * Sets {@value SIZE} bytes of {@code hello, world} string on given array starting at specified index.
     *
     * @param array the array to which {@code hello, world} bytes are set.
     * @param index the starting index of the array.
     * @return given array.
     * @throws NullPointerException      if the {@code array} is {@code null}
     * @throws IndexOutOfBoundsException if {@code index} is negative or {@code index} + {@value SIZE} is greater than
     *                                   {@code array.length}
     */
    byte[] set(byte[] array, int index);
}
