package ac.mju.memoria.backend.domain.ai.networking;

import okhttp3.Response;

import java.util.concurrent.Future;

public interface NodePool<REQ, RES> {
    void addNode(Node node);

    void removeNode(Node node);

    Future<RES> submitRequest(REQ request);
}
