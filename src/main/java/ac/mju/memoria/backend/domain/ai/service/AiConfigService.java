package ac.mju.memoria.backend.domain.ai.service;

import ac.mju.memoria.backend.domain.ai.dto.NodeDto;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
}
