package org.FastData.Spring.Annotation;

import org.FastData.Spring.FastServiceAop.IFastServiceAop;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface FastServiceAop {
    Class<?> aopType() default IFastServiceAop.class;
    String packageName() default "";
}
