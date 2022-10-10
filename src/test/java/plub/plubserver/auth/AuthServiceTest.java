package plub.plubserver.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import plub.plubserver.domain.account.service.AuthService;

@SpringBootTest
class AuthServiceTest {
    @Autowired
    AuthService authService;

    @Test @DisplayName("loginAccess 실패 - JWT")
    void test() {

    }
}
