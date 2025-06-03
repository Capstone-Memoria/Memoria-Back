package ac.mju.memoria.backend.domain.ai.networking;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AbstractSyncNodePoolTest {

  private TestSyncNodePool nodePool;

  // 테스트용 AbstractSyncNodePool 구현체
  private static class TestSyncNodePool extends AbstractSyncNodePool<String, String> {
    @Override
    protected String handleRequest(String request, Node node) {
      return "result-" + request;
    }
  }

  @BeforeEach
  void setUp() {
    nodePool = new TestSyncNodePool();

    // 테스트용 노드 추가
    Node testNode = new Node() {
      private Boolean available = true;

      @Override
      public String getURL() {
        return "http://test-node";
      }

      @Override
      public Boolean isAvailable() {
        return available;
      }

      @Override
      public void setAvailable(Boolean available) {
        this.available = available;
      }
    };

    nodePool.addNode(testNode);
  }

  @Test
  @DisplayName("diaryId로 요청 취소 - 매칭되는 요청이 있는 경우")
  void cancelRequestsByDiaryId_WithMatchingRequests() {
    // given
    Long targetDiaryId = 123L;
    nodePool.submitRequestWithDiaryId("request1", targetDiaryId, response -> {
    });
    nodePool.submitRequestWithDiaryId("request2", targetDiaryId, response -> {
    });
    nodePool.submitRequestWithDiaryId("request3", 456L, response -> {
    });

    assertThat(nodePool.getRequestQueueSize()).isEqualTo(3);

    // when
    int cancelledCount = nodePool.cancelRequestsByDiaryId(targetDiaryId);

    // then
    assertThat(cancelledCount).isEqualTo(2);
    assertThat(nodePool.getRequestQueueSize()).isEqualTo(1);
  }

  @Test
  @DisplayName("diaryId로 요청 취소 - 매칭되는 요청이 없는 경우")
  void cancelRequestsByDiaryId_WithNoMatchingRequests() {
    // given
    Long targetDiaryId = 123L;
    nodePool.submitRequestWithDiaryId("request1", 456L, response -> {
    });
    nodePool.submitRequestWithDiaryId("request2", 789L, response -> {
    });

    assertThat(nodePool.getRequestQueueSize()).isEqualTo(2);

    // when
    int cancelledCount = nodePool.cancelRequestsByDiaryId(targetDiaryId);

    // then
    assertThat(cancelledCount).isEqualTo(0);
    assertThat(nodePool.getRequestQueueSize()).isEqualTo(2);
  }

  @Test
  @DisplayName("diaryId가 null인 경우")
  void cancelRequestsByDiaryId_WithNullDiaryId() {
    // given
    nodePool.submitRequestWithDiaryId("request1", 123L, response -> {
    });
    nodePool.submitRequestWithDiaryId("request2", 456L, response -> {
    });

    assertThat(nodePool.getRequestQueueSize()).isEqualTo(2);

    // when
    int cancelledCount = nodePool.cancelRequestsByDiaryId(null);

    // then
    assertThat(cancelledCount).isEqualTo(0);
    assertThat(nodePool.getRequestQueueSize()).isEqualTo(2);
  }

  @Test
  @DisplayName("빈 큐에서 요청 취소")
  void cancelRequestsByDiaryId_WithEmptyQueue() {
    // given
    assertThat(nodePool.getRequestQueueSize()).isEqualTo(0);

    // when
    int cancelledCount = nodePool.cancelRequestsByDiaryId(123L);

    // then
    assertThat(cancelledCount).isEqualTo(0);
    assertThat(nodePool.getRequestQueueSize()).isEqualTo(0);
  }
}