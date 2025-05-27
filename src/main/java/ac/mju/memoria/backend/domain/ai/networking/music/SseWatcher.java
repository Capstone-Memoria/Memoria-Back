package ac.mju.memoria.backend.domain.ai.networking.music;

import ac.mju.memoria.backend.domain.ai.networking.Node;

public interface SseWatcher<RES> {
    void init();

    void addListener(SseListener<RES> listener);

    void removeListener(SseListener<RES> listener);

    void addNode(Node node);

    void removeNode(Node node);
}
