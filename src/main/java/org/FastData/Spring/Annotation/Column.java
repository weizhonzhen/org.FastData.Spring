package org.FastData.Spring.Annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    boolean isKey() default false;

    int length() default 0;

    int precision() default 0;

    int scale() default 0;

    String dataType() default "";

    boolean isNull() default true;

    String comments() default "";
}


