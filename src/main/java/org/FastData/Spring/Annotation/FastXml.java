package org.FastData.Spring.Annotation;

import org.FastData.Spring.Model.PageResultImpl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FastXml {
    String[] xml() default "";

    String dbKey() default "";

    boolean isPage() default false;

    Class<?> pageType() default PageResultImpl.class;
}
