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
#include <netinet/in.h>
#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
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
  int server = socket(AF_INET, SOCK_STREAM, 0);
  if (server < 0) {
    if (errno == EAFNOSUPPORT || errno == EPROTONOSUPPORT) {
      fprintf(stderr, "AF_INET is not available on this platform\n");
      return 1;
    }
    perror("socket");
    return 1;
  }
  struct sockaddr_in addr = {0};
  addr.sin_family = AF_INET;
  addr.sin_addr.s_addr = htonl(INADDR_LOOPBACK);
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
  char host[INET_ADDRSTRLEN];
  inet_ntop(AF_INET, &addr.sin_addr, host, sizeof(host));
  printf("[server] bound: ('%s', %u)\n", host, ntohs(addr.sin_port));

  pthread_t th;
  if (pthread_create(&th, NULL, serve, &server) != 0) {
    perror("pthread_create");
    close(server);
    return 1;
  }

  int client = socket(AF_INET, SOCK_STREAM, 0);
  if (client < 0) {
    perror("socket");
    close(server);
    return 1;
  }
  if (connect(client, (struct sockaddr *)&addr, addrlen) < 0) {
    perror("connect");
    close(client);
    close(server);
    return 1;
  }
  printf("[client] connected to : ('%s', %u)\n", host, ntohs(addr.sin_port));

  size_t off = 0;
  while (off < BYTES) {
    ssize_t w = send(client, HELLO_WORLD + off, BYTES - off, 0);
    if (w <= 0) {
      perror("send");
      close(client);
      close(server);
      return 1;
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
      close(server);
      return 1;
    }
    off += (size_t)r;
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
