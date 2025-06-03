package ac.mju.memoria.backend.domain.ai.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ac.mju.memoria.backend.domain.ai.dto.NodeDto;
import ac.mju.memoria.backend.domain.ai.dto.QueueDto;
import ac.mju.memoria.backend.domain.ai.entity.AiNode;
import ac.mju.memoria.backend.domain.ai.entity.NodeType;
import ac.mju.memoria.backend.domain.ai.networking.DBNode;
import ac.mju.memoria.backend.domain.ai.networking.image.ImageNodePool;
import ac.mju.memoria.backend.domain.ai.networking.music.MusicNodePool;
import ac.mju.memoria.backend.domain.ai.repository.AiNodeRepository;
import ac.mju.memoria.backend.system.exception.model.ErrorCode;
import ac.mju.memoria.backend.system.exception.model.RestException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AiConfigService {
  private final AiNodeRepository aiNodeRepository;
  private final ImageNodePool imageNodePool;
  private final MusicNodePool musicNodePool;

  @PostConstruct
  private void addNodesToPools() {
    List<AiNode> nodes = aiNodeRepository.findAll();
    nodes.forEach(node -> {
      if (node.getNodeType() == NodeType.IMAGE) {
        imageNodePool.addNode(DBNode.from(node));
      } else if (node.getNodeType() == NodeType.MUSIC) {
        musicNodePool.addNode(DBNode.from(node));
      }
    });
  }

  @Transactional
  public NodeDto.Response createNode(NodeDto.CreateRequest request) {
    // URL 중복 검사
    if (aiNodeRepository.existsByUrl(request.getUrl())) {
      throw new RestException(ErrorCode.AI_NODE_URL_ALREADY_EXISTS);
    }

    AiNode aiNode = AiNode.from(request);
    AiNode savedNode = aiNodeRepository.save(aiNode);

    // 노드 풀에 새로운 노드 추가
    DBNode dbNode = DBNode.from(savedNode);
    if (savedNode.getNodeType() == NodeType.IMAGE) {
      imageNodePool.addNode(dbNode);
    } else if (savedNode.getNodeType() == NodeType.MUSIC) {
      musicNodePool.addNode(dbNode);
    }

    return NodeDto.Response.from(savedNode, true);
  }

  @Transactional(readOnly = true)
  public List<NodeDto.Response> getAllNodes() {
    List<AiNode> nodes = aiNodeRepository.findAll();

    return nodes.stream()
        .map(node -> NodeDto.Response.from(node, getNodeById(node.getId()).map(DBNode::getAvailable).orElse(true)))
        .toList();
  }

  private Optional<DBNode> getNodeById(Long id) {
    return imageNodePool.getNodeById(id)
        .or(() -> musicNodePool.getNodeById(id));
  }

  @Transactional
  public NodeDto.Response updateNode(Long nodeId, NodeDto.UpdateRequest request) {
    AiNode aiNode = findNodeById(nodeId);

    // 기존 노드 정보 저장 (풀에서 제거하기 위해)
    DBNode oldNode = DBNode.from(aiNode);

    // URL 변경 시 중복 검사
    if (request.getUrl() != null && !request.getUrl().equals(aiNode.getUrl())) {
      if (aiNodeRepository.existsByUrlAndIdNot(request.getUrl(), nodeId)) {
        throw new RestException(ErrorCode.AI_NODE_URL_ALREADY_EXISTS);
      }
    }

    aiNode.updateFromRequest(request);
    AiNode updatedNode = aiNodeRepository.save(aiNode);

    // 노드 풀에서 기존 노드 제거 후 새로운 노드 추가
    DBNode newNode = DBNode.from(updatedNode);
    if (updatedNode.getNodeType() == NodeType.IMAGE) {
      imageNodePool.removeNode(oldNode);
      imageNodePool.addNode(newNode);
    } else if (updatedNode.getNodeType() == NodeType.MUSIC) {
      musicNodePool.removeNode(oldNode);
      musicNodePool.addNode(newNode);
    }

    return NodeDto.Response.from(updatedNode, true);
  }

  @Transactional
  public void deleteNode(Long nodeId) {
    AiNode aiNode = findNodeById(nodeId);

    // 노드 풀에서 노드 제거
    DBNode nodeToRemove = DBNode.from(aiNode);
    if (aiNode.getNodeType() == NodeType.IMAGE) {
      imageNodePool.removeNode(nodeToRemove);
    } else if (aiNode.getNodeType() == NodeType.MUSIC) {
      musicNodePool.removeNode(nodeToRemove);
    }

    aiNodeRepository.delete(aiNode);
  }

  private AiNode findNodeById(Long nodeId) {
    return aiNodeRepository.findById(nodeId)
        .orElseThrow(() -> new RestException(ErrorCode.AI_NODE_NOT_FOUND));
  }

  @Transactional(readOnly = true)
  public int getImageNodeQueueSize() {
    return imageNodePool.getRequestQueueSize();
  }

  @Transactional(readOnly = true)
  public int getMusicNodeQueueSize() {
    return musicNodePool.getRequestQueueSize();
  }

  @Transactional(readOnly = true)
  public int getMusicNodePendingJobsCount() {
    return musicNodePool.getPendingJobsCount();
  }

  /**
   * 특정 다이어리 ID와 연관된 AI 요청들을 취소합니다.
   *
   * @param diaryId 취소할 요청의 다이어리 ID
   * @return 취소된 전체 요청 수
   */
  public int cancelAiRequestsByDiaryId(Long diaryId) {
    int cancelledImageRequests = imageNodePool.cancelRequestsByDiaryId(diaryId);
    int cancelledMusicRequests = musicNodePool.cancelRequestsByDiaryId(diaryId);

    int totalCancelled = cancelledImageRequests + cancelledMusicRequests;
    if (totalCancelled > 0) {
      log.info("Cancelled {} AI requests for diary ID: {} (Image: {}, Music: {})",
          totalCancelled, diaryId, cancelledImageRequests, cancelledMusicRequests);
    }

    return totalCancelled;
  }

  /**
   * 뮤직 노드 풀의 큐 상태를 조회합니다.
   * 
   * @return 큐 상태 정보
   */
  @Transactional(readOnly = true)
  public QueueDto.QueueListResponse getMusicQueueStatus() {
    return musicNodePool.getQueueStatus();
  }

  /**
   * 뮤직 큐 아이템의 순서를 변경합니다.
   * 
   * @param uuid        이동할 아이템의 UUID
   * @param newPosition 새로운 위치
   * @return 순서 변경 성공 여부
   */
  public boolean reorderMusicQueueItem(UUID uuid, int newPosition) {
    boolean result = musicNodePool.reorderQueueItem(uuid, newPosition);
    if (result) {
      log.info("Successfully reordered music queue item {} to position {}", uuid, newPosition);
    } else {
      log.warn("Failed to reorder music queue item {} to position {}", uuid, newPosition);
    }
    return result;
  }

  /**
   * 뮤직 큐에서 특정 아이템을 삭제합니다.
   * 
   * @param uuid 삭제할 아이템의 UUID
   * @return 삭제 성공 여부
   */
  public boolean deleteMusicQueueItem(UUID uuid) {
    boolean result = musicNodePool.deleteQueueItem(uuid);
    if (result) {
      log.info("Successfully deleted music queue item {}", uuid);
    } else {
      log.warn("Failed to delete music queue item {}", uuid);
    }
    return result;
  }
}
