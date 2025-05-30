package ac.mju.memoria.backend.domain.ai.networking;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * 동기 요청을 처리하는 노드 풀의 추상 클래스입니다.
 * 이 클래스는 노드 목록을 관리하고, 요청 큐를 사용하여 순차적으로 요청을 처리합니다.
 * 요청은 동기적으로 처리되며, 각 요청은 사용 가능한 노드에 할당됩니다.
 *
 * @param <REQ> 요청 객체의 타입
 * @param <RES> 응답 객체의 타입
 */
@Slf4j
public abstract class AbstractSyncNodePool<REQ, RES> implements NodePool<REQ, RES> {
    private final List<Node> nodes = new ArrayList<>();

    private final Queue<NodePoolQueueItem<REQ, RES>> requestQueue = new ConcurrentLinkedQueue<>();

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final ExecutorService threadPool = Executors.newVirtualThreadPerTaskExecutor();

    /**
     * 노드 풀을 시작합니다.
     * 요청 큐 처리를 시작하고, 노드 풀이 시작되었음을 로그로 남깁니다.
     */
    @Override
    public void start() {
        executor.submit(this::processQueue);
        log.info("Node pool started with {} nodes", nodes.size());
    }

    /**
     * 노드 풀을 중지합니다.
     * 모든 ExecutorService를 종료하고, 노드 풀이 중지되었음을 로그로 남깁니다.
     */
    @Override
    public void stop() {
        executor.shutdownNow();
        threadPool.shutdownNow();
        log.info("Node pool stopped");
    }

    /**
     * 노드 풀에 노드를 추가합니다.
     *
     * @param node 추가할 노드 (null이 아니어야 함)
     */
    @Override
    public void addNode(@NonNull Node node) {
        nodes.add(node);
    }

    /**
     * 노드 풀에서 노드를 제거합니다.
     *
     * @param node 제거할 노드 (null이 아니어야 함)
     */
    @Override
    public void removeNode(@NonNull Node node) {
        nodes.remove(node);
    }

    /**
     * 노드 풀에 포함된 모든 노드를 반환합니다.
     *
     * @return 노드 풀에 포함된 모든 노드 목록
     */
    @Override
    public List<Node> getNodes() {
        return nodes;
    }

    /**
     * 요청을 제출하고 즉시 Future를 반환합니다.
     * 요청은 큐에 추가되어 순차적으로 처리됩니다.
     *
     * @param request 제출할 요청
     * @return 응답을 받을 수 있는 Future 객체
     */
    @Override
    public Future<RES> submitRequest(REQ request) {
        if (nodes.isEmpty()) {
            throw new IllegalStateException("No available nodes to process the request");
        }

        NodePoolQueueItem<REQ, RES> toQueue = NodePoolQueueItem.from(request);
        requestQueue.add(toQueue);
        return toQueue.getResponse();
    }

    /**
     * 요청을 제출하고, 응답이 도착하면 제공된 핸들러를 통해 처리합니다.
     * 요청은 큐에 추가되어 순차적으로 처리됩니다.
     *
     * @param request         제출할 요청
     * @param responseHandler 응답을 처리할 핸들러
     */
    @Override
    public void submitRequest(REQ request, ResponseHandler<RES> responseHandler) {
        if (nodes.isEmpty()) {
            log.error("No available nodes to process the request");
            return;
        }

        NodePoolQueueItem<REQ, RES> toQueue = NodePoolQueueItem.from(request);
        toQueue.setResponseHandler(responseHandler);
        requestQueue.add(toQueue);
    }

    /**
     * 실제 동기 요청을 특정 노드에서 처리하는 로직을 정의하는 추상 메서드입니다.
     * 이 메서드는 하위 클래스에서 구현되어야 합니다.
     *
     * @param request 처리할 요청
     * @param node    요청을 처리할 노드
     * @return 처리된 응답 객체
     */
    protected abstract RES handleRequest(REQ request, Node node);

    /**
     * 요청 큐를 지속적으로 처리하는 내부 메서드입니다.
     * 사용 가능한 노드가 있고 큐에 요청이 있으면, 해당 요청을 가져와 동기적으로 처리합니다.
     * 요청 처리 중 예외 발생 시 재시도 로직을 포함하며, 성공 또는 실패 시 로그를 남깁니다.
     */
    private void processQueue() {
        while (true) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                log.error("Queue processing interrupted: {}", e.getMessage());
                Thread.currentThread().interrupt(); // Restore the interrupted status
                break; // Exit the loop if interrupted
            }

            Optional<Node> foundAvailableNode = nodes.stream()
                    .filter(Node::isAvailable)
                    .findFirst();

            if (foundAvailableNode.isEmpty()) {
                continue;
            }

            NodePoolQueueItem<REQ, RES> queueItem = requestQueue.poll();

            if (queueItem == null) {
                continue;
            }

            threadPool.submit(() -> {
                try {
                    foundAvailableNode.get().setAvailable(false);
                    queueItem.setRequestProcessor(foundAvailableNode.get());
                    RES res = handleRequest(queueItem.getRequest(), foundAvailableNode.get());
                    queueItem.getResponse().complete(res);
                    if (queueItem.getResponseHandler() != null) {
                        queueItem.getResponseHandler().handleResponse(res);
                    }
                } catch (Exception e) {
                    if (queueItem.getRetryCountDown() > 0) {
                        queueItem.setRetryCountDown(queueItem.getRetryCountDown() - 1);
                        requestQueue.add(queueItem); // Re-add to the queue for retry
                        log.error("Request failed, retrying. Error: {}", e.getMessage());
                        log.warn("Request failed, retrying. Remaining retries: {}", queueItem.getRetryCountDown());
                    } else {
                        queueItem.getResponse().completeExceptionally(e);
                        log.error("Request failed. Error: {}", e.getMessage());
                    }
                } finally {
                    foundAvailableNode.get().setAvailable(true);
                }
            });

        }
    }
}
