/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2026 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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
