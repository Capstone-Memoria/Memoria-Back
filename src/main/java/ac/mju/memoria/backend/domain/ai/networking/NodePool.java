package ac.mju.memoria.backend.domain.ai.networking;

public interface NodePool<REQ> {
    void addNode(Node node);

    void removeNode(Node node);

    void sendRequest(REQ request);
}
