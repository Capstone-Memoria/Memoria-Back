package ac.mju.memoria.backend.domain.diarybook.service.handler;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 임시 업로드 처리된 스티커 이미지들을 보관 / 관리 하는 클래스
 */
public class StickerImageHolder {
    private static final String TMP_PATH = System.getProperty("java.io.tmpdir");
    private static final Path STICKER_SAVE_DIR = Path.of(TMP_PATH, "stickers");

    private final List<CachedStickerImage> cachedStickerImages = new ArrayList<>();
    private final ScheduledExecutorService expiredImageCleaner = Executors.newScheduledThreadPool(1);


    public String hold(MultipartFile file) throws IOException {
        CachedStickerImage newStickerImage = CachedStickerImage.ofNew();
        addImage(file.getInputStream(), newStickerImage);

        long delayMillis = Duration.between(LocalDateTime.now(), newStickerImage.expiresAt).toMillis();

        expiredImageCleaner.schedule(() -> deleteImage(newStickerImage), delayMillis, TimeUnit.MILLISECONDS);

        return newStickerImage.getUuid();
    }

    /**
     * UUID로 스티커 이미지 파일을 찾습니다.
     * @param uuid 스티커 이미지의 UUID
     * @return Optional로 감싼 스티커 이미지 파일의 byte 배열
     */
    public Optional<byte[]> findImage(String uuid) {
        return cachedStickerImages.stream()
                .filter(stickerImage -> stickerImage.getUuid().equals(uuid))
                .findFirst()
                .map(stickerImage -> {
                    try {
                        return Files.readAllBytes(toSavedFilePath(uuid));
                    } catch (Exception e) {
                        return null;
                    }
                });
    }

    @SneakyThrows
    private void addImage(InputStream inputStream, CachedStickerImage stickerImage) {
        if (!Files.exists(STICKER_SAVE_DIR)) {
            Files.createDirectories(STICKER_SAVE_DIR);
        }

        Path savedFilePath = toSavedFilePath(stickerImage.getUuid());
        File targetFile = savedFilePath.toFile();

        if (targetFile.exists()) {
            Files.delete(savedFilePath); // 기존 파일이 있다면 삭제
        }

        try (OutputStream outputStream = new FileOutputStream(targetFile)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        cachedStickerImages.add(stickerImage);
    }

    @SneakyThrows
    private void deleteImage(CachedStickerImage stickerImage) {
        cachedStickerImages.remove(stickerImage);
        Files.deleteIfExists(toSavedFilePath(stickerImage.getUuid()));
    }

    private Path toSavedFilePath(String uuid) {
        return STICKER_SAVE_DIR.resolve(uuid);
    }



    @RequiredArgsConstructor
    @Data
    @Builder
    private static class CachedStickerImage {
        private final String uuid;
        private final LocalDateTime createdAt;
        private final LocalDateTime expiresAt;

        public static CachedStickerImage ofNew() {
            String uuid = UUID.randomUUID().toString();
            LocalDateTime nowDateTime = LocalDateTime.now();

            return CachedStickerImage.builder()
                    .uuid(uuid)
                    .createdAt(nowDateTime)
                    .expiresAt(nowDateTime.plusMinutes(3)) // 3분 후 만료
                    .build();
        }
    }
}
