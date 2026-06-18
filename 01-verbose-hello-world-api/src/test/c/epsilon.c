#include <stdio.h>
#include <float.h>

int main(void) {
    /* Machine epsilon as defined in <float.h>. */
    printf("FLT_EPSILON : %.20e\n", FLT_EPSILON);
    printf("DBL_EPSILON : %.20e\n", DBL_EPSILON);
    printf("LDBL_EPSILON: %.20Le\n", LDBL_EPSILON);

    /* Machine epsilon computed at run time for type double. */
    double epsilon = 1.0;
    while ((1.0 + epsilon / 2.0) != 1.0) {
        epsilon /= 2.0;
    }
    printf("computed    : %.20e\n", epsilon);

    return 0;
}
