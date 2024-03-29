package io.github.xezzon.tao.logger;

import cn.hutool.core.net.NetUtil;
import io.github.xezzon.tao.constant.KeyConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 日志拦截器
 * @author xezzon
 * @deprecated 请使用Trace替代
 */
@Deprecated
public class LogInterceptor implements HandlerInterceptor {

  public static final String ENDPOINT = "endpoint";
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
    MDC.put(ENDPOINT,
        request.getRequestURI() + ":" + request.getMethod() + "?" + request.getParameterNames()
    );
    // UA
    MDC.put(USER_AGENT, request.getHeader("User-Agent"));
    // IP
    String ip = Stream.of("X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR")
        .parallel()
        .map(request::getHeader)
        .filter(o -> !NetUtil.isUnknown(o))
        .findFirst()
        .orElse(request.getRemoteAddr());
    MDC.put(IP, ip);
    return HandlerInterceptor.super.preHandle(request, response, handler);
  }
}
