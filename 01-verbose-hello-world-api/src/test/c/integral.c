/*
 * Prints all defined integral-type limit constants from <limits.h> and
 * <stdint.h>. Each line uses fixed column widths:
 *
 *   <defining header>  <constant>  <type>  <value>  <fixed|impl>
 *
 * The type column is the C type the constant describes. The last column flags
 * whether the value is mandated by the C standard (fixed) or implementation-
 * defined (impl). Only the exact-width types (INTn / UINTn -- two's-complement,
 * exactly N bits) and bool are fixed; the classic / least / fast / pointer / size
 * / wchar limits are all implementation-defined.
 *
 * Signed values are printed via intmax_t (%+jd), unsigned via uintmax_t (%ju),
 * widths as int (%+d). Optional macros (exact-width <stdint.h> types, C23 *_WIDTH
 * / *_MAX additions) are #ifdef-guarded.
 *
 * Build: see the repository Makefile (`make c`) or simply:
 *     cc -Wall -Wextra -O2 integral.c -o integral -lm
 */
#include <stdio.h>
#include <stdarg.h>
#include <limits.h>
#include <stdint.h>

/* one aligned row: <header> <name> <type> <value> <flag> (value right-aligned) */
static void row(const char *hdr, const char *name, const char *type,
                const char *flag, const char *fmt, ...) {
    char buf[64];
    va_list ap;
    va_start(ap, fmt);
    vsnprintf(buf, sizeof buf, fmt, ap);
    va_end(ap);
    printf("%-10s %-18s %-18s %28s %s\n", hdr, name, type, buf, flag);
}

/* stringize the macro NAME, carry the described C type, print value, then flag */
#define S(h, m, t, f) row(h, #m, t, f, "%+jd", (intmax_t) (m))
#define U(h, m, t, f) row(h, #m, t, f, "%ju", (uintmax_t) (m))
#define D(h, m, t, f) row(h, #m, t, f, "%+d", (int) (m))

int main(void) {
    /* ---------------------------------------------------------------- <limits.h> */
    D("<limits.h>", CHAR_BIT, "char", "impl");
    D("<limits.h>", MB_LEN_MAX, "int", "impl");
    S("<limits.h>", SCHAR_MIN, "signed char", "impl");
    S("<limits.h>", SCHAR_MAX, "signed char", "impl");
    U("<limits.h>", UCHAR_MAX, "unsigned char", "impl");
    S("<limits.h>", CHAR_MIN, "char", "impl");
    S("<limits.h>", CHAR_MAX, "char", "impl");
    S("<limits.h>", SHRT_MIN, "short", "impl");
    S("<limits.h>", SHRT_MAX, "short", "impl");
    U("<limits.h>", USHRT_MAX, "unsigned short", "impl");
    S("<limits.h>", INT_MIN, "int", "impl");
    S("<limits.h>", INT_MAX, "int", "impl");
    U("<limits.h>", UINT_MAX, "unsigned int", "impl");
    S("<limits.h>", LONG_MIN, "long", "impl");
    S("<limits.h>", LONG_MAX, "long", "impl");
    U("<limits.h>", ULONG_MAX, "unsigned long", "impl");
    S("<limits.h>", LLONG_MIN, "long long", "impl");
    S("<limits.h>", LLONG_MAX, "long long", "impl");
    U("<limits.h>", ULLONG_MAX, "unsigned long long", "impl");
#ifdef BOOL_MAX                 /* C23 */
    S("<limits.h>", BOOL_MAX, "bool", "fixed");
#endif
#ifdef CHAR_WIDTH              /* C23 *_WIDTH */
    D("<limits.h>", BOOL_WIDTH, "bool", "fixed");
    D("<limits.h>", CHAR_WIDTH, "char", "impl");
    D("<limits.h>", SCHAR_WIDTH, "signed char", "impl");
    D("<limits.h>", UCHAR_WIDTH, "unsigned char", "impl");
    D("<limits.h>", SHRT_WIDTH, "short", "impl");
    D("<limits.h>", USHRT_WIDTH, "unsigned short", "impl");
    D("<limits.h>", INT_WIDTH, "int", "impl");
    D("<limits.h>", UINT_WIDTH, "unsigned int", "impl");
    D("<limits.h>", LONG_WIDTH, "long", "impl");
    D("<limits.h>", ULONG_WIDTH, "unsigned long", "impl");
    D("<limits.h>", LLONG_WIDTH, "long long", "impl");
    D("<limits.h>", ULLONG_WIDTH, "unsigned long long", "impl");
#endif
#ifdef BITINT_MAXWIDTH        /* C23 _BitInt */
    D("<limits.h>", BITINT_MAXWIDTH, "int", "impl");
#endif

    /* ---------------------------------------------------------------- <stdint.h> */
    /* exact-width (optional, but fixed-by-definition where present) */
#ifdef INT8_MAX
    S("<stdint.h>", INT8_MIN, "int8_t", "fixed");
    S("<stdint.h>", INT8_MAX, "int8_t", "fixed");
    U("<stdint.h>", UINT8_MAX, "uint8_t", "fixed");
#endif
#ifdef INT16_MAX
    S("<stdint.h>", INT16_MIN, "int16_t", "fixed");
    S("<stdint.h>", INT16_MAX, "int16_t", "fixed");
    U("<stdint.h>", UINT16_MAX, "uint16_t", "fixed");
#endif
#ifdef INT32_MAX
    S("<stdint.h>", INT32_MIN, "int32_t", "fixed");
    S("<stdint.h>", INT32_MAX, "int32_t", "fixed");
    U("<stdint.h>", UINT32_MAX, "uint32_t", "fixed");
#endif
#ifdef INT64_MAX
    S("<stdint.h>", INT64_MIN, "int64_t", "fixed");
    S("<stdint.h>", INT64_MAX, "int64_t", "fixed");
    U("<stdint.h>", UINT64_MAX, "uint64_t", "fixed");
#endif

    /* minimum-width (mandatory, size impl-defined) */
    S("<stdint.h>", INT_LEAST8_MIN, "int_least8_t", "impl");
    S("<stdint.h>", INT_LEAST8_MAX, "int_least8_t", "impl");
    U("<stdint.h>", UINT_LEAST8_MAX, "uint_least8_t", "impl");
    S("<stdint.h>", INT_LEAST16_MIN, "int_least16_t", "impl");
    S("<stdint.h>", INT_LEAST16_MAX, "int_least16_t", "impl");
    U("<stdint.h>", UINT_LEAST16_MAX, "uint_least16_t", "impl");
    S("<stdint.h>", INT_LEAST32_MIN, "int_least32_t", "impl");
    S("<stdint.h>", INT_LEAST32_MAX, "int_least32_t", "impl");
    U("<stdint.h>", UINT_LEAST32_MAX, "uint_least32_t", "impl");
    S("<stdint.h>", INT_LEAST64_MIN, "int_least64_t", "impl");
    S("<stdint.h>", INT_LEAST64_MAX, "int_least64_t", "impl");
    U("<stdint.h>", UINT_LEAST64_MAX, "uint_least64_t", "impl");

    /* fastest minimum-width (mandatory, size impl-defined) */
    S("<stdint.h>", INT_FAST8_MIN, "int_fast8_t", "impl");
    S("<stdint.h>", INT_FAST8_MAX, "int_fast8_t", "impl");
    U("<stdint.h>", UINT_FAST8_MAX, "uint_fast8_t", "impl");
    S("<stdint.h>", INT_FAST16_MIN, "int_fast16_t", "impl");
    S("<stdint.h>", INT_FAST16_MAX, "int_fast16_t", "impl");
    U("<stdint.h>", UINT_FAST16_MAX, "uint_fast16_t", "impl");
    S("<stdint.h>", INT_FAST32_MIN, "int_fast32_t", "impl");
    S("<stdint.h>", INT_FAST32_MAX, "int_fast32_t", "impl");
    U("<stdint.h>", UINT_FAST32_MAX, "uint_fast32_t", "impl");
    S("<stdint.h>", INT_FAST64_MIN, "int_fast64_t", "impl");
    S("<stdint.h>", INT_FAST64_MAX, "int_fast64_t", "impl");
    U("<stdint.h>", UINT_FAST64_MAX, "uint_fast64_t", "impl");

    /* greatest-width (size impl-defined) */
    S("<stdint.h>", INTMAX_MIN, "intmax_t", "impl");
    S("<stdint.h>", INTMAX_MAX, "intmax_t", "impl");
    U("<stdint.h>", UINTMAX_MAX, "uintmax_t", "impl");

    /* pointer-holding (optional, size impl-defined) */
#ifdef INTPTR_MAX
    S("<stdint.h>", INTPTR_MIN, "intptr_t", "impl");
    S("<stdint.h>", INTPTR_MAX, "intptr_t", "impl");
    U("<stdint.h>", UINTPTR_MAX, "uintptr_t", "impl");
#endif

    /* other standard integer types (size impl-defined) */
    S("<stdint.h>", PTRDIFF_MIN, "ptrdiff_t", "impl");
    S("<stdint.h>", PTRDIFF_MAX, "ptrdiff_t", "impl");
    U("<stdint.h>", SIZE_MAX, "size_t", "impl");
    S("<stdint.h>", SIG_ATOMIC_MIN, "sig_atomic_t", "impl");
    S("<stdint.h>", SIG_ATOMIC_MAX, "sig_atomic_t", "impl");
    S("<stdint.h>", WCHAR_MIN, "wchar_t", "impl");
    S("<stdint.h>", WCHAR_MAX, "wchar_t", "impl");
    S("<stdint.h>", WINT_MIN, "wint_t", "impl");
    S("<stdint.h>", WINT_MAX, "wint_t", "impl");

    /* C23 *_WIDTH additions (size impl-defined) */
#ifdef INTMAX_WIDTH
    D("<stdint.h>", INTMAX_WIDTH, "intmax_t", "impl");
    D("<stdint.h>", UINTMAX_WIDTH, "uintmax_t", "impl");
    D("<stdint.h>", PTRDIFF_WIDTH, "ptrdiff_t", "impl");
    D("<stdint.h>", SIZE_WIDTH, "size_t", "impl");
    D("<stdint.h>", SIG_ATOMIC_WIDTH, "sig_atomic_t", "impl");
    D("<stdint.h>", WCHAR_WIDTH, "wchar_t", "impl");
    D("<stdint.h>", WINT_WIDTH, "wint_t", "impl");
#endif

    return 0;
}
