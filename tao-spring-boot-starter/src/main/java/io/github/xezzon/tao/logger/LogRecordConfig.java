package io.github.xezzon.tao.logger;

import java.lang.annotation.Annotation;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author xezzon
 */
@Configuration
public class LogRecordConfig implements ImportBeanDefinitionRegistrar {

  @Bean("geomLogRecordAspect")
  public LogRecordAspect logRecordAspect() {
    return new LogRecordAspect();
  }

  @Override
  public void registerBeanDefinitions(
      @NotNull AnnotationMetadata metadata,
      @NotNull BeanDefinitionRegistry registry
  ) {
    if (this.getConfiguration(metadata).isGlobal()) {
      registry.registerBeanDefinition(
          "geomGlobalLogAspect",
          new RootBeanDefinition(GlobalLogAspect.class)
      );
    }
  }

  protected AnnotationLogConfiguration getConfiguration(AnnotationMetadata metadata) {
    return new AnnotationLogConfiguration(metadata, EnableLogRecord.class);
  }
}

class AnnotationLogConfiguration {

  private final AnnotationAttributes attributes;

  public AnnotationLogConfiguration(
      @NotNull AnnotationMetadata metadata,
      @NotNull Class<? extends Annotation> annotation
  ) {
    Map<String, Object> attributesSource = metadata.getAnnotationAttributes(annotation.getName());
    if (attributesSource == null) {
      throw new IllegalArgumentException(
          String.format("Couldn't find annotation attributes for %s in %s", annotation, metadata)
      );
    }
    this.attributes = new AnnotationAttributes(attributesSource);
  }

  public boolean isGlobal() {
    return this.attributes.getBoolean("global");
  }
}
