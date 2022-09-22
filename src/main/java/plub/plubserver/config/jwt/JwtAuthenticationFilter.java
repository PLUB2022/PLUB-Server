package plub.plubserver.config.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    private final String[] excludePaths = {"/issue"};

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = jwtProvider.resolveToken(request);
        if (jwtProvider.validate(accessToken)) {
            Authentication authentication = jwtProvider.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
//        String path = request.getRequestURI();
//        // jwt filter 처리 안 할 URI 인지 검사
//        if (Arrays.stream(excludePaths).noneMatch(path::equals)) {
//
//            String accessToken = jwtProvider.resolveToken(request);
//            if (accessToken == null)
//                servletResponseManager.sendError(response, "jwt 토큰이 없습니다.");
//            else {
//                // 토큰 유효성 검증
//                //case DENIED, EXPIRED -> servletResponseManager.sendError(response, "유효하지 않음");
//                if (jwtProvider.validate(accessToken) == JwtCode.ACCESS) {
//                    Authentication authentication = jwtProvider.getAuthentication(accessToken);
//                    SecurityContextHolder.getContext().setAuthentication(authentication);
//                    filterChain.doFilter(request, response);
//                }
//            }
//        } // jwt filter 가 처리할 URI 가 아니라면 그냥 흘려보낸다
//        else {
//            filterChain.doFilter(request, response);
//        }
    }
}
