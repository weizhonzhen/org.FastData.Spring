package org.FastData.Spring.Annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FastRead {
    String sql() default "";
}
