package ac.mju.memoria.backend.system.security.model;

import ac.mju.memoria.backend.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@Builder
public class UserDetails extends AuthDetails{
    private final User user;

    @Override
    public String getKey() {
        return user.getEmail();
    }

    public static UserDetails from(User user) {
        return UserDetails.builder()
                .user(user)
                .build();
    }
}
