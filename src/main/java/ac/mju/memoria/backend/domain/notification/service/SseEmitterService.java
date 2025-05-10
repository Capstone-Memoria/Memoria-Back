package ac.mju.memoria.backend.domain.notification.service;

import ac.mju.memoria.backend.domain.notification.dto.NotificationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
// import javax.annotation.PreDestroy; // Spring Boot 3.x 에서는 jakarta.annotation.PreDestroy

@Service
public class SseEmitterService {

    private static final Logger log = LoggerFactory.getLogger(SseEmitterService.class);
    private static final Long DEFAULT_TIMEOUT = 60L * 60 * 1000; // 60 minutes
    private static final Long HEARTBEAT_INTERVAL = 25L * 60 * 1000; // 25 minutes, timeout보다 짧게 설정
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1); // 스레드 풀 크기는 상황에 맞게 조절

    public SseEmitter subscribe(String userEmail) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitters.put(userEmail, emitter);

        // 초기 연결 메시지
        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data(userEmail + " connected. SSE Connection established."));
            log.info("SSE connected for user: {}", userEmail);
        } catch (IOException e) {
            log.error("Error sending initial connect event for user: {}", userEmail, e);
            emitters.remove(userEmail);
            emitter.completeWithError(e);
            return emitter;
        }

        // 주기적인 heartbeat 전송 스케줄링
        Runnable heartbeatTask = () -> {
            if (emitters.containsKey(userEmail)) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("heartbeat")
                            .data("ping"));
                    log.debug("Sent heartbeat to user: {}", userEmail);
                } catch (IOException e) {
                    log.warn("Error sending heartbeat to user: {}, removing emitter.", userEmail, e);
                    emitters.remove(userEmail);
                    // emitter.complete(); // 필요에 따라 명시적 완료
                } catch (IllegalStateException e) {
                    log.warn("Emitter for user {} is already completed or request processing has finished. Removing emitter.", userEmail, e);
                    emitters.remove(userEmail);
                }
            } else {
                log.debug("Emitter for user {} no longer exists. Heartbeat task will not run.", userEmail);
                // 이 경우 해당 스케줄된 작업 자체를 취소하는 것이 이상적이지만,
                // ScheduledExecutorService에서 특정 작업을 직접 취소하는 것은 복잡할 수 있음.
                // emitters 맵에서 제거되었으므로 다음 실행 시에는 동작하지 않음.
            }
        };

        scheduler.scheduleAtFixedRate(heartbeatTask, HEARTBEAT_INTERVAL, HEARTBEAT_INTERVAL, TimeUnit.MILLISECONDS);

        emitter.onTimeout(() -> {
            log.info("SSE timeout for user: {}", userEmail);
            emitters.remove(userEmail);
        });
        emitter.onCompletion(() -> {
            log.info("SSE completed for user: {}", userEmail);
            emitters.remove(userEmail);
        });
        emitter.onError(e -> {
            log.error("SSE error for user: {}", userEmail, e);
            emitters.remove(userEmail);
        });

        return emitter;
    }

    public void sendNotification(String userEmail, NotificationDto.Response response) {
        SseEmitter emitter = emitters.get(userEmail);
        if (Objects.nonNull(emitter)) {
            try {
                emitter.send(SseEmitter.event()
                        .name("message")
                        .data(response));
                log.info("Sent notification to user: {}", userEmail);
            } catch (IOException e) {
                log.error("Error sending notification to user: {}, removing emitter.", userEmail, e);
                emitters.remove(userEmail);
                emitter.completeWithError(e);
            } catch (IllegalStateException e) {
                log.warn("Emitter for user {} is already completed or request processing has finished when sending notification. Removing emitter.", userEmail, e);
                emitters.remove(userEmail);
            }
        } else {
            log.warn("No active SSE emitter for user: {} to send notification.", userEmail);
        }
    }

    // 애플리케이션 종료 시 스케줄러를 종료합니다.
    // Spring Boot 환경에서는 @PreDestroy 어노테이션을 사용하여 빈 소멸 시 호출되도록 할 수 있습니다.
    // @PreDestroy
    // public void destroy() {
    //     log.info("Shutting down SSE heartbeat scheduler.");
    //     scheduler.shutdown();
    //     try {
    //         if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
    //             scheduler.shutdownNow();
    //         }
    //     } catch (InterruptedException e) {
    //         scheduler.shutdownNow();
    //         Thread.currentThread().interrupt();
    //     }
    // }
}
