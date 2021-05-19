package org.FastData.Annotation;

import org.FastData.Repository.FastRepository;
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
}
