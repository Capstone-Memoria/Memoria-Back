package ac.mju.memoria.backend.domain.user.service;

import ac.mju.memoria.backend.domain.user.dto.UserDto;
import ac.mju.memoria.backend.domain.user.entity.User;
import ac.mju.memoria.backend.domain.user.repository.UserRepository;
import ac.mju.memoria.backend.system.exception.model.ErrorCode;
import ac.mju.memoria.backend.system.exception.model.RestException;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import ac.mju.memoria.backend.system.security.service.UserLoadService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDto.UserResponse updateProfile(
            Integer userId, UserDto.UserUpdateRequest request
    ) {
        User user = userRepository.findById(userId)
                        .orElseThrow(() -> new RestException(ErrorCode.USER_NOT_FOUND));

        request.applyTo(user, passwordEncoder);

        return UserDto.UserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public UserDto.UserResponse getProfile(Integer userId) {
        User user = userRepository.findById(userId)
                        .orElseThrow(() -> new RestException(ErrorCode.USER_NOT_FOUND));

        return UserDto.UserResponse.from(user);
    }

}
