package ac.mju.memoria.backend.domain.diarybook.service;

import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ac.mju.memoria.backend.domain.diarybook.entity.AICharacter;
import ac.mju.memoria.backend.domain.diarybook.entity.enums.AICharacterType;
import ac.mju.memoria.backend.domain.diarybook.repository.AICharacterRepository;
import ac.mju.memoria.backend.domain.file.entity.ProfileImage;
import ac.mju.memoria.backend.domain.file.handler.FileSystemHandler;
import ac.mju.memoria.backend.domain.file.repository.AttachedFileRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DefaultCharacterLoader implements CommandLineRunner {
    private final AICharacterRepository aiCharacterRepository;
    private final FileSystemHandler fileSystemHandler;
    private final AttachedFileRepository attachedFileRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (aiCharacterRepository.count() == 0) {
            addCoony();
        }
    }

    private void addCoony() throws Exception {
        AICharacter coony = AICharacter.builder()
                .name("솜토끼 쿠니")
                .type(AICharacterType.DEFAULT)
                .feature("""
                        이름: 솜토끼 쿠니 (Coony)

                        종류: 토끼 + 솜사탕 하이브리드 캐릭터

                        성별: 무성별(사용자 감정에 따라 중성적으로 표현 가능)

                        나이: 인간 나이로 약 5살 정도의 순수한 느낌

                        컨셉 키워드: 말랑말랑, 순수, 감성, 위로, 몽글몽글
                        """)
                .accent("""
                        5살 정도의 순수한 아이의 말투, 반말 사용
                        예시:
                        - "오늘은 무슨 일이 있었어? 쿠니랑 얘기해줄래?"
                        - "우와~ 정말 대단한 일이네! 쿠니도 기쁘다!"
                        - "힘들 때는 쿠니가 안아줄게. 말랑말랑해져~"
                        """)
                .build();
        AICharacter saved = aiCharacterRepository.save(coony);
        ClassPathResource profileImageResource = new ClassPathResource("images/coony.png");

        ProfileImage profileImage = ProfileImage.builder()
                .id(UUID.randomUUID().toString())
                .fileName("coony.png")
                .aiCharacter(saved)
                .size(profileImageResource.contentLength())
                .build();

        attachedFileRepository.save(profileImage);

        fileSystemHandler.saveFile(profileImageResource, profileImage);
    }
}
