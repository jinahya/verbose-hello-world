package com.github.jinahya.hello.lib;

/*-
 * #%L
 * verbose-hello-world-lib
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

import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * A CDI {@link jakarta.interceptor.Interceptor &#64;Interceptor} bound to {@link HelloWorld_Logging}
 * that {@code DEBUG}-logs every invocation it intercepts.
 *
 * <p>For every bound call the interceptor emits an entry / exit pair:
 * <ul>
 *   <li>{@code -> <method>(<args>)} before {@link jakarta.interceptor.InvocationContext#proceed()
 *       proceed()}; and</li>
 *   <li>{@code <- <method> = <result>} after {@code proceed()} returns, or
 *       {@code <- <method> threw} (with the throwable) when {@code proceed()} throws.</li>
 * </ul>
 *
 * <p>In addition it emits one CDI-aware line per call:
 * <ul>
 *   <li>If the intercepted method carries {@link jakarta.enterprise.inject.Produces &#64;Produces}:
 *       {@code producing [<result>]} — optionally followed by
 *       {@code &#9;for [<injection-point-member-name>]} when any argument is a
 *       {@link jakarta.enterprise.inject.spi.InjectionPoint}.</li>
 *   <li>If any argument carries {@link jakarta.enterprise.inject.Disposes &#64;Disposes}:
 *       {@code disposing [<bean>]} on that argument.</li>
 * </ul>
 *
 * <p>Activated by {@link jakarta.annotation.Priority &#64;Priority(Interceptor.Priority.APPLICATION)};
 * no {@code beans.xml} entry is required.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@HelloWorld_Logging
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
@Slf4j
class HelloWorld_LoggingInterceptor {

    @AroundInvoke
    Object intercept(final InvocationContext context) throws Exception {
        final var method = context.getMethod();
        final var args = context.getParameters();
        log.debug("  -> {}({})", method.getName(), Arrays.deepToString(args));
        try {
            final var result = context.proceed();
            log.debug("  <- {} = {}", method.getName(), result);
            if (method.isAnnotationPresent(jakarta.enterprise.inject.Produces.class)) {
                final var ip = firstInjectionPoint(args);
                if (ip != null) {
                    log.debug("producing [{}]\tfor [{}]", result, ip.getMember().getName());
                } else {
                    log.debug("producing [{}]", result);
                }
            }
            final var disposed = firstDisposed(method, args);
            if (disposed != null) {
                log.debug("disposing [{}]", disposed);
            }
            return result;
        } catch (final Throwable t) {
            log.debug("  <- {} threw", method.getName(), t);
            throw t;
        }
    }

    private static jakarta.enterprise.inject.spi.InjectionPoint firstInjectionPoint(
            final Object[] args) {
        for (final var arg : args) {
            if (arg instanceof jakarta.enterprise.inject.spi.InjectionPoint ip) {
                return ip;
            }
        }
        return null;
    }

    private static Object firstDisposed(final Method method, final Object[] args) {
        final var perParam = method.getParameterAnnotations();
        for (int i = 0; i < perParam.length; i++) {
            for (final var a : perParam[i]) {
                if (a.annotationType() == jakarta.enterprise.inject.Disposes.class) {
                    return args[i];
                }
            }
        }
        return null;
    }
}
