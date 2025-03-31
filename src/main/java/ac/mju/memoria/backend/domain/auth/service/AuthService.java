package ac.mju.memoria.backend.domain.auth.service;

import ac.mju.memoria.backend.domain.auth.dto.AuthDto;
import ac.mju.memoria.backend.domain.user.dto.UserDto;
import ac.mju.memoria.backend.domain.user.entity.User;
import ac.mju.memoria.backend.domain.user.repository.UserRepository;
import ac.mju.memoria.backend.system.exception.model.ErrorCode;
import ac.mju.memoria.backend.system.exception.model.RestException;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import ac.mju.memoria.backend.system.security.utility.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional(readOnly = true)
    public AuthDto.SignInResponse signIn(AuthDto.SignInRequest request) {
        var found = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RestException(ErrorCode.GLOBAL_NOT_FOUND));
        
        if(!passwordEncoder.matches(request.getPassword(), found.getPassword()))
            throw new RestException(ErrorCode.AUTH_PASSWORD_NOT_MATCH);


        var token = jwtTokenProvider.issueToken(
                UserDetails.from(found),
                24 * 7L // expires in 1 week
        );

        return AuthDto.SignInResponse.of(
                token.getTokenString(),
                UserDto.UserResponse.from(found),
                token.getExpireAt()
        );
    }

    @Transactional
    public UserDto.UserResponse signUp(AuthDto.SignUpRequest request) {
        boolean isExisting = userRepository.existsByEmail(request.getEmail());
        if(isExisting)
            throw new RestException(ErrorCode.GLOBAL_ALREADY_EXIST);

        User toSave = request.toEntity(passwordEncoder);
        User saved = userRepository.save(toSave);

        return UserDto.UserResponse.from(saved);
    }

    @Transactional
    public AuthDto.EmailExistResponse checkEmailExist(AuthDto.EmailExistRequest request) {
       boolean exists = userRepository.existsByEmail(request.getEmail());
       return AuthDto.EmailExistResponse.from(exists);
    }
}
