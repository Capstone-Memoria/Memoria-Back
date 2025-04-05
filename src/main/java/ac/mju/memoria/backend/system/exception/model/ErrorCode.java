package ac.mju.memoria.backend.system.exception.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    //Global
    GLOBAL_BAD_REQUEST(400, "올바르지 않은 요청입니다."),
    GLOBAL_NOT_FOUND(404, "요청한 사항을 찾을 수 없습니다."),
    GLOBAL_ALREADY_EXIST(400, "요청의 대상이 이미 존재합니다."),
    GLOBAL_METHOD_NOT_ALLOWED(405, "허용되지 않는 Method 입니다."),

    //Auth
    AUTH_PASSWORD_NOT_MATCH(401, "비밀번호가 올바르지 않습니다."),

    //User
    USER_ALREADY_JOINED(400, "이미 가입된 그룹입니다."),
    USER_NOT_FOUND(404, "존재하지 않는 사용자입니다."),

    //Other
    INTERNAL_SERVER_ERROR(500, "오류가 발생했습니다."), ;

    private final int statusCode;
    private final String message;
}
