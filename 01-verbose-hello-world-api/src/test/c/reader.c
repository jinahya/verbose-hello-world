#include <fcntl.h>
#include <stdio.h>
#include <string.h>
#include <sys/mman.h>
#include <unistd.h>

int main(int argc, char *argv[]) {
  if (argc < 2) {
    return 1;
  }
  int fd = open(argv[1], O_RDONLY);
  if (fd < 0) {
    perror("C: Failed to open shared file");
    return 1;
  }
  char *mem = mmap(NULL, 13, PROT_READ, MAP_SHARED, fd, 0);
  if (mem == MAP_FAILED) {
    perror("C: mmap failed");
    return 1;
  }
  printf("C Output: %s\n", mem);
  munmap(mem, 13);
  close(fd);
  return 0;
}
