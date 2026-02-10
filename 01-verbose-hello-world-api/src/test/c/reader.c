#include <stdio.h>
#include <fcntl.h>
#include <sys/mman.h>
#include <unistd.h>
#include <string.h>

int main(int argc, char *argv[]) {
    if (argc < 2) return 1;

    // Open the shared memory file created by Java
    int fd = open(argv[1], O_RDONLY);
    if (fd < 0) {
        perror("C: Failed to open shared file");
        return 1;
    }

    // Map the 13 bytes (12 ASCII + 1 Null)
    char *mem = mmap(NULL, 13, PROT_READ, MAP_SHARED, fd, 0);
    if (mem == MAP_FAILED) {
        perror("C: mmap failed");
        return 1;
    }

    // Print the shared data
    printf("C Output: %s\n", mem);

    munmap(mem, 13);
    close(fd);
    return 0;
}
