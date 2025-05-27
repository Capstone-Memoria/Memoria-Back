package ac.mju.memoria.backend.domain.ai.networking;

/**
 * 네트워크 풀에서 관리되는 개별 노드를 나타내는 인터페이스입니다.
 * 각 노드는 고유한 URL을 가지며, 현재 사용 가능 상태를 관리할 수 있습니다.
 */
public interface Node {
    /**
     * 노드의 URL을 반환합니다.
     *
     * @return 노드의 URL 문자열
     */
    String getURL();

    /**
     * 노드의 현재 사용 가능 상태를 확인합니다.
     *
     * @return 노드가 사용 가능하면 true, 그렇지 않으면 false
     */
    Boolean isAvailable();

    /**
     * 노드의 사용 가능 상태를 설정합니다.
     *
     * @param available 노드의 새로운 사용 가능 상태 (true 또는 false)
     */
    void setAvailable(Boolean available);
}
