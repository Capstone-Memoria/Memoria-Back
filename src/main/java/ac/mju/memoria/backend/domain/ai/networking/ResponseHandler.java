package ac.mju.memoria.backend.domain.ai.networking;

@FunctionalInterface
public interface ResponseHandler {
    void handleResponse(String responseBody);
}
