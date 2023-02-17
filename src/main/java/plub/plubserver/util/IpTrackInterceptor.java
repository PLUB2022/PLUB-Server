package plub.plubserver.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.InetAddress;

@Slf4j
@Component
@RequiredArgsConstructor
public class IpTrackInterceptor implements HandlerInterceptor {

    public static String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    @Override
    public boolean preHandle(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull Object handler
    ) {
        InetAddress ipAddress;
        String cityInfo = "";
        try {
            ipAddress = InetAddress.getByName(getClientIP(request));
            cityInfo = new GeoReader().getCity(ipAddress);
        } catch (Exception ignored) {}
        log.info("{}({}) {} {}", getClientIP(request), cityInfo, request.getMethod(), request.getRequestURI());
        return true;
    }
}
