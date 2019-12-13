package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2019 Jinahya, Inc.
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
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.hibernate.validator.testutil.ValidationInvocationHandler;

import javax.validation.Validation;
import javax.validation.Validator;
import java.lang.reflect.InvocationHandler;

import static java.lang.reflect.Proxy.newProxyInstance;

/**
 * A class for proxying validation instances.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
final class ValidationProxy {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns a proxy of specified hello world instance whose method arguments and result are validated.
     *
     * @return a validation proxy of specified instance.
     */
    @SuppressWarnings({"unchecked"})
    static <T extends HelloWorld> T newValidationProxy(final Class<T> type, final T instance) {
        if (type == null) {
            throw new NullPointerException("type is null");
        }
        if (!type.isInterface()) {
            throw new IllegalArgumentException("type is not an interface");
        }
        if (instance == null) {
            throw new NullPointerException("instance is null");
        }
        final ClassLoader loader = instance.getClass().getClassLoader();
        final Class<?>[] interfaces = new Class<?>[] {type};
        final Validator validator
                = Validation.byDefaultProvider()
                .configure()
                .messageInterpolator(new ParameterMessageInterpolator())
                .buildValidatorFactory()
                .getValidator();
        final InvocationHandler handler = new ValidationInvocationHandler(instance, validator);
        return (T) newProxyInstance(loader, interfaces, handler);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private ValidationProxy() {
        super();
    }
}
