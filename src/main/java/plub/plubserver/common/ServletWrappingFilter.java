package plub.plubserver.common;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * HttpServletRequest, Response 는 단 한번만 액세스 할 수 있어서
 * 여러번 접근하고 싶으면 Wrapping 을 해줘야 한다.
 */

@Component
@Order(1)
public class ServletWrappingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ContentCachingResponseWrapper wrapResponse = new ContentCachingResponseWrapper(response);

        filterChain.doFilter(request, wrapResponse);
        wrapResponse.copyBodyToResponse();
    }
}
