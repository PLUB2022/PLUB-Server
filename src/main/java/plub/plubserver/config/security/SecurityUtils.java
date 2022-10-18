package plub.plubserver.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import plub.plubserver.domain.account.exception.NotFoundAccountException;


public class SecurityUtils {

    public static String getCurrentAccountEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() ==null){
            throw new NotFoundAccountException();
        }
        return authentication.getName();
    }
}
