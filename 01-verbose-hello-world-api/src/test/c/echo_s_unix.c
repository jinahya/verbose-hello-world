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
#include <errno.h>
#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <unistd.h>

#include "echo_.h"

static void *serve(void *arg) {
  int server = *(int *)arg;
  int accepted = accept(server, NULL, NULL);
  if (accepted < 0) {
    perror("accept");
    return NULL;
  }
  char buf[BYTES];
  size_t off = 0;
  while (off < BYTES) {
    ssize_t r = recv(accepted, buf + off, BYTES - off, 0);
    if (r <= 0) {
      perror("recv");
      close(accepted);
      return NULL;
    }
    off += (size_t)r;
  }
  off = 0;
  while (off < BYTES) {
    ssize_t w = send(accepted, buf + off, BYTES - off, 0);
    if (w <= 0) {
      perror("send");
      close(accepted);
      return NULL;
    }
    off += (size_t)w;
  }
  close(accepted);
  return NULL;
}

int main(void) {
  char dir[] = "/tmp/echo.XXXXXX";
  if (mkdtemp(dir) == NULL) {
    perror("mkdtemp");
    return 1;
  }
  struct sockaddr_un addr = {0};
  addr.sun_family = AF_UNIX;
  if ((size_t)snprintf(addr.sun_path, sizeof(addr.sun_path), "%s/echo.sock",
                       dir) >= sizeof(addr.sun_path)) {
    fprintf(stderr, "socket path too long\n");
    rmdir(dir);
    return 1;
  }

  int rc = 1;
  int server = socket(AF_UNIX, SOCK_STREAM, 0);
  if (server < 0) {
    if (errno == EAFNOSUPPORT || errno == EPROTONOSUPPORT) {
      fprintf(stderr, "AF_UNIX is not available on this platform\n");
    } else {
      perror("socket");
    }
    goto cleanup_dir;
  }
  if (bind(server, (struct sockaddr *)&addr, sizeof(addr)) < 0) {
    perror("bind");
    goto cleanup_server;
  }
  if (listen(server, 1) < 0) {
    perror("listen");
    goto cleanup_path;
  }
  printf("[server] bound: '%s'\n", addr.sun_path);

  pthread_t th;
  if (pthread_create(&th, NULL, serve, &server) != 0) {
    perror("pthread_create");
    goto cleanup_path;
  }

  int client = socket(AF_UNIX, SOCK_STREAM, 0);
  if (client < 0) {
    perror("socket");
    pthread_join(th, NULL);
    goto cleanup_path;
  }
  if (connect(client, (struct sockaddr *)&addr, sizeof(addr)) < 0) {
    perror("connect");
    close(client);
    pthread_join(th, NULL);
    goto cleanup_path;
  }
  printf("[client] connected to : '%s'\n", addr.sun_path);

  size_t off = 0;
  while (off < BYTES) {
    ssize_t w = send(client, HELLO_WORLD + off, BYTES - off, 0);
    if (w <= 0) {
      perror("send");
      close(client);
      pthread_join(th, NULL);
      goto cleanup_path;
    }
    off += (size_t)w;
  }
  char dst[BYTES + 1];
  off = 0;
  while (off < BYTES) {
    ssize_t r = recv(client, dst + off, BYTES - off, 0);
    if (r <= 0) {
      perror("recv");
      close(client);
      pthread_join(th, NULL);
      goto cleanup_path;
    }
    off += (size_t)r;
  }
  dst[BYTES] = '\0';
  if (memcmp(dst, HELLO_WORLD, BYTES) != 0) {
    fprintf(stderr, "unexpected: \"%s\"\n", dst);
    close(client);
    pthread_join(th, NULL);
    goto cleanup_path;
  }
  printf("[client] received: \"%s\"\n", dst);
  rc = 0;

  close(client);
  pthread_join(th, NULL);
cleanup_path:
  unlink(addr.sun_path);
cleanup_server:
  close(server);
cleanup_dir:
  rmdir(dir);
  return rc;
}
