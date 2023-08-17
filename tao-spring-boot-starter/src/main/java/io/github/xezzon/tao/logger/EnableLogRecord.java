package io.github.xezzon.tao.logger;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author xezzon
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(LogRecordConfig.class)
public @interface EnableLogRecord {

    boolean global() default false;
}
