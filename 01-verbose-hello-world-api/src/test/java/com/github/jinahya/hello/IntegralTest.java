package com.github.jinahya.hello;

class IntegralTest {

    /**
     * Returns a string representing binary of the specified value, starting from the specified higher bit index
     * (inclusive) to the specified lower bit index (inclusive).
     *
     * @param i the int value whose bit binary is printed.
     * @param h the higher bit index (inclusive) which should be less than or equal to {@code 31} and greater than the
     *          {@code l}.
     * @param l the lower bit index (inclusive) which should be greater than or equal to {@code 0} and less than the
     *          {@code h}.
     * @return a string representing bit
     */
    static String printBits(int i, final int h, final int l) {
        assert h < Integer.SIZE;
        assert l >= 0 && l < h;
        i >>= l;
        final StringBuilder builder = new StringBuilder();
        for (int b = l; b <= h; b++, i >>= 1) {
            builder.append(i & 1);
        }
        return builder.reverse().toString();
    }

    /**
     * Returns a string representing binary of the specified value, starting from the specified higher bit index
     * (inclusive) to the specified lower bit index (inclusive).
     *
     * @param l the long value whose bit binary is printed.
     * @param h the higher bit index (inclusive) which should be less than or equal to {@code 63} and greater than the
     *          {@code lo}.
     * @param lo the lower bit index (inclusive) which should be greater than or equal to {@code 0} and less than the
     *          {@code h}.
     * @return a string representing bit
     */
    static String printBits(long l, final int h, final int lo) {
        assert h < Long.SIZE;
        assert lo >= 0 && lo < h;
        l >>= lo;
        final StringBuilder builder = new StringBuilder();
        for (int b = lo; b <= h; b++, l >>= 1) {
            builder.append(l & 1);
        }
        return builder.reverse().toString();
    }
}
