package plub.plubserver.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


public class SecurityUtils {

    public static String getCurrentAccountEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
//            throw new AccountException(AccountCode.NOT_FOUND_ACCOUNT);
            return null;
        }
        return authentication.getName();
    }
}
