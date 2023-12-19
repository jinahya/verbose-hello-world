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
def randomize(array):
    array[:] = random.randbytes(len(array))


with closing(socket.socket(FAMILY, socket.SOCK_STREAM)) as s:
    # -------------------------------------------------------------------- bind
    if random.choice([True, False]):
        s.bind((HOST, 0))
        print(f'(optionally) bound to {s.getsockname()}')
    # ----------------------------------------------------------------- connect
    s.connect((HOST, PORT))
    print(f'connected to {s.getpeername()}')
    bytes = random.randint(0, 65536)
    print(f'sending {bytes} byte(s)')
    array = bytearray(random.randint(1, 8192))
    print(f'array.length: {len(array)}')
    hash = hashlib.sha1()
    while bytes > 0:
        # random.shuffle(array)
        randomize(array)
        if bytes < len(array):
            array = array[0:bytes]
        # ---------------------------------------------------------------- send
        w = s.send(array)
        hash.update(array)
        bytes -= w
    print(f'digest: {hash.hexdigest()}')
