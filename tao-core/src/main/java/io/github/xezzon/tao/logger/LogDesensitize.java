package io.github.xezzon.tao.logger;

import io.github.xezzon.tao.desensitize.Desensitizer;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogDesensitize {

  Class<? extends Desensitizer> value();
}
