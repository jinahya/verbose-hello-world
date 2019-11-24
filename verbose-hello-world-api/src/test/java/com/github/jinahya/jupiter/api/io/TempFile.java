package com.github.jinahya.jupiter.api.io;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A marker annotation for parameterizing a temporary file.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see com.github.jinahya.jupiter.api.extension.TempFileParameterResolver
 */
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@Documented
public @interface TempFile {

}
