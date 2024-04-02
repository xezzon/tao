package io.github.xezzon.tao.logger;

import cn.hutool.core.util.DesensitizedUtil.DesensitizedType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogDesensitize {

  DesensitizedType value();
}
