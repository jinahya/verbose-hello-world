package com.github.jinahya.hello.misc.c01rfc863;

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

import com.github.jinahya.hello.misc.c00rfc86_._Rfc86_AttachmentTest;

abstract class _Rfc863AttachmentTest<T extends _Rfc863Attachment> extends _Rfc86_AttachmentTest<T> {

    abstract static class ClientTest<T extends _Rfc863Attachment.Client>
            extends _Rfc863AttachmentTest<T> {

        ClientTest(final Class<T> attachmentClass) {
            super(attachmentClass);
        }
    }

    abstract static class ServerTest<T extends _Rfc863Attachment.Server>
            extends _Rfc863AttachmentTest<T> {

        ServerTest(final Class<T> attachmentClass) {
            super(attachmentClass);
        }
    }

    _Rfc863AttachmentTest(final Class<T> attachmentClass) {
        super(attachmentClass);
    }
}
