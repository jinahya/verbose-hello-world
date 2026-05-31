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
#include <fcntl.h>
#include <poll.h>
#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <unistd.h>

#include "echo_.h"

static int set_nonblock(int fd) {
  int flags = fcntl(fd, F_GETFL, 0);
  return flags < 0 ? -1 : fcntl(fd, F_SETFL, flags | O_NONBLOCK);
}

static int wait_for(int fd, short events) {
  struct pollfd pfd = {.fd = fd, .events = events};
  return poll(&pfd, 1, -1);
}

static int recv_all(int fd, void *buf, size_t n) {
  size_t off = 0;
  while (off < n) {
    ssize_t r = recv(fd, (char *)buf + off, n - off, 0);
    if (r > 0) {
      off += (size_t)r;
      continue;
    }
    if (r == 0) return -1;
    if (errno != EAGAIN && errno != EWOULDBLOCK) return -1;
    if (wait_for(fd, POLLIN) < 0) return -1;
  }
  return 0;
}

static int send_all(int fd, const void *buf, size_t n) {
  size_t off = 0;
  while (off < n) {
    ssize_t w = send(fd, (const char *)buf + off, n - off, 0);
    if (w > 0) {
      off += (size_t)w;
      continue;
    }
    if (errno != EAGAIN && errno != EWOULDBLOCK) return -1;
    if (wait_for(fd, POLLOUT) < 0) return -1;
  }
  return 0;
}

static void *serve(void *arg) {
  int server = *(int *)arg;
  if (wait_for(server, POLLIN) < 0) {
    perror("poll accept");
    return NULL;
  }
  int accepted = accept(server, NULL, NULL);
  if (accepted < 0) {
    perror("accept");
    return NULL;
  }
  if (set_nonblock(accepted) < 0) {
    perror("nonblock accepted");
    close(accepted);
    return NULL;
  }
  char buf[BYTES];
  if (recv_all(accepted, buf, BYTES) < 0) {
    perror("recv");
    close(accepted);
    return NULL;
  }
  if (send_all(accepted, buf, BYTES) < 0) {
    perror("send");
    close(accepted);
    return NULL;
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
  if (set_nonblock(server) < 0) {
    perror("nonblock server");
    goto cleanup_server;
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
  if (set_nonblock(client) < 0) {
    perror("nonblock client");
    close(client);
    pthread_join(th, NULL);
    goto cleanup_path;
  }
  int cr = connect(client, (struct sockaddr *)&addr, sizeof(addr));
  if (cr < 0 && errno != EINPROGRESS) {
    perror("connect");
    close(client);
    pthread_join(th, NULL);
    goto cleanup_path;
  }
  if (cr < 0) {
    if (wait_for(client, POLLOUT) < 0) {
      perror("poll connect");
      close(client);
      pthread_join(th, NULL);
      goto cleanup_path;
    }
    int err = 0;
    socklen_t errlen = sizeof(err);
    if (getsockopt(client, SOL_SOCKET, SO_ERROR, &err, &errlen) < 0 ||
        err != 0) {
      fprintf(stderr, "connect failed: %s\n", strerror(err));
      close(client);
      pthread_join(th, NULL);
      goto cleanup_path;
    }
  }
  printf("[client] connected to : '%s'\n", addr.sun_path);

  if (send_all(client, HELLO_WORLD, BYTES) < 0) {
    perror("send");
    close(client);
    pthread_join(th, NULL);
    goto cleanup_path;
  }
  char dst[BYTES + 1];
  if (recv_all(client, dst, BYTES) < 0) {
    perror("recv");
    close(client);
    pthread_join(th, NULL);
    goto cleanup_path;
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
