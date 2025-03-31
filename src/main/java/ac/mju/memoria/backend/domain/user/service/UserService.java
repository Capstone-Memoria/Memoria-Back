package ac.mju.memoria.backend.domain.user.service;

import ac.mju.memoria.backend.domain.user.repository.UserRepository;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import ac.mju.memoria.backend.system.security.service.UserLoadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserLoadService {
    private final UserRepository userRepository;


    public Optional<UserDetails> loadUserByKey(String key) {
        var foundUser = userRepository.findByEmail(key);

        return foundUser
                .map(UserDetails::from);
    }

}
