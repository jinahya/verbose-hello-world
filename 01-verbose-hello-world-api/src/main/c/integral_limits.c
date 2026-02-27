// integral_limits.c
#include <limits.h>
#include <stdint.h>
#include <stdio.h>

int main(void) {
    printf("%-10s %5s %6s %26s %26s\n",
           "type", "SIZE", "BYTES", "MIN_VALUE", "MAX_VALUE");
    printf("%-10s %5zu %6zu %26d %26d\n",
           "int8_t",  sizeof(int8_t)  * CHAR_BIT, sizeof(int8_t),  INT8_MIN,  INT8_MAX);
    printf("%-10s %5zu %6zu %26d %26d\n",
           "int16_t", sizeof(int16_t) * CHAR_BIT, sizeof(int16_t), INT16_MIN, INT16_MAX);
    printf("%-10s %5zu %6zu %26d %26d\n",
           "int32_t", sizeof(int32_t) * CHAR_BIT, sizeof(int32_t), INT32_MIN, INT32_MAX);
    printf("%-10s %5zu %6zu %26lld %26lld\n",
           "int64_t", sizeof(int64_t) * CHAR_BIT, sizeof(int64_t),
           (long long) INT64_MIN, (long long) INT64_MAX);
    return 0;
}
