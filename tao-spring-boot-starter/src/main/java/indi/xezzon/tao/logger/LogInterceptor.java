package indi.xezzon.tao.logger;

import static indi.xezzon.tao.constant.KeyConstants.TRACE_ID;

import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 日志拦截器
 * @author xezzon
 */
public class LogInterceptor implements HandlerInterceptor {
  public static final String CATALOG = "catalog";

  /**
   * 保存线程内日志参数——traceId
   */
  @Override
  public boolean preHandle(
      HttpServletRequest request,
      @NotNull HttpServletResponse response,
      @NotNull Object handler
  ) throws Exception {
    MDC.put(TRACE_ID, UUID.randomUUID().toString());
    MDC.put(CATALOG, request.getRequestURI() + ":" + request.getMethod());
    return HandlerInterceptor.super.preHandle(request, response, handler);
  }
}
