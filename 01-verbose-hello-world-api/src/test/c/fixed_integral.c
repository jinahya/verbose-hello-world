/*
 * Prints only the fixed-by-definition integral constants -- those whose value is
 * identical on every conforming platform: the exact-width <stdint.h> types
 * (INTn / UINTn min/max, and their C23 widths) and <limits.h>'s bool. Each line
 * uses fixed column widths:
 *
 *   <defining header>  <constant>  <type>  <value>  fixed
 *
 * The type column is the C type the constant describes. This is the subset of
 * integral.c flagged "fixed"; see integral.c for the full set, including the
 * implementation-defined classic / least / fast / pointer / size / wchar limits.
 *
 * Signed values are printed via intmax_t (%+jd), unsigned via uintmax_t (%ju),
 * widths as int (%+d). Everything here is #ifdef-guarded: exact-width types are
 * optional, and the *_WIDTH macros need C23.
 *
 * Build: see the repository Makefile (`make c`) or simply:
 *     cc -Wall -Wextra -O2 fixed_integral.c -o fixed_integral -lm
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
    printf("%-10s %-18s %-8s %28s %s\n", hdr, name, type, buf, flag);
}

/* stringize the macro NAME, carry the described C type, print value, then flag */
#define S(h, m, t, f) row(h, #m, t, f, "%+jd", (intmax_t) (m))
#define U(h, m, t, f) row(h, #m, t, f, "%ju", (uintmax_t) (m))
#define D(h, m, t, f) row(h, #m, t, f, "%+d", (int) (m))

int main(void) {
    /* ---------------------------------------------------------------- <limits.h> */
    /* bool is the one classic integer type with a fixed value range (0..1) */
#ifdef BOOL_MAX                 /* C23 */
    S("<limits.h>", BOOL_MAX, "bool", "fixed");
#endif
#ifdef BOOL_WIDTH              /* C23 */
    D("<limits.h>", BOOL_WIDTH, "bool", "fixed");
#endif

    /* ---------------------------------------------------------------- <stdint.h> */
    /* exact-width integer types: int8_t / int16_t / int32_t / int64_t */
#ifdef INT8_MAX
    S("<stdint.h>", INT8_MIN, "int8_t", "fixed");
    S("<stdint.h>", INT8_MAX, "int8_t", "fixed");
    U("<stdint.h>", UINT8_MAX, "uint8_t", "fixed");
#endif
#ifdef INT8_WIDTH             /* C23 */
    D("<stdint.h>", INT8_WIDTH, "int8_t", "fixed");
    D("<stdint.h>", UINT8_WIDTH, "uint8_t", "fixed");
#endif
#ifdef INT16_MAX
    S("<stdint.h>", INT16_MIN, "int16_t", "fixed");
    S("<stdint.h>", INT16_MAX, "int16_t", "fixed");
    U("<stdint.h>", UINT16_MAX, "uint16_t", "fixed");
#endif
#ifdef INT16_WIDTH            /* C23 */
    D("<stdint.h>", INT16_WIDTH, "int16_t", "fixed");
    D("<stdint.h>", UINT16_WIDTH, "uint16_t", "fixed");
#endif
#ifdef INT32_MAX
    S("<stdint.h>", INT32_MIN, "int32_t", "fixed");
    S("<stdint.h>", INT32_MAX, "int32_t", "fixed");
    U("<stdint.h>", UINT32_MAX, "uint32_t", "fixed");
#endif
#ifdef INT32_WIDTH            /* C23 */
    D("<stdint.h>", INT32_WIDTH, "int32_t", "fixed");
    D("<stdint.h>", UINT32_WIDTH, "uint32_t", "fixed");
#endif
#ifdef INT64_MAX
    S("<stdint.h>", INT64_MIN, "int64_t", "fixed");
    S("<stdint.h>", INT64_MAX, "int64_t", "fixed");
    U("<stdint.h>", UINT64_MAX, "uint64_t", "fixed");
#endif
#ifdef INT64_WIDTH            /* C23 */
    D("<stdint.h>", INT64_WIDTH, "int64_t", "fixed");
    D("<stdint.h>", UINT64_WIDTH, "uint64_t", "fixed");
#endif

    return 0;
}
