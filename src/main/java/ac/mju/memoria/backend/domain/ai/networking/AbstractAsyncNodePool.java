package ac.mju.memoria.backend.domain.ai.networking;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * 비동기 요청을 처리하는 노드 풀의 추상 클래스입니다.
 * 이 클래스는 노드 목록을 관리하고, 요청 큐와 보류 중인 작업 맵을 사용하여 요청을 처리합니다.
 * 요청은 비동기적으로 처리되며, 각 요청은 사용 가능한 노드에 할당됩니다.
 *
 * @param <REQ> 요청 객체의 타입
 * @param <RES> 응답 객체의 타입
 */
@Slf4j
public abstract class AbstractAsyncNodePool<REQ, RES> implements NodePool<REQ, RES> {
    private final static int POLLING_INTERVAL_MS = 500; // 요청 큐를 폴링하는 간격 (밀리초 단위)

    private final List<Node> nodes = new ArrayList<>();

    private final Queue<NodePoolQueueItem<REQ, RES>> requestQueue = new ConcurrentLinkedQueue<>();
    private final Map<String, NodePoolQueueItem<REQ, RES>> pendingJobs = new ConcurrentHashMap<>();

    /**
     * requestQueue에 있는 요청을 pendingJobs로 이동시키는 작업을 수행하는 ExecutorService 입니다.
     */
    private final ExecutorService requestExecutor = Executors.newSingleThreadExecutor();

    private final ExecutorService threadPool = Executors.newVirtualThreadPerTaskExecutor();

    /**
     * 노드 풀을 시작합니다.
     * 요청 큐 처리를 시작합니다.
     */
    @Override
    public void start() {
        requestExecutor.submit(this::processRequestQueue);
    }

    /**
     * 노드 풀을 중지합니다.
     * 모든 ExecutorService를 종료합니다.
     */
    @Override
    public void stop() {
        requestExecutor.shutdownNow();
        threadPool.shutdownNow();
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
     * 현재 요청 큐의 크기를 반환합니다.
     *
     * @return 현재 큐에 대기 중인 요청의 수
     */
    public int getRequestQueueSize() {
        return requestQueue.size();
    }

    /**
     * 현재 처리 중인 작업의 수를 반환합니다.
     *
     * @return 현재 처리 중인 작업의 수
     */
    public int getPendingJobsCount() {
        return pendingJobs.size();
    }

    /**
     * 특정 다이어리 ID와 연관된 대기 중인 요청과 처리 중인 작업을 취소합니다.
     *
     * @param diaryId 취소할 요청의 다이어리 ID
     * @return 취소된 요청의 수
     */
    public int cancelRequestsByDiaryId(Long diaryId) {
        if (diaryId == null) {
            return 0;
        }

        List<NodePoolQueueItem<REQ, RES>> itemsToCancel = new ArrayList<>();
        int cancelledCount = 0;

        // 대기 중인 요청 큐에서 해당 diaryId를 가진 요청들을 찾아서 제거
        requestQueue.removeIf(item -> {
            if (diaryId.equals(item.getDiaryId())) {
                itemsToCancel.add(item);
                return true;
            }
            return false;
        });

        // 처리 중인 작업에서 해당 diaryId를 가진 작업들을 찾아서 제거
        List<String> jobIdsToCancel = new ArrayList<>();
        pendingJobs.entrySet().removeIf(entry -> {
            if (diaryId.equals(entry.getValue().getDiaryId())) {
                jobIdsToCancel.add(entry.getKey());
                itemsToCancel.add(entry.getValue());
                return true;
            }
            return false;
        });

        // 취소된 요청들의 Future를 cancel
        itemsToCancel.forEach(item -> {
            item.getResponse().cancel(true);
            if (item.getRequestProcessor() != null) {
                item.getRequestProcessor().setAvailable(true);
            }
        });

        cancelledCount = itemsToCancel.size();

        if (cancelledCount > 0) {
            log.info("Cancelled {} music generation requests for diary ID: {}", cancelledCount, diaryId);
        }

        return cancelledCount;
    }

    /**
     * 요청을 제출하고 즉시 Future를 반환합니다.
     * 응답은 Future를 통해 비동기적으로 받을 수 있습니다.
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
        return toQueue.getResponse();
    }

    /**
     * 요청을 제출하고, 응답이 도착하면 제공된 핸들러를 통해 처리합니다.
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
     * 다이어리 ID와 함께 요청을 제출하고, 응답이 도착하면 제공된 핸들러를 통해 처리합니다.
     *
     * @param request         제출할 요청
     * @param diaryId         연관된 다이어리 ID
     * @param responseHandler 응답을 처리할 핸들러
     */
    public void submitRequestWithDiaryId(REQ request, Long diaryId, ResponseHandler<RES> responseHandler) {
        if (nodes.isEmpty()) {
            log.error("No available nodes to process the request");
            return;
        }

        NodePoolQueueItem<REQ, RES> toQueue = NodePoolQueueItem.from(request, diaryId);
        toQueue.setResponseHandler(responseHandler);
        requestQueue.add(toQueue);
    }

    /**
     * 실제 요청을 특정 노드로 전송하는 로직을 정의하는 추상 메서드입니다.
     * 이 메서드는 하위 클래스에서 구현되어야 합니다.
     *
     * @param request 전송할 요청
     * @param node    요청을 처리할 노드
     * @return 요청을 식별하는 고유한 작업 ID (job ID)
     */
    protected abstract String handleSubmitRequest(REQ request, Node node);

    /**
     * 요청 큐를 지속적으로 처리하는 내부 메서드입니다.
     * 사용 가능한 노드가 있고 큐에 요청이 있으면, 해당 요청을 가져와 비동기적으로 처리합니다.
     * 요청 처리 중 예외 발생 시 재시도 로직을 포함합니다.
     */
    private void processRequestQueue() {
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

            NodePoolQueueItem<REQ, RES> toProcess = requestQueue.poll();

            if (toProcess == null) {
                continue;
            }

            threadPool.submit(() -> {
                try {
                    foundAvailableNode.get().setAvailable(false);
                    toProcess.setRequestProcessor(foundAvailableNode.get());
                    toProcess.setRequestedAt(LocalDateTime.now());

                    String jobId = handleSubmitRequest(toProcess.getRequest(), foundAvailableNode.get());

                    pendingJobs.put(jobId, toProcess);
                } catch (Exception e) {
                    if (toProcess.getRetryCountDown() > 0) {
                        toProcess.setRetryCountDown(toProcess.getRetryCountDown() - 1);
                        requestQueue.add(toProcess); // Re-add to the queue for retry
                        log.warn("Request failed, retrying. Remaining retries: {}", toProcess.getRetryCountDown());
                    } else {
                        toProcess.getResponse().completeExceptionally(e);
                        log.error("Request failed after retries. Error: {}", e.getMessage());
                    }
                }
            });
        }
    }

    /**
     * 특정 노드에서 특정 작업 ID에 대한 실제 결과 데이터를 가져오는 추상 메서드입니다.
     * 이 메서드는 하위 클래스에서 구현되어야 합니다.
     *
     * @param jobId 가져올 결과에 해당하는 작업 ID
     * @param node  작업이 처리된 노드
     * @return 작업의 결과 데이터 (타입 RES)
     */
    protected abstract RES fetchJobResult(String jobId, Node node);

    /**
     * 작업이 완료되었을 때 호출되는 메서드입니다.
     * 이 메서드는 작업 ID를 사용하여 보류 중인 작업을 찾아 응답을 처리합니다.
     *
     * @param jobId 완료된 작업의 ID
     */
    protected void onJobCompleted(String jobId) {
        NodePoolQueueItem<REQ, RES> pendingJob = pendingJobs.get(jobId);
        if (pendingJob == null) {
            return;
        }

        Node processingNode = pendingJob.getRequestProcessor();
        RES response = fetchJobResult(jobId, processingNode);

        pendingJob.getResponse().complete(response);
        if (pendingJob.getResponseHandler() != null) {
            pendingJob.getResponseHandler().handleResponse(response);
        }

        processingNode.setAvailable(true);
        pendingJobs.remove(jobId);
    }
}
