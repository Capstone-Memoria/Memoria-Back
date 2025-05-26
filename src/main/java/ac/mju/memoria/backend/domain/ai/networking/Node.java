package ac.mju.memoria.backend.domain.ai.networking;

/**
 * 노드의 인터페이스 작성
 */
public interface Node {
    String getURL();

    Boolean isAvailable();

    void setAvailable(Boolean available);
}
