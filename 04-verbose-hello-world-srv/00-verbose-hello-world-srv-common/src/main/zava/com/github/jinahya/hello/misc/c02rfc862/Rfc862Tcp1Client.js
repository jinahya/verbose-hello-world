// #!/usr/bin/node

'use strict';

const assert = require('node:assert');
const crypto = require('node:crypto');
const net = require('node:net');

const IPv4 = true;
const HOST = IPv4 ? '127.0.0.1' : '::1';
if (IPv4) assert(net.isIPv4(HOST)); else assert(net.isIPv6(HOST));
const PORT = 50007;


function main() {
    const client = new net.Socket();
    client.on('connect', function () {
        console.log('connected to %s:%d/%s', client.remoteAddress, client.remotePort, client.remoteFamily);
        const hash = crypto.createHash('sha256');
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
        console.log('digest: %s', hash.digest('base64'));
    });
    client.on('data', function (data) {
    });
    client.on('drain', function () {
    });
    client.on('error', function (error) {
        console.log('error: %s', error);
    });
    client.on('end', function () {
        client.destroy();
    });
    client.on('close', function () {
    });
    client.connect({
        host: HOST,
        port: PORT
    });
}

if (require.main === module) {
    main();
}
