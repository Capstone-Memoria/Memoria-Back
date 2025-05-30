package ac.mju.memoria.backend.domain.ai.repository;

import ac.mju.memoria.backend.domain.ai.entity.AiNode;
import ac.mju.memoria.backend.domain.ai.entity.NodeType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AiNodeRepository extends JpaRepository<AiNode, Long> {
  boolean existsByUrl(String url);

  boolean existsByUrlAndIdNot(String url, Long id);
}