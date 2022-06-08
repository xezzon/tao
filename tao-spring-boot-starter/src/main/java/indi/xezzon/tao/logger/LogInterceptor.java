package indi.xezzon.tao.logger;

import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 日志拦截器
 * @author xezzon
 */
@Component
public class LogInterceptor implements HandlerInterceptor {

  /**
   * 保存线程内日志参数——traceId
   */
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    MDC.put("traceId", UUID.randomUUID().toString());
    MDC.put("catalog", request.getRequestURI() + ":" + request.getMethod());
    return HandlerInterceptor.super.preHandle(request, response, handler);
  }
}
