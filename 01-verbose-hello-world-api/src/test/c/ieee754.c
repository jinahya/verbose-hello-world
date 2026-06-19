/*
 * Prints only those C standard-library constants that name an entity defined by
 * IEEE 754 itself -- format parameters (radix, precision, exponent range), the
 * representable extremes (min normal, max finite, min subnormal), the special
 * values and value classes, the five exceptions, and the rounding directions.
 *
 * Each line uses fixed column widths:
 *   <defining header>  <constant>  <type>  <value>  <fixed|impl>
 *
 * The type column is the C type of the constant (int / float / double / long
 * double). The last column flags whether the value is fixed by definition
 * (identical on every common IEEE-754 platform) or implementation-defined.
 *
 * Deliberately omitted (C-library / decimal-convenience / error-handling, not
 * IEEE 754): FLT_ROUNDS, FLT_EVAL_METHOD, *_DIG, *_DECIMAL_DIG, DECIMAL_DIG,
 * *_MIN_10_EXP, *_MAX_10_EXP, FP_ILOGB0, FP_ILOGBNAN, math_errhandling,
 * MATH_ERRNO, MATH_ERREXCEPT. See floating.c for the exhaustive dump.
 *
 * Build: see the repository Makefile (`make c`) or simply:
 *     cc -Wall -Wextra -O2 ieee754.c -o ieee754 -lm
 */
#include <stdio.h>
#include <stdarg.h>
#include <float.h>
#include <math.h>
#include <fenv.h>

/* one aligned row: <header> <name> <type> <value> <flag> (value right-aligned) */
static void row(const char *hdr, const char *name, const char *type,
                const char *flag, const char *fmt, ...) {
    char buf[64];
    va_list ap;
    va_start(ap, fmt);
    vsnprintf(buf, sizeof buf, fmt, ap);
    va_end(ap);
    printf("%-10s %-18s %-11s %28s %s\n", hdr, name, type, buf, flag);
}

/* stringize the macro NAME, carry its C type, print value, then the flag */
#define MI(h, m, f)   row(h, #m, "int", f, "%+d", (int) (m))
#define MFLT(h, m, f) row(h, #m, "float", f, "%+.20e", (double) (m))
#define MDBL(h, m, f) row(h, #m, "double", f, "%+.20e", (double) (m))
#define ML(h, m, f)   row(h, #m, "long double", f, "%+.20Le", (long double) (m))
#define MH(h, m, f)   row(h, #m, "int", f, "0x%x", (unsigned) (m))

int main(void) {
    /* ---------------------------------------------------- <float.h> : formats */
    MI("<float.h>", FLT_RADIX, "impl");

    /* float (binary32) */
    MI("<float.h>", FLT_MANT_DIG, "fixed");
    MI("<float.h>", FLT_MIN_EXP, "fixed");
    MI("<float.h>", FLT_MAX_EXP, "fixed");
#ifdef FLT_HAS_SUBNORM
    MI("<float.h>", FLT_HAS_SUBNORM, "fixed");
#endif
    MFLT("<float.h>", FLT_EPSILON, "fixed");
    MFLT("<float.h>", FLT_MIN, "fixed");
    MFLT("<float.h>", FLT_MAX, "fixed");
#ifdef FLT_TRUE_MIN
    MFLT("<float.h>", FLT_TRUE_MIN, "fixed");
#endif

    /* double (binary64) */
    MI("<float.h>", DBL_MANT_DIG, "fixed");
    MI("<float.h>", DBL_MIN_EXP, "fixed");
    MI("<float.h>", DBL_MAX_EXP, "fixed");
#ifdef DBL_HAS_SUBNORM
    MI("<float.h>", DBL_HAS_SUBNORM, "fixed");
#endif
    MDBL("<float.h>", DBL_EPSILON, "fixed");
    MDBL("<float.h>", DBL_MIN, "fixed");
    MDBL("<float.h>", DBL_MAX, "fixed");
#ifdef DBL_TRUE_MIN
    MDBL("<float.h>", DBL_TRUE_MIN, "fixed");
#endif

    /* long double (format varies by platform) */
    MI("<float.h>", LDBL_MANT_DIG, "impl");
    MI("<float.h>", LDBL_MIN_EXP, "impl");
    MI("<float.h>", LDBL_MAX_EXP, "impl");
#ifdef LDBL_HAS_SUBNORM
    MI("<float.h>", LDBL_HAS_SUBNORM, "impl");
#endif
    ML("<float.h>", LDBL_EPSILON, "impl");
    ML("<float.h>", LDBL_MIN, "impl");
    ML("<float.h>", LDBL_MAX, "impl");
#ifdef LDBL_TRUE_MIN
    ML("<float.h>", LDBL_TRUE_MIN, "impl");
#endif

    /* -------------------------------------- <math.h> : special values/classes */
    MFLT("<math.h>", INFINITY, "fixed");
    MDBL("<math.h>", HUGE_VAL, "fixed");
    MFLT("<math.h>", HUGE_VALF, "fixed");
    ML("<math.h>", HUGE_VALL, "fixed");
    MFLT("<math.h>", NAN, "fixed");
    MI("<math.h>", FP_NAN, "impl");
    MI("<math.h>", FP_INFINITE, "impl");
    MI("<math.h>", FP_ZERO, "impl");
    MI("<math.h>", FP_SUBNORMAL, "impl");
    MI("<math.h>", FP_NORMAL, "impl");

    /* ------------------------------ <fenv.h> : exceptions & rounding direction */
#ifdef FE_INVALID
    MH("<fenv.h>", FE_INVALID, "impl");
#endif
#ifdef FE_DIVBYZERO
    MH("<fenv.h>", FE_DIVBYZERO, "impl");
#endif
#ifdef FE_OVERFLOW
    MH("<fenv.h>", FE_OVERFLOW, "impl");
#endif
#ifdef FE_UNDERFLOW
    MH("<fenv.h>", FE_UNDERFLOW, "impl");
#endif
#ifdef FE_INEXACT
    MH("<fenv.h>", FE_INEXACT, "impl");
#endif
#ifdef FE_ALL_EXCEPT
    MH("<fenv.h>", FE_ALL_EXCEPT, "impl");
#endif
#ifdef FE_TONEAREST
    MH("<fenv.h>", FE_TONEAREST, "impl");
#endif
#ifdef FE_UPWARD
    MH("<fenv.h>", FE_UPWARD, "impl");
#endif
#ifdef FE_DOWNWARD
    MH("<fenv.h>", FE_DOWNWARD, "impl");
#endif
#ifdef FE_TOWARDZERO
    MH("<fenv.h>", FE_TOWARDZERO, "impl");
#endif

    return 0;
}
