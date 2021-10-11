package org.FastData.Spring.Annotation;

import org.FastData.Spring.Model.PageResultImpl;
import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FastRead {
    String sql() default "";

    String dbKey() default "";

    boolean isPage() default false;

    Class<?> pageType() default PageResultImpl.class;
}
