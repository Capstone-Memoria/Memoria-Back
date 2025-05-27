package ac.mju.memoria.backend.domain.ai.networking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Node 인터페이스의 기본 구현체입니다.
 * 각 노드는 URL과 사용 가능 상태를 가집니다.
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder
public class DefaultNode implements Node {
    /**
     * 노드의 URL 주소입니다.
     */
    protected String URL;
    /**
     * 노드의 현재 사용 가능 상태입니다. true이면 사용 가능, false이면 사용 중 또는 비활성 상태를 의미합니다.
     */
    protected Boolean available = true;

    /**
     * 노드의 URL을 반환합니다.
     *
     * @return 노드의 URL 문자열
     */
    @Override
    public String getURL() {
        return URL;
    }

    /**
     * 노드의 사용 가능 상태를 반환합니다.
     *
     * @return 노드가 사용 가능하면 true, 그렇지 않으면 false
     */
    @Override
    public Boolean isAvailable() {
        return available;
    }

    /**
     * 주어진 URL로부터 새로운 DefaultNode 객체를 생성합니다.
     * 생성된 노드는 기본적으로 사용 가능한 상태로 초기화됩니다.
     *
     * @param URL 생성할 노드의 URL
     * @return 생성된 DefaultNode 객체
     */
    public static DefaultNode fromURL(String URL) {
        return DefaultNode.builder()
                .URL(URL)
                .available(true)
                .build();
    }
}
