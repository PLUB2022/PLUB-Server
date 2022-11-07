package plub.plubserver.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import plub.plubserver.common.dto.ApiResponse;
import plub.plubserver.domain.account.exception.AccountError;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    // 인증은 되었으나, 해당 자원 접근에 권한(Role)이 없을 경우 403
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException {
        log.warn("Forbidden access = {}", e.getMessage());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        String body = objectMapper.writeValueAsString(
                ApiResponse.error(AccountError.FILTER_ROLE_FORBIDDEN.getStatusCode(), AccountError.FILTER_ROLE_FORBIDDEN.getMessage())
        );
        response.getWriter().write(body);
    }
}
