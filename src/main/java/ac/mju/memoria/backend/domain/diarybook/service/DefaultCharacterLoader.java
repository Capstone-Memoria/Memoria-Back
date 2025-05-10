package ac.mju.memoria.backend.domain.diarybook.service;

import ac.mju.memoria.backend.domain.diarybook.entity.AICharacter;
import ac.mju.memoria.backend.domain.diarybook.entity.enums.AICharacterType;
import ac.mju.memoria.backend.domain.diarybook.repository.AICharacterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DefaultCharacterLoader implements CommandLineRunner {
    private final AICharacterRepository aiCharacterRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (aiCharacterRepository.count() == 0) {
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
                    .accent("5살 정도의 순수한 아이의 말투")
                    .build();

            aiCharacterRepository.save(coony);
        }
    }
}
