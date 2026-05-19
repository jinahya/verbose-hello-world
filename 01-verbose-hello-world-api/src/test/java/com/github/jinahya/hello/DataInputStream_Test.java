package com.github.jinahya.hello;

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

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.classfile.ClassFile;
import java.nio.file.Files;
import java.util.spi.ToolProvider;

@ExtendWith({MockitoExtension.class})
@Slf4j
class DataInputStream_Test {

    private static byte[] bytecode() {
        /* class A { } */
        return new byte[] {
                (byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE, // Magic
                0x00, 0x00,                                     // Minor version
                0x00, 0x34,                                     // Major version (JDK 8)
                0x00, 0x05,                                     // CP Count (4 entries + 1)
                0x07, 0x00, 0x03,                               // #1 Class "A"
                0x07, 0x00, 0x04,                               // #2 Class "java/lang/Object"
                0x01, 0x00, 0x01, 0x41,                         // #3 UTF-8 "A"
                0x01, 0x00, 0x10, 0x6A, 0x61, 0x76, 0x61, 0x2F, // #4 UTF-8 "java/lang/Object"
                0x6C, 0x61, 0x6E, 0x67, 0x2F, 0x4F, 0x62, 0x6A, 0x65, 0x63, 0x74,

                0x00, 0x00,                                     // Access Flags (Public)
                0x00, 0x01,                                     // This Class (Index #1)
                0x00, 0x02,                                     // Super Class (Index #2)
                0x00, 0x00,                                     // Interfaces Count
                0x00, 0x00,                                     // Fields Count
                0x00, 0x00,                                     // Methods Count
                0x00, 0x00                                      // Attributes Count
        };
    }

    @Test
    void __DataInput() throws IOException {
        // -----------------------------------------------------------------------------------------
        final var stream = new DataInputStream(new ByteArrayInputStream(bytecode()));
        // -----------------------------------------------------------------------------------------
        log.debug("magic: 0x{}", Integer.toHexString(stream.readInt()));
        log.debug("minor_version: {}", stream.readUnsignedShort());
        log.debug("major_version: {}", stream.readUnsignedShort());
        // -----------------------------------------------------------------------------------------
        final var constantPoolCount = stream.readUnsignedShort();
        log.debug("constant_pool_count: {}", constantPoolCount);
        for (int i = 1; i < constantPoolCount; i++) {
            switch (stream.readUnsignedByte()) {
                case 7 -> log.debug("  #{} CONSTANT_Class name_index={}", i,
                                    stream.readUnsignedShort());
                case 1 -> log.debug("  #{} CONSTANT_Utf8 \"{}\"", i, stream.readUTF());
                default -> log.warn("  #{} unknown tag", i);
            }
        }
        // -----------------------------------------------------------------------------------------
        log.debug("access_flags: 0x{}", Integer.toHexString(stream.readUnsignedShort()));
        log.debug("this_class: #{}", stream.readUnsignedShort());
        log.debug("super_class: #{}", stream.readUnsignedShort());
        log.debug("interfaces_count: {}", stream.readUnsignedShort());
        log.debug("fields_count: {}", stream.readUnsignedShort());
        log.debug("methods_count: {}", stream.readUnsignedShort());
        log.debug("attributes_count: {}", stream.readUnsignedShort());
    }

    @Test
    void __ToolProvider() throws IOException {
        final var file = Files.createTempFile(null, ".class");
        try {
            Files.write(file, bytecode());
            final var javap = ToolProvider.findFirst("javap").orElseThrow();
            javap.run(System.out, System.err, "-c", "-p", "-v", file.toString());
        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    void __ClassFileApi() {
        final var model = ClassFile.of().parse(bytecode());
        log.debug("magic: 0x{}", Integer.toHexString(ClassFile.MAGIC_NUMBER));
        log.debug("minor_version: {}", model.minorVersion());
        log.debug("major_version: {}", model.majorVersion());
        log.debug("constant_pool_count: {}", model.constantPool().size());
        model.constantPool().forEach(entry -> log.debug("  #{} {}", entry.index(), entry));
        log.debug("access_flags: 0x{}", Integer.toHexString(model.flags().flagsMask()));
        log.debug("this_class: {}", model.thisClass().asInternalName());
        log.debug("super_class: {}",
                  model.superclass().map(c -> c.asInternalName()).orElse(null));
        log.debug("interfaces_count: {}", model.interfaces().size());
        log.debug("fields_count: {}", model.fields().size());
        log.debug("methods_count: {}", model.methods().size());
        log.debug("attributes_count: {}", model.attributes().size());
    }
}
