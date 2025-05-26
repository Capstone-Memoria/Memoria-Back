package ac.mju.memoria.backend.domain.ai.networking;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * 보내는 요청이 동기적으로 처리되는 서비스에 대한 노드 풀
 */
@Slf4j
public abstract class AbstrctSyncNodePool<REQ, RES> implements NodePool<REQ, RES> {
    private final List<Node> nodes = new ArrayList<>();

    private final Queue<NodePoolQueueItem<REQ, RES>> requestQueue = new ConcurrentLinkedQueue<>();

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final ExecutorService threadPool = Executors.newVirtualThreadPerTaskExecutor();

    public void start() {
        executor.submit(this::processQueue);
        log.info("Node pool started with {} nodes", nodes.size());
    }

    public void stop() {
        executor.shutdownNow();
        threadPool.shutdownNow();
        log.info("Node pool stopped");
    }

    @Override
    public void addNode(@NonNull Node node) {
        nodes.add(node);
    }

    @Override
    public void removeNode(@NonNull Node node) {
        nodes.remove(node);
    }

    @Override
    public Future<RES> submitRequest(REQ request) {
        NodePoolQueueItem<REQ, RES> toQueue = NodePoolQueueItem.from(request);
        requestQueue.add(toQueue);
        return toQueue.getResponse();
    }

    protected abstract RES handleRequest(REQ request, Node node);

    private void processQueue() {
        while (true) {
            Optional<Node> foundAvailableNode = nodes.stream()
                    .filter(Node::isAvailable)
                    .findFirst();

            if (foundAvailableNode.isEmpty()) {
                continue;
            }

            NodePoolQueueItem<REQ, RES> queueItem = requestQueue.poll();

            if(queueItem == null) {
                continue;
            }

            threadPool.submit(() -> {
                try {
                    RES res = handleRequest(queueItem.getRequest(), foundAvailableNode.get());
                    queueItem.getResponse().complete(res);
                    log.info("Processed request: {} with response: {}", queueItem.getUuid(), res);
                } catch (Exception e) {
                    if(queueItem.getRetryCountDown() > 0) {
                        queueItem.setRetryCountDown(queueItem.getRetryCountDown() - 1);
                        requestQueue.add(queueItem); // Re-add to the queue for retry
                        log.warn("Request failed, retrying: {}. Remaining retries: {}", queueItem.getUuid(), queueItem.getRetryCountDown());
                    } else {
                        queueItem.getResponse().completeExceptionally(e);
                        log.error("Request failed after retries: {}. Error: {}", queueItem.getUuid(), e.getMessage());
                    }
                }
            });

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                log.error("Queue processing interrupted: {}", e.getMessage());
                Thread.currentThread().interrupt(); // Restore the interrupted status
                break; // Exit the loop if interrupted
            }
        }
    }
}
