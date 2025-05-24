package ac.mju.memoria.backend.domain.ai.networking;


import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;

@RequiredArgsConstructor
public abstract class AbstractNode<REQ> implements Node {
    protected String url;
    protected Boolean isAvailable = true;
    protected final OkHttpClient client;

    @Override
    public String getURL() {
        return url;
    }

    @Override
    public Boolean getIsAvailable() {
        return isAvailable;
    }

    protected abstract void sendRequest(REQ req, ResponseHandler handler);
}
