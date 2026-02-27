// floating_points.c
#include <float.h>
#include <math.h>
#include <stdint.h>
#include <stdio.h>
#include <string.h>

// -------------------------------------------------------------------------

static void print_bits_u32(const uint32_t bits, const int hi, const int lo) {
    for (int b = hi; b >= lo; b--) {
        putchar('0' + ((bits >> b) & 1));
    }
}

static void print_float_bits(const float f) {
    uint32_t bits;
    memcpy(&bits, &f, sizeof bits);
    putchar('0' + ((bits >> 31) & 1));
    putchar('_');
    print_bits_u32(bits, 30, 23);
    putchar('_');
    print_bits_u32(bits, 22, 0);
}

static void print_bits_u64(const uint64_t bits, const int hi, const int lo) {
    for (int b = hi; b >= lo; b--) {
        putchar('0' + (int) ((bits >> b) & 1));
    }
}

static void print_double_bits(const double d) {
    uint64_t bits;
    memcpy(&bits, &d, sizeof bits);
    putchar('0' + (int) ((bits >> 63) & 1));
    putchar('_');
    print_bits_u64(bits, 62, 52);
    putchar('_');
    print_bits_u64(bits, 51, 0);
}

static void printf_f(const char *name, const float value) {
    printf("%-44s: ", name);
    print_float_bits(value);
    printf(" (%+e)\n", value);
}

static void printf_d(const char *name, const double value) {
    printf("%-44s: ", name);
    print_double_bits(value);
    printf(" (%+e)\n", value);
}

// -------------------------------------------------------------------------

static void zeros(void) {
    printf("\n--- Zeros ---\n");
    {
        printf_f("+0.0f", +0.0f);
        printf_f("-0.0f", -0.0f);
    }
    {
        printf_d("+0.0", +0.0);
        printf_d("-0.0", -0.0);
    }
}

static void normal_numbers(void) {
    printf("\n--- Normal Numbers ---\n");
    {
        // smallest normal: MIN_EXPONENT = -126
        printf_f("+FLT_MIN", +FLT_MIN);
        printf_f("-FLT_MIN", -FLT_MIN);
        // one: biased exponent = 127, fraction = 0
        printf_f("+1.0f", +1.0f);
        printf_f("-1.0f", -1.0f);
        // FLT_EPSILON = ulp(1.0f) = 2^-23
        printf_f("FLT_EPSILON", FLT_EPSILON);
        // largest normal: MAX_EXPONENT = 127
        printf_f("+FLT_MAX", +FLT_MAX);
        printf_f("-FLT_MAX", -FLT_MAX);
    }
    {
        // smallest normal: MIN_EXPONENT = -1022
        printf_d("+DBL_MIN", +DBL_MIN);
        printf_d("-DBL_MIN", -DBL_MIN);
        // one: biased exponent = 1023, fraction = 0
        printf_d("+1.0", +1.0);
        printf_d("-1.0", -1.0);
        // DBL_EPSILON = ulp(1.0) = 2^-52
        printf_d("DBL_EPSILON", DBL_EPSILON);
        // largest normal: MAX_EXPONENT = 1023
        printf_d("+DBL_MAX", +DBL_MAX);
        printf_d("-DBL_MAX", -DBL_MAX);
    }
}

static void subnormal_numbers(void) {
    printf("\n--- Subnormal Numbers ---\n");
    {
        // smallest subnormal: exponent=00000000, fraction=000...001
        printf_f("+FLT_TRUE_MIN", +FLT_TRUE_MIN);
        printf_f("-FLT_TRUE_MIN", -FLT_TRUE_MIN);
        // largest subnormal: exponent=00000000, fraction=111...111
        const uint32_t fls_bits = 0x007FFFFF;
        float fls; memcpy(&fls, &fls_bits, sizeof fls);
        printf_f("+0x007FFFFF", +fls);
        printf_f("-0x007FFFFF", -fls);
    }
    {
        // smallest subnormal: exponent=00000000000, fraction=000...001
        printf_d("+DBL_TRUE_MIN", +DBL_TRUE_MIN);
        printf_d("-DBL_TRUE_MIN", -DBL_TRUE_MIN);
        // largest subnormal: exponent=00000000000, fraction=111...111
        const uint64_t dls_bits = 0x000FFFFFFFFFFFFFUL;
        double dls; memcpy(&dls, &dls_bits, sizeof dls);
        printf_d("+0x000FFFFFFFFFFFFFUL", +dls);
        printf_d("-0x000FFFFFFFFFFFFFUL", -dls);
    }
}

static void infinities(void) {
    printf("\n--- Infinities ---\n");
    {
        printf_f("+INFINITY (float)", (float) +INFINITY);
        printf_f("-INFINITY (float)", (float) -INFINITY);
    }
    {
        printf_d("+INFINITY", +INFINITY);
        printf_d("-INFINITY", -INFINITY);
    }
}

static void qNaNs(void) {
    printf("\n--- Quiet NaNs (qNaN) ---\n");
    {
        printf_f("NAN (float)", (float) NAN);
        const uint32_t qnan_f0_bits = 0x7FC00000; // sign=0, fraction MSB=1
        const uint32_t qnan_f1_bits = 0xFFC00000; // sign=1, fraction MSB=1
        float qnan_f0; memcpy(&qnan_f0, &qnan_f0_bits, sizeof qnan_f0);
        float qnan_f1; memcpy(&qnan_f1, &qnan_f1_bits, sizeof qnan_f1);
        printf_f("float qNaN (sign=0) 0x7FC00000", qnan_f0);
        printf_f("float qNaN (sign=1) 0xFFC00000", qnan_f1);
    }
    {
        printf_d("NAN", NAN);
        const uint64_t qnan_d0_bits = 0x7FF8000000000000UL; // sign=0, fraction MSB=1
        const uint64_t qnan_d1_bits = 0xFFF8000000000000UL; // sign=1, fraction MSB=1
        double qnan_d0; memcpy(&qnan_d0, &qnan_d0_bits, sizeof qnan_d0);
        double qnan_d1; memcpy(&qnan_d1, &qnan_d1_bits, sizeof qnan_d1);
        printf_d("double qNaN (sign=0) 0x7FF8000000000000", qnan_d0);
        printf_d("double qNaN (sign=1) 0xFFF8000000000000", qnan_d1);
    }
}

static void sNaNs(void) {
    printf("\n--- Signaling NaNs (sNaN) ---\n");
    {
        const uint32_t snan_f0_bits = 0x7F800001; // fraction=000...001 (bit 0)
        const uint32_t snan_f1_bits = 0x7F800002; // fraction=000...010 (bit 1)
        const uint32_t snan_f2_bits = 0x7FA00000; // fraction=010...000 (bit 21)
        const uint32_t snan_f3_bits = 0x7FBFFFFF; // fraction=011...111 (all but MSB)
        float snan_f0; memcpy(&snan_f0, &snan_f0_bits, sizeof snan_f0);
        float snan_f1; memcpy(&snan_f1, &snan_f1_bits, sizeof snan_f1);
        float snan_f2; memcpy(&snan_f2, &snan_f2_bits, sizeof snan_f2);
        float snan_f3; memcpy(&snan_f3, &snan_f3_bits, sizeof snan_f3);
        printf_f("float sNaN 0x7F800001", snan_f0);
        printf_f("float sNaN 0x7F800002", snan_f1);
        printf_f("float sNaN 0x7FA00000", snan_f2);
        printf_f("float sNaN 0x7FBFFFFF", snan_f3);
    }
    {
        const uint64_t snan_d0_bits = 0x7FF0000000000001UL; // fraction=000...001 (bit 0)
        const uint64_t snan_d1_bits = 0x7FF0000000000002UL; // fraction=000...010 (bit 1)
        const uint64_t snan_d2_bits = 0x7FF4000000000000UL; // fraction=010...000 (bit 50)
        const uint64_t snan_d3_bits = 0x7FF7FFFFFFFFFFFFUL; // fraction=011...111 (all but MSB)
        double snan_d0; memcpy(&snan_d0, &snan_d0_bits, sizeof snan_d0);
        double snan_d1; memcpy(&snan_d1, &snan_d1_bits, sizeof snan_d1);
        double snan_d2; memcpy(&snan_d2, &snan_d2_bits, sizeof snan_d2);
        double snan_d3; memcpy(&snan_d3, &snan_d3_bits, sizeof snan_d3);
        printf_d("double sNaN 0x7FF0000000000001", snan_d0);
        printf_d("double sNaN 0x7FF0000000000002", snan_d1);
        printf_d("double sNaN 0x7FF4000000000000", snan_d2);
        printf_d("double sNaN 0x7FF7FFFFFFFFFFFF", snan_d3);
    }
}

// -------------------------------------------------------------------------

int main(void) {
    zeros();
    normal_numbers();
    subnormal_numbers();
    infinities();
    qNaNs();
    sNaNs();
    return 0;
}
