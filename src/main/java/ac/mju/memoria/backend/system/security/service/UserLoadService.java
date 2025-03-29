package ac.mju.memoria.backend.system.security.service;

import ac.mju.memoria.backend.system.security.model.AuthDetails;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UserLoadService {
    Optional<? extends AuthDetails> loadUserByKey(String key);
}
