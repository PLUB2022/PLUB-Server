package plub.plubserver.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import plub.plubserver.common.dto.ApiResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 필터에서 예외처리를 할 수 없기 때문에 예외가 발생했을때 강제로
 * Response 객체를 조작해서 응답 가게 해주는 객체
 */

@Component
@RequiredArgsConstructor
public class ServletResponseManager {

    private final ObjectMapper objectMapper;

    public void sendError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        String body = objectMapper.writeValueAsString(
                ApiResponse.error(message)
        );
        response.getWriter().write(body);
    }
}
