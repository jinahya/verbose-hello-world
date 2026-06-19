/*
 * Machine epsilon per IEEE 754 type. Each line uses fixed column widths:
 *
 *   <defining header>  <constant>  <type>  <value>  <fixed|impl>
 *
 * The type column is the C type of the constant. The last column flags whether
 * the value is fixed by definition (identical on every common IEEE-754 platform)
 * or implementation-defined: float (binary32), double (binary64), and the C23
 * interchange types _Float16/32/64/128 are "fixed"; long double (80-bit /
 * binary64 / binary128 vary by platform) and the extended _Float32x/_Float64x
 * are "impl".
 *
 * The C23 _FloatN / _FloatNx rows are guarded: the standard FLTn_EPSILON macros
 * need C23 + libc support; where absent the compiler builtin __FLTn_EPSILON__ is
 * used as a fallback.
 */
#include <stdio.h>
#include <stdarg.h>
#include <float.h>

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

int main(void) {
    row("<float.h>", "FLT_EPSILON", "float", "fixed", "%+.20e", (double) FLT_EPSILON);
    row("<float.h>", "DBL_EPSILON", "double", "fixed", "%+.20e", DBL_EPSILON);
    row("<float.h>", "LDBL_EPSILON", "long double", "impl", "%+.20Le", LDBL_EPSILON);

    /* C23 _FloatN / _FloatNx (binary16/32/64/128 + extended) */
#if defined(FLT16_EPSILON)
    row("<float.h>", "FLT16_EPSILON", "_Float16", "fixed", "%+.20e", (double) FLT16_EPSILON);
#elif defined(__FLT16_EPSILON__)
    row("<float.h>", "FLT16_EPSILON", "_Float16", "fixed", "%+.20e", (double) __FLT16_EPSILON__);
#endif
#if defined(FLT32_EPSILON)
    row("<float.h>", "FLT32_EPSILON", "_Float32", "fixed", "%+.20e", (double) FLT32_EPSILON);
#elif defined(__FLT32_EPSILON__)
    row("<float.h>", "FLT32_EPSILON", "_Float32", "fixed", "%+.20e", (double) __FLT32_EPSILON__);
#endif
#if defined(FLT64_EPSILON)
    row("<float.h>", "FLT64_EPSILON", "_Float64", "fixed", "%+.20e", (double) FLT64_EPSILON);
#elif defined(__FLT64_EPSILON__)
    row("<float.h>", "FLT64_EPSILON", "_Float64", "fixed", "%+.20e", (double) __FLT64_EPSILON__);
#endif
#if defined(FLT128_EPSILON)
    row("<float.h>", "FLT128_EPSILON", "_Float128", "fixed", "%+.20e", (double) FLT128_EPSILON);
#elif defined(__FLT128_EPSILON__)
    row("<float.h>", "FLT128_EPSILON", "_Float128", "fixed", "%+.20e", (double) __FLT128_EPSILON__);
#endif
#if defined(FLT32X_EPSILON)
    row("<float.h>", "FLT32X_EPSILON", "_Float32x", "impl", "%+.20e", (double) FLT32X_EPSILON);
#elif defined(__FLT32X_EPSILON__)
    row("<float.h>", "FLT32X_EPSILON", "_Float32x", "impl", "%+.20e", (double) __FLT32X_EPSILON__);
#endif
#if defined(FLT64X_EPSILON)
    row("<float.h>", "FLT64X_EPSILON", "_Float64x", "impl", "%+.20e", (double) FLT64X_EPSILON);
#elif defined(__FLT64X_EPSILON__)
    row("<float.h>", "FLT64X_EPSILON", "_Float64x", "impl", "%+.20e", (double) __FLT64X_EPSILON__);
#endif

    return 0;
}
