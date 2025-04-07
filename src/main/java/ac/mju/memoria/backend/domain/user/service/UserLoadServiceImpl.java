package ac.mju.memoria.backend.domain.user.service;

import ac.mju.memoria.backend.domain.user.repository.UserRepository;
import ac.mju.memoria.backend.system.security.model.AuthDetails;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import ac.mju.memoria.backend.system.security.service.UserLoadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserLoadServiceImpl implements UserLoadService {
    private final UserRepository userRepository;

    @Override
    public Optional<UserDetails> loadUserByKey(String key) {
        var foundUser = userRepository.findByEmail(key);

        return foundUser
                .map(UserDetails::from);
    }
}
