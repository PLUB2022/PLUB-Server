package plub.plubserver.domain.notification.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotifyHost {
    NotifyDetail detail();
    long plubbingId() default 0L;
}
