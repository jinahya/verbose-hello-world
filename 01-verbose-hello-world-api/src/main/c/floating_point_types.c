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
// floating_point_types.c
#include <float.h>
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
    printf("%-26s: ", name);
    print_float_bits(value);
    printf(" (%+e)\n", value);
}

static void printf_d(const char *name, const double value) {
    printf("%-26s: ", name);
    print_double_bits(value);
    printf(" (%+e)\n", value);
}

static void printf_ld(const char *name, const long double value) {
    printf("%-26s: %+Le\n", name, value);
}

static void printf_i(const char *name, const int value) {
    printf("%-26s: %d\n", name, value);
}

// -------------------------------------------------------------------------

static void value_range_constants(void) {
    printf("\n--- 1. Value Range Constants ---\n");
    {
        printf_f("FLT_MAX",      +FLT_MAX);       /* Float.MAX_VALUE */
        printf_f("FLT_MIN",      +FLT_MIN);       /* Float.MIN_NORMAL */
        /* FLT_TRUE_MIN (C11): smallest positive subnormal float */
        const uint32_t ftm_bits = 0x00000001U;
        float ftm; memcpy(&ftm, &ftm_bits, sizeof ftm);
        printf_f("FLT_TRUE_MIN", ftm);             /* Float.MIN_VALUE */
    }
    {
        printf_d("DBL_MAX",      +DBL_MAX);        /* Double.MAX_VALUE */
        printf_d("DBL_MIN",      +DBL_MIN);        /* Double.MIN_NORMAL */
        /* DBL_TRUE_MIN (C11): smallest positive subnormal double */
        const uint64_t dtm_bits = 0x0000000000000001UL;
        double dtm; memcpy(&dtm, &dtm_bits, sizeof dtm);
        printf_d("DBL_TRUE_MIN", dtm);             /* Double.MIN_VALUE */
    }
    {
        printf_ld("LDBL_MAX", +LDBL_MAX);
        printf_ld("LDBL_MIN", +LDBL_MIN);
    }
}

static void precision_digit_constants(void) {
    printf("\n--- 2. Precision & Digit Constants ---\n");
    printf_i("FLT_DIG",       FLT_DIG);
    printf_i("DBL_DIG",       DBL_DIG);
    printf_i("LDBL_DIG",      LDBL_DIG);
    printf_i("FLT_MANT_DIG",  FLT_MANT_DIG);
    printf_i("DBL_MANT_DIG",  DBL_MANT_DIG);
    printf_i("LDBL_MANT_DIG", LDBL_MANT_DIG);
#ifdef FLT_DECIMAL_DIG
    printf_i("FLT_DECIMAL_DIG", FLT_DECIMAL_DIG);  /* C11 */
#endif
#ifdef DECIMAL_DIG
    printf_i("DECIMAL_DIG",   DECIMAL_DIG);         /* C11 */
#endif
}

static void exponent_range_constants(void) {
    printf("\n--- 3. Exponent Range Constants ---\n");
    printf_i("FLT_MIN_EXP",    FLT_MIN_EXP);       /* Float.MIN_EXPONENT + 1 */
    printf_i("FLT_MAX_EXP",    FLT_MAX_EXP);        /* Float.MAX_EXPONENT + 1 */
    printf_i("DBL_MIN_EXP",    DBL_MIN_EXP);        /* Double.MIN_EXPONENT + 1 */
    printf_i("DBL_MAX_EXP",    DBL_MAX_EXP);         /* Double.MAX_EXPONENT + 1 */
    printf_i("FLT_MIN_10_EXP", FLT_MIN_10_EXP);
    printf_i("FLT_MAX_10_EXP", FLT_MAX_10_EXP);
    printf_i("DBL_MIN_10_EXP", DBL_MIN_10_EXP);
    printf_i("DBL_MAX_10_EXP", DBL_MAX_10_EXP);
}

static void error_behavior_constants(void) {
    printf("\n--- 4. Error & Behavior Constants ---\n");
    printf_f("FLT_EPSILON",      FLT_EPSILON);
    printf_d("DBL_EPSILON",     DBL_EPSILON);
    printf_ld("LDBL_EPSILON",   LDBL_EPSILON);
    printf_i("FLT_RADIX",       FLT_RADIX);
    printf_i("FLT_ROUNDS",      FLT_ROUNDS);
    printf_i("FLT_EVAL_METHOD", FLT_EVAL_METHOD);
}

// -------------------------------------------------------------------------

int main(void) {
    printf("--- 0. IEEE 754 Conformance ---\n");
#ifdef __STDC_IEC_559__
    printf("__STDC_IEC_559__          : defined (IEEE 754 conformant)\n");
#else
    printf("__STDC_IEC_559__          : not defined\n");
#endif
#ifdef __STDC_IEC_60559_BFP__
    printf("__STDC_IEC_60559_BFP__    : defined (IEEE 754 conformant, C23)\n");
#else
    printf("__STDC_IEC_60559_BFP__    : not defined\n");
#endif
    printf_i("FLT_RADIX",               FLT_RADIX);
    printf_i("sizeof(float)",            (int) sizeof(float));
    printf_i("sizeof(double)",           (int) sizeof(double));
    printf_i("sizeof(long double)",      (int) sizeof(long double));
#ifdef __STDC_VERSION__
    printf_i("__STDC_VERSION__",        (int) __STDC_VERSION__);
#endif
#ifdef __STDC_IEC_60559_TYPES__
    printf("__STDC_IEC_60559_TYPES__  : defined\n");
#ifdef FLT32_MAX
    printf_i("sizeof(_Float32)",        (int) sizeof(_Float32));
    printf_f("FLT32_MAX",              (float) FLT32_MAX);
    printf_f("FLT32_MIN",              (float) FLT32_MIN);
    printf_f("FLT32_EPSILON",          (float) FLT32_EPSILON);
#endif
#ifdef FLT64_MAX
    printf_i("sizeof(_Float64)",        (int) sizeof(_Float64));
    printf_d("FLT64_MAX",              (double) FLT64_MAX);
    printf_d("FLT64_MIN",              (double) FLT64_MIN);
    printf_d("FLT64_EPSILON",          (double) FLT64_EPSILON);
#endif
#else
    printf("__STDC_IEC_60559_TYPES__  : not defined (C23 _Float32/_Float64 unavailable)\n");
#endif

    value_range_constants();
    precision_digit_constants();
    exponent_range_constants();
    error_behavior_constants();
    return 0;
}
