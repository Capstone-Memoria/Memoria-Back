package ac.mju.memoria.backend.domain.ai.networking;

/**
 * 비동기 요청에 대한 응답을 처리하는 핸들러의 인터페이스입니다.
 * 이 인터페이스는 단일 메서드 `handleResponse`를 정의하며, 이는 응답 본문을 인자로 받습니다.
 *
 * @param <RES> 응답 객체의 타입
 */
@FunctionalInterface
public interface ResponseHandler<RES> {
    /**
     * 수신된 응답을 처리합니다.
     *
     * @param responseBody 처리할 응답 객체
     */
    void handleResponse(RES responseBody);
}
