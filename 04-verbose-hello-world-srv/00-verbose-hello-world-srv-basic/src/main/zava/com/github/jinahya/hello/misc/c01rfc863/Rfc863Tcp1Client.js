/*-
 * #%L
 * verbose-hello-world-srv-common
 * %%
 * Copyright (C) 2018 - 2023 Jinahya, Inc.
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
// #!/usr/bin/node

'use strict';

const assert = require('node:assert');
const crypto = require('node:crypto');
const net = require('node:net');

function main() {
    const client = new net.Socket();
    client.on('connect', function () {
        console.log('connected to %s:%d/%s', client.remoteAddress, client.remotePort, client.remoteFamily);
        const hash = crypto.createHash('sha1');
        let bytes = Math.floor(Math.random() * 65536);
        console.log('sending %d byte(s)', bytes);
        const length = Math.floor(Math.random() * 8192) + 1;
        console.log('array.length: %d', length);
        let array = new Uint8Array(length);
        while (bytes > 0) {
            crypto.getRandomValues(array);
            if (array.length > bytes) {
                array = array.slice(0, bytes);
            }
            if (!client.write(array)) {
                client.once('drain');
            }
            hash.update(array);
            bytes -= array.length;
        }
        client.end();
        client.destroy();
        console.log('digest: %s', hash.digest('hex'));
    });
    client.on('error', function (error) {
        console.log('error: %s', error);
    });
    client.on('end', function () {
    });
    client.on('close', function () {
    });
    const ipv4 = true;
    const host = ipv4 ? '127.0.0.1' : '::1';
    const port = 50009;
    if (ipv4) assert(net.isIPv4(host)); else assert(net.isIPv6(host));
    client.connect({
        host: host,
        port: port
    });
}

if (require.main === module) {
    main();
}
