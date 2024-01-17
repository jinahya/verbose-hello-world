#!/usr/bin/python3

###
# #%L
# verbose-hello-world-srv-common
# %%
# Copyright (C) 2018 - 2023 Jinahya, Inc.
# %%
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# 
#      http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# #L%
###

import hashlib
import random
import socket
import sys
from contextlib import closing

IPv4 = True
# IPv4 = False
FAMILY = socket.AF_INET if IPv4 else socket.AF_INET6
HOST = '127.0.0.1' if IPv4 else "::1"
PORT = 50009


def hook(event, args):
    print(f'\t[audit]: {event} with args={args}')


sys.addaudithook(hook)


# https://stackoverflow.com/a/77426018/330457
def randomize(a):
    a[:] = random.randbytes(len(a))


with closing(socket.socket(FAMILY, socket.SOCK_STREAM)) as s:
    # -------------------------------------------------------------------- bind
    if random.choice([True, False]):
        s.bind((HOST, 0))
        print(f'(optionally) bound to {s.getsockname()}')
    # ----------------------------------------------------------------- connect
    s.connect((HOST, PORT))
    print(f'connected to {s.getpeername()}')
    numberOfBytes = random.randint(0, 65536)
    print(f'sending {numberOfBytes} byte(s)')
    array = bytearray(random.randint(1, 8192))
    print(f'array.length: {len(array)}')
    sha1 = hashlib.sha1()
    while numberOfBytes > 0:
        # random.shuffle(array)
        randomize(array)
        if numberOfBytes < len(array):
            array = array[0:numberOfBytes]
        # ---------------------------------------------------------------- send
        w = s.send(array)
        sha1.update(array)
        numberOfBytes -= w
    print(f'digest: {sha1.hexdigest()}')
