package io.github.xezzon.tao.logger;

import cn.hutool.extra.servlet.ServletUtil;
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
  public static final String USER_AGENT = "UA";
  public static final String IP = "ip";

  /**
   * 保存线程内日志参数 —— Tracing、模块、UA、IP
   * 用户等信息无法做到统一配置，需要用户侧处理
   */
  @Override
  public boolean preHandle(
      HttpServletRequest request,
      @NotNull HttpServletResponse response,
      @NotNull Object handler
  ) throws Exception {
    // Tracing
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
    // 模块
    MDC.put(CATALOG, request.getRequestURI() + ":" + request.getMethod());
    // UA
    MDC.put(USER_AGENT, request.getHeader("User-Agent"));
    // IP
    MDC.put(IP, ServletUtil.getClientIP(request));
    return HandlerInterceptor.super.preHandle(request, response, handler);
  }
}
