package io.github.xezzon.tao.logger;

import io.github.xezzon.tao.constant.KeyConstants;
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
    String traceId = request.getHeader(KeyConstants.TRACE_ID);
    if (traceId == null) {
      traceId = UUID.randomUUID().toString();
    }
    MDC.put(KeyConstants.TRACE_ID, traceId);
    String pSpanId = request.getHeader(KeyConstants.P_SPAN_ID);
    if (pSpanId != null) {
      MDC.put(KeyConstants.P_SPAN_ID, pSpanId);
    }
    MDC.put(KeyConstants.SPAN_ID, UUID.randomUUID().toString());
    MDC.put(CATALOG, request.getRequestURI() + ":" + request.getMethod());
    return HandlerInterceptor.super.preHandle(request, response, handler);
  }
}
