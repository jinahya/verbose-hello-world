package com.github.jinahya.hello.util;

final class Gates {

    @FunctionalInterface
    interface UnaryGate {

        boolean apply(boolean v);

        default UnaryGate combine(final UnaryGate other) {
            return v -> other.apply(UnaryGate.this.apply(v));
        }

        default BinaryGate combine(final BinaryGate other) {
            return (a, b) -> other.apply(UnaryGate.this.apply(a), UnaryGate.this.apply(b));
        }
    }

    @FunctionalInterface
    interface BinaryGate {

        boolean apply(boolean a, boolean b);

        default BinaryGate combine(final UnaryGate other) {
            return (a, b) -> other.apply(BinaryGate.this.apply(a, b));
        }

        default BinaryGate combine(final BinaryGate other) {
            return (a, b) -> other.apply(BinaryGate.this.apply(a, b), BinaryGate.this.apply(a, b));
        }
    }

    interface AND extends BinaryGate {

        @Override
        default boolean apply(boolean a, boolean b) {
            return a & b;
        }
    }

    interface OR extends BinaryGate {

        @Override
        default boolean apply(boolean a, boolean b) {
            return a | b;
        }
    }

    interface XOR extends BinaryGate {

        @Override
        default boolean apply(boolean a, boolean b) {
            return a ^ b;
        }
    }

    interface NAND extends BinaryGate {

        @Override
        default boolean apply(boolean a, boolean b) {
            return !new AND() {
            }.apply(a, b);
        }
    }

    interface NAND2 extends BinaryGate {

        @Override
        default boolean apply(boolean a, boolean b) {
            return !a | !b;
        }
    }

    interface AND2 extends BinaryGate {

        @Override
        default boolean apply(boolean a, boolean b) {
            final var c = new NAND2() {
            }.apply(a, b);
            return new NAND2() {
            }.apply(c, c);
        }
    }

    private Gates() {
        throw new AssertionError("instantiation is not allowed");
    }
}
