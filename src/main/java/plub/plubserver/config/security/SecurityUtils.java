package plub.plubserver.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import plub.plubserver.exception.AccountException;


public class SecurityUtils {

    public static String getCurrentAccountEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null){
            throw new AccountException("Authentication 정보가 없습니다.");
        }
        return authentication.getName();
    }
}
