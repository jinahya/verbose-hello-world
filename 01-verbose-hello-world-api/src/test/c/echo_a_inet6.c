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
#include <arpa/inet.h>
#include <errno.h>
#include <fcntl.h>
#include <netinet/in.h>
#include <poll.h>
#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
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
  int server = socket(AF_INET6, SOCK_STREAM, 0);
  if (server < 0) {
    if (errno == EAFNOSUPPORT || errno == EPROTONOSUPPORT) {
      fprintf(stderr, "AF_INET6 is not available on this platform\n");
      return 1;
    }
    perror("socket");
    return 1;
  }
  if (set_nonblock(server) < 0) {
    perror("nonblock server");
    close(server);
    return 1;
  }
  struct sockaddr_in6 addr = {0};
  addr.sin6_family = AF_INET6;
  addr.sin6_addr = in6addr_loopback;
  if (bind(server, (struct sockaddr *)&addr, sizeof(addr)) < 0) {
    perror("bind");
    close(server);
    return 1;
  }
  if (listen(server, 1) < 0) {
    perror("listen");
    close(server);
    return 1;
  }
  socklen_t addrlen = sizeof(addr);
  if (getsockname(server, (struct sockaddr *)&addr, &addrlen) < 0) {
    perror("getsockname");
    close(server);
    return 1;
  }
  char host[INET6_ADDRSTRLEN];
  inet_ntop(AF_INET6, &addr.sin6_addr, host, sizeof(host));
  printf("[server] bound: ('%s', %u, %u, %u)\n", host, ntohs(addr.sin6_port),
         ntohl(addr.sin6_flowinfo), addr.sin6_scope_id);

  pthread_t th;
  if (pthread_create(&th, NULL, serve, &server) != 0) {
    perror("pthread_create");
    close(server);
    return 1;
  }

  int client = socket(AF_INET6, SOCK_STREAM, 0);
  if (client < 0) {
    perror("socket");
    close(server);
    return 1;
  }
  if (set_nonblock(client) < 0) {
    perror("nonblock client");
    close(client);
    close(server);
    return 1;
  }
  int rc = connect(client, (struct sockaddr *)&addr, addrlen);
  if (rc < 0 && errno != EINPROGRESS) {
    perror("connect");
    close(client);
    close(server);
    return 1;
  }
  if (rc < 0) {
    if (wait_for(client, POLLOUT) < 0) {
      perror("poll connect");
      close(client);
      close(server);
      return 1;
    }
    int err = 0;
    socklen_t errlen = sizeof(err);
    if (getsockopt(client, SOL_SOCKET, SO_ERROR, &err, &errlen) < 0 ||
        err != 0) {
      fprintf(stderr, "connect failed: %s\n", strerror(err));
      close(client);
      close(server);
      return 1;
    }
  }
  printf("[client] connected to : ('%s', %u, %u, %u)\n", host,
         ntohs(addr.sin6_port), ntohl(addr.sin6_flowinfo), addr.sin6_scope_id);

  if (send_all(client, HELLO_WORLD, BYTES) < 0) {
    perror("send");
    close(client);
    close(server);
    return 1;
  }
  char dst[BYTES + 1];
  if (recv_all(client, dst, BYTES) < 0) {
    perror("recv");
    close(client);
    close(server);
    return 1;
  }
  dst[BYTES] = '\0';
  if (memcmp(dst, HELLO_WORLD, BYTES) != 0) {
    fprintf(stderr, "unexpected: \"%s\"\n", dst);
    close(client);
    close(server);
    return 1;
  }
  printf("[client] received: \"%s\"\n", dst);

  close(client);
  pthread_join(th, NULL);
  close(server);
  return 0;
}
