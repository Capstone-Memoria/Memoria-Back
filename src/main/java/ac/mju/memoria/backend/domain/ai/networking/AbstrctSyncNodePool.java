package ac.mju.memoria.backend.domain.ai.networking;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 보내는 요청이 동기적으로 처리되는 서비스에 대한 노드 풀
 */
public abstract class AbstrctSyncNodePool<REQ> implements NodePool<REQ> {
    private final ExecutorService threadPool = Executors.newVirtualThreadPerTaskExecutor();
    private final List<Node> nodes = new ArrayList<>();

    @Override
    public void addNode(@NonNull Node node) {
        nodes.add(node);
    }

    @Override
    public void removeNode(@NonNull Node node) {
        nodes.remove(node);
    }

    @Override
    public void sendRequest(REQ request) {
        var availableNodes = nodes.stream()
                .filter(Node::getIsAvailable)
                .toList();

        if (availableNodes.isEmpty()) {
            throw new IllegalStateException("No available nodes to send request");
        }
    }
}
