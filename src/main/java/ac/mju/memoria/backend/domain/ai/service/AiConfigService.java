package ac.mju.memoria.backend.domain.ai.service;

import ac.mju.memoria.backend.domain.ai.dto.NodeDto;
import ac.mju.memoria.backend.domain.ai.networking.DefaultNode;
import ac.mju.memoria.backend.domain.ai.networking.Node;
import org.springframework.stereotype.Service;

import ac.mju.memoria.backend.domain.ai.networking.image.ImageNodePool;
import ac.mju.memoria.backend.domain.ai.networking.music.MusicNodePool;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiConfigService {
  private final ImageNodePool imageNodePool;
  private final MusicNodePool musicNodePool;

  public List<NodeDto.InfoResponse> getImageNodes() {
    return imageNodePool.getNodes().stream()
        .map(node -> NodeDto.InfoResponse.builder()
            .url(node.getURL())
            .available(node.isAvailable())
            .build())
        .collect(Collectors.toList());
  }

  public List<NodeDto.InfoResponse> getMusicNodes() {
    return musicNodePool.getNodes().stream()
        .map(node -> NodeDto.InfoResponse.builder()
            .url(node.getURL())
            .available(node.isAvailable())
            .build())
        .collect(Collectors.toList());
  }

  public void addImageNode(NodeDto.CreateRequest request) {
    imageNodePool.addNode(DefaultNode.fromURL(request.getUrl()));
  }

  public void addMusicNode(NodeDto.CreateRequest request) {
    musicNodePool.addNode(DefaultNode.fromURL(request.getUrl()));
  }

  public void deleteImageNode(String url) {
    Node nodeToRemove = imageNodePool.getNodes().stream()
        .filter(node -> node.getURL().equals(url))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Node not found with URL: " + url));
    imageNodePool.removeNode(nodeToRemove);
  }

  public void deleteMusicNode(String url) {
    Node nodeToRemove = musicNodePool.getNodes().stream()
        .filter(node -> node.getURL().equals(url))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Node not found with URL: " + url));
    musicNodePool.removeNode(nodeToRemove);
  }
}
