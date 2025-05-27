package ac.mju.memoria.backend.domain.ai.networking;

import java.util.concurrent.Future;

/**
 * 여러 노드에 걸쳐 요청을 분산하고 관리하는 NodePool의 인터페이스입니다.
 * NodePool은 시작 및 중지, 노드 추가 및 제거, 요청 제출 기능을 제공합니다.
 *
 * @param <REQ> 요청 객체의 타입
 * @param <RES> 응답 객체의 타입
 */
public interface NodePool<REQ, RES> {
    /**
     * NodePool을 시작합니다.
     * NodePool이 요청을 처리할 준비를 갖추도록 초기화 작업을 수행합니다.
     */
    void start();

    /**
     * NodePool을 중지합니다.
     * NodePool이 사용 중인 리소스를 정리하고, 진행 중인 작업을 안전하게 종료합니다.
     */
    void stop();

    /**
     * NodePool에 새로운 노드를 추가합니다.
     *
     * @param node 추가할 노드 객체
     */
    void addNode(Node node);

    /**
     * NodePool에서 기존 노드를 제거합니다.
     *
     * @param node 제거할 노드 객체
     */
    void removeNode(Node node);

    /**
     * 요청을 NodePool에 제출합니다.
     * 이 메서드는 즉시 Future를 반환하며, 실제 응답은 비동기적으로 처리될 수 있습니다.
     *
     * @param request 제출할 요청 객체
     * @return 응답을 나타내는 Future 객체
     */
    Future<RES> submitRequest(REQ request);

    /**
     * 요청을 NodePool에 제출하고, 응답을 처리할 콜백 핸들러를 등록합니다.
     * 이 메서드는 응답이 준비되었을 때 제공된 ResponseHandler를 통해 응답을 전달합니다.
     *
     * @param request         제출할 요청 객체
     * @param responseHandler 응답을 처리할 핸들러
     */
    void submitRequest(REQ request, ResponseHandler<RES> responseHandler);
}
