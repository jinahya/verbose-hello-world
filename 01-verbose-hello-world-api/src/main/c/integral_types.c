#include <stdint.h>
#include <stdbool.h>
#include <stdio.h>
#include <string.h>

int main(void) {
    /* runtime: inspect the bit pattern of -1 to distinguish all three */
    {
        signed char minus_one = -1;
        unsigned char bits;
        memcpy(&bits, &minus_one, sizeof bits);
        if      (bits == 0xFF) printf("Signed integer representation  (runtime): two's complement  (-1 = 0xFF)\n");
        else if (bits == 0xFE) printf("Signed integer representation  (runtime): ones' complement  (-1 = 0xFE)\n");
        else if (bits == 0x81) printf("Signed integer representation  (runtime): sign-magnitude    (-1 = 0x81)\n");
        else                   printf("Signed integer representation  (runtime): unknown            (-1 = 0x%02X)\n", bits);
    }
    /* 1. Exact-Width Signed Integers */
    printf("%-10s %-5s %-22s %-22s\n", "TYPE", "BITS", "MIN", "MAX");
    printf("------------------------------------------------------------\n");
    printf("%-10s %-5d %-22lld %-22lld\n", "int8_t",  (int)(sizeof(int8_t)  * 8), (long long)INT8_MIN,  (long long)INT8_MAX);
    printf("%-10s %-5d %-22lld %-22lld\n", "int16_t", (int)(sizeof(int16_t) * 8), (long long)INT16_MIN, (long long)INT16_MAX);
    printf("%-10s %-5d %-22lld %-22lld\n", "int32_t", (int)(sizeof(int32_t) * 8), (long long)INT32_MIN, (long long)INT32_MAX);
    printf("%-10s %-5d %-22lld %-22lld\n", "int64_t", (int)(sizeof(int64_t) * 8), (long long)INT64_MIN, (long long)INT64_MAX);

    /* 2. Exact-Width Unsigned Integers */
    printf("\n%-10s %-5s %-22s %-22s\n", "TYPE", "BITS", "MIN", "MAX");
    printf("------------------------------------------------------------\n");
    printf("%-10s %-5d %-22d %-22llu\n", "uint8_t",  (int)(sizeof(uint8_t)  * 8), 0, (unsigned long long)UINT8_MAX);
    printf("%-10s %-5d %-22d %-22llu\n", "uint16_t", (int)(sizeof(uint16_t) * 8), 0, (unsigned long long)UINT16_MAX);
    printf("%-10s %-5d %-22d %-22llu\n", "uint32_t", (int)(sizeof(uint32_t) * 8), 0, (unsigned long long)UINT32_MAX);
    printf("%-10s %-5d %-22d %-22llu\n", "uint64_t", (int)(sizeof(uint64_t) * 8), 0, (unsigned long long)UINT64_MAX);

    /* 3. Boolean Type */
    printf("\n%-14s %-22s %-22s\n", "TYPE", "MIN", "MAX");
    printf("------------------------------------------------------------\n");
    printf("%-14s %-22d %-22d\n", "bool (_Bool)", 0, 1);

    return 0;
}
