package org.FastData.Spring.Annotation;

import org.FastData.Spring.Aop.IFastAop;
import org.FastData.Spring.Model.PageResultImpl;
import org.FastData.Spring.Repository.FastRepository;
import org.springframework.context.annotation.Import;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({FastRepository.class})
public @interface FastData {

    String key() default "";

    String cachePackageName() default "";

    String codeFirstPackageName() default "";

    String servicePackageName() default "";

    Class<?> aopType() default IFastAop.class;
}