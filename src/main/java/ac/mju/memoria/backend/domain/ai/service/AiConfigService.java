package ac.mju.memoria.backend.domain.ai.service;

import ac.mju.memoria.backend.domain.ai.dto.NodeDto;
import ac.mju.memoria.backend.domain.ai.networking.DefaultNode;
import ac.mju.memoria.backend.domain.ai.networking.Node;
import ac.mju.memoria.backend.system.exception.model.ErrorCode;
import ac.mju.memoria.backend.system.exception.model.RestException;
import ac.mju.memoria.backend.system.security.model.UserDetails;
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

  private final String ADMIN_KEY = "admin";

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

  public void addImageNode(NodeDto.CreateRequest request, UserDetails user) {
    if(user.getKey().equals(ADMIN_KEY)) {
      throw new RestException(ErrorCode.AUTH_FORBIDDEN);
    }

    imageNodePool.addNode(DefaultNode.fromURL(request.getUrl()));
  }

  public void addMusicNode(NodeDto.CreateRequest request, UserDetails user) {
    if(user.getKey().equals(ADMIN_KEY)) {
      throw new RestException(ErrorCode.AUTH_FORBIDDEN);
    }

    musicNodePool.addNode(DefaultNode.fromURL(request.getUrl()));
  }

  public void deleteImageNode(String url, UserDetails user) {
    if(user.getKey().equals(ADMIN_KEY)) {
      throw new RestException(ErrorCode.AUTH_FORBIDDEN);
    }

    Node nodeToRemove = imageNodePool.getNodes().stream()
        .filter(node -> node.getURL().equals(url))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Node not found with URL: " + url));
    imageNodePool.removeNode(nodeToRemove);
  }

  public void deleteMusicNode(String url, UserDetails user) {
    if(user.getKey().equals(ADMIN_KEY)) {
      throw new RestException(ErrorCode.AUTH_FORBIDDEN);
    }

    Node nodeToRemove = musicNodePool.getNodes().stream()
        .filter(node -> node.getURL().equals(url))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Node not found with URL: " + url));
    musicNodePool.removeNode(nodeToRemove);
  }
}
