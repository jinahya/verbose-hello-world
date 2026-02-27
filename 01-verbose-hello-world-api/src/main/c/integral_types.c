#include <stddef.h>
#include <stdint.h>
#include <stdio.h>
#include <limits.h>
#include <stdbool.h>

int main(void) {
    printf("%-22s %-6s %-22s %-22s\n", "TYPE", "BYTES", "MIN", "MAX");
    printf("--------------------------------------------------------------------------\n");

    /* Standard Character Types */
    printf("%-22s %-6zu %-22d %-22d\n", "char",          sizeof(char),          CHAR_MIN,  CHAR_MAX);
    printf("%-22s %-6zu %-22d %-22d\n", "signed char",   sizeof(signed char),   SCHAR_MIN, SCHAR_MAX);
    printf("%-22s %-6zu %-22d %-22u\n", "unsigned char", sizeof(unsigned char), 0,         UCHAR_MAX);

    /* Standard Integer Types */
    printf("%-22s %-6zu %-22d %-22d\n",   "short",              sizeof(short),              SHRT_MIN,  SHRT_MAX);
    printf("%-22s %-6zu %-22d %-22u\n",   "unsigned short",     sizeof(unsigned short),     0,         USHRT_MAX);
    printf("%-22s %-6zu %-22d %-22d\n",   "int",                sizeof(int),                INT_MIN,   INT_MAX);
    printf("%-22s %-6zu %-22d %-22u\n",   "unsigned int",       sizeof(unsigned int),       0,         UINT_MAX);
    printf("%-22s %-6zu %-22ld %-22ld\n", "long",               sizeof(long),               LONG_MIN,  LONG_MAX);
    printf("%-22s %-6zu %-22d %-22lu\n",  "unsigned long",      sizeof(unsigned long),      0,         ULONG_MAX);
    printf("%-22s %-6zu %-22lld %-22lld\n","long long",         sizeof(long long),          LLONG_MIN, LLONG_MAX);
    printf("%-22s %-6zu %-22d %-22llu\n", "unsigned long long", sizeof(unsigned long long), 0,         ULLONG_MAX);

    /* Exact-Width Types (C99) */
    printf("--------------------------------------------------------------------------\n");
    printf("%-22s %-6zu %-22lld %-22lld\n", "int8_t",   sizeof(int8_t),   (long long)INT8_MIN,   (long long)INT8_MAX);
    printf("%-22s %-6zu %-22d %-22llu\n",   "uint8_t",  sizeof(uint8_t),  0,                     (unsigned long long)UINT8_MAX);
    printf("%-22s %-6zu %-22lld %-22lld\n", "int16_t",  sizeof(int16_t),  (long long)INT16_MIN,  (long long)INT16_MAX);
    printf("%-22s %-6zu %-22d %-22llu\n",   "uint16_t", sizeof(uint16_t), 0,                     (unsigned long long)UINT16_MAX);
    printf("%-22s %-6zu %-22lld %-22lld\n", "int32_t",  sizeof(int32_t),  (long long)INT32_MIN,  (long long)INT32_MAX);
    printf("%-22s %-6zu %-22d %-22llu\n",   "uint32_t", sizeof(uint32_t), 0,                     (unsigned long long)UINT32_MAX);
    printf("%-22s %-6zu %-22lld %-22lld\n", "int64_t",  sizeof(int64_t),  (long long)INT64_MIN,  (long long)INT64_MAX);
    printf("%-22s %-6zu %-22d %-22llu\n",   "uint64_t", sizeof(uint64_t), 0,                     (unsigned long long)UINT64_MAX);

    /* Minimum-Width Types (C99) */
    printf("--------------------------------------------------------------------------\n");
    printf("%-22s %-6zu %-22lld %-22lld\n", "int_least8_t",    sizeof(int_least8_t),    (long long)INT_LEAST8_MIN,    (long long)INT_LEAST8_MAX);
    printf("%-22s %-6zu %-22d %-22llu\n",   "uint_least8_t",   sizeof(uint_least8_t),   0,                            (unsigned long long)UINT_LEAST8_MAX);
    printf("%-22s %-6zu %-22lld %-22lld\n", "int_least16_t",   sizeof(int_least16_t),   (long long)INT_LEAST16_MIN,   (long long)INT_LEAST16_MAX);
    printf("%-22s %-6zu %-22d %-22llu\n",   "uint_least16_t",  sizeof(uint_least16_t),  0,                            (unsigned long long)UINT_LEAST16_MAX);
    printf("%-22s %-6zu %-22lld %-22lld\n", "int_least32_t",   sizeof(int_least32_t),   (long long)INT_LEAST32_MIN,   (long long)INT_LEAST32_MAX);
    printf("%-22s %-6zu %-22d %-22llu\n",   "uint_least32_t",  sizeof(uint_least32_t),  0,                            (unsigned long long)UINT_LEAST32_MAX);
    printf("%-22s %-6zu %-22lld %-22lld\n", "int_least64_t",   sizeof(int_least64_t),   (long long)INT_LEAST64_MIN,   (long long)INT_LEAST64_MAX);
    printf("%-22s %-6zu %-22d %-22llu\n",   "uint_least64_t",  sizeof(uint_least64_t),  0,                            (unsigned long long)UINT_LEAST64_MAX);

    /* Fastest Minimum-Width Types (C99) */
    printf("--------------------------------------------------------------------------\n");
    printf("%-22s %-6zu %-22lld %-22lld\n", "int_fast8_t",    sizeof(int_fast8_t),    (long long)INT_FAST8_MIN,    (long long)INT_FAST8_MAX);
    printf("%-22s %-6zu %-22d %-22llu\n",   "uint_fast8_t",   sizeof(uint_fast8_t),   0,                           (unsigned long long)UINT_FAST8_MAX);
    printf("%-22s %-6zu %-22lld %-22lld\n", "int_fast16_t",   sizeof(int_fast16_t),   (long long)INT_FAST16_MIN,   (long long)INT_FAST16_MAX);
    printf("%-22s %-6zu %-22d %-22llu\n",   "uint_fast16_t",  sizeof(uint_fast16_t),  0,                           (unsigned long long)UINT_FAST16_MAX);
    printf("%-22s %-6zu %-22lld %-22lld\n", "int_fast32_t",   sizeof(int_fast32_t),   (long long)INT_FAST32_MIN,   (long long)INT_FAST32_MAX);
    printf("%-22s %-6zu %-22d %-22llu\n",   "uint_fast32_t",  sizeof(uint_fast32_t),  0,                           (unsigned long long)UINT_FAST32_MAX);
    printf("%-22s %-6zu %-22lld %-22lld\n", "int_fast64_t",   sizeof(int_fast64_t),   (long long)INT_FAST64_MIN,   (long long)INT_FAST64_MAX);
    printf("%-22s %-6zu %-22d %-22llu\n",   "uint_fast64_t",  sizeof(uint_fast64_t),  0,                           (unsigned long long)UINT_FAST64_MAX);

    /* Pointer-Sized Types (C99) */
    printf("--------------------------------------------------------------------------\n");
    printf("%-22s %-6zu %-22lld %-22lld\n", "intptr_t",  sizeof(intptr_t),  (long long)INTPTR_MIN,  (long long)INTPTR_MAX);
    printf("%-22s %-6zu %-22d %-22llu\n",   "uintptr_t", sizeof(uintptr_t), 0,                      (unsigned long long)UINTPTR_MAX);

    /* Greatest-Width Types (C99) */
    printf("%-22s %-6zu %-22jd %-22jd\n", "intmax_t",  sizeof(intmax_t),  INTMAX_MIN,  INTMAX_MAX);
    printf("%-22s %-6zu %-22d %-22ju\n",  "uintmax_t", sizeof(uintmax_t), 0,           UINTMAX_MAX);

    /* Special Types */
    printf("--------------------------------------------------------------------------\n");
    printf("%-22s %-6zu %-22d %-22d\n",  "_Bool/bool",  sizeof(bool),      0,             1);
    printf("%-22s %-6zu %-22d %-22zu\n", "size_t",      sizeof(size_t),    0,             SIZE_MAX);
    printf("%-22s %-6zu %-22td %-22td\n","ptrdiff_t",   sizeof(ptrdiff_t), PTRDIFF_MIN,   PTRDIFF_MAX);

    return 0;
}
