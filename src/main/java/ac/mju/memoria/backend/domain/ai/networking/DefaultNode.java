package ac.mju.memoria.backend.domain.ai.networking;


import lombok.*;
import okhttp3.OkHttpClient;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder
public class DefaultNode implements Node {
    protected String URL;
    protected Boolean available = true;

    @Override
    public String getURL() {
        return URL;
    }

    @Override
    public Boolean isAvailable() {
        return available;
    }

    public static DefaultNode fromURL(String URL) {
        return DefaultNode.builder()
                .URL(URL)
                .available(true)
                .build();
    }
}
