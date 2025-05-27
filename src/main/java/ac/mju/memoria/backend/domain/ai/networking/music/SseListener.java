package ac.mju.memoria.backend.domain.ai.networking.music;

import ac.mju.memoria.backend.domain.ai.networking.Node;

@FunctionalInterface
public interface SseListener<RES> {
    void handleResponse(Node node, RES response);
}
