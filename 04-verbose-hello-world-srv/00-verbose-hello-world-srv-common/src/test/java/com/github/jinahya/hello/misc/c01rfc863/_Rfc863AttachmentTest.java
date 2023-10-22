package com.github.jinahya.hello.misc.c01rfc863;

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
