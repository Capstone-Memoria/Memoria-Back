package ac.mju.memoria.backend.system.auditor;

import ac.mju.memoria.backend.domain.user.entity.User;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<User> {

    @Override
    public Optional<User> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            return Optional.ofNullable(userDetails.getUser());
        } else if (principal instanceof User) {
            return Optional.of((User) principal);
        }

        return Optional.empty();
    }
}
