package ac.mju.memoria.backend.domain.notification.service;

import ac.mju.memoria.backend.domain.notification.dto.NotificationDto;
import ac.mju.memoria.backend.domain.notification.entity.Notification;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseEmitterService {

    private static final Long DEFAULT_TIMEOUT = 30L * 60 * 1000;
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String userEmail) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitters.put(userEmail, emitter);

        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data(userEmail + "connected"));
        } catch (IOException e) {
            emitters.remove(userEmail);
        }

        emitter.onTimeout(() -> emitters.remove(userEmail));
        emitter.onCompletion(() -> emitters.remove(userEmail));
        emitter.onError(e -> emitters.remove(userEmail));

        return emitter;
    }

    public void sendNotification(String userEmail, NotificationDto.Response response) {
        SseEmitter emitter = emitters.get(userEmail);
        if (Objects.nonNull(emitter)) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(response));
            } catch (IOException e) {
                emitters.remove(userEmail);
            }
        }
    }
}
