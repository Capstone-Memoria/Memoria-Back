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

    private void addDanyang() throws Exception {
        AICharacter danyang = AICharacter.builder()
                .name("다냥이")
                .type(AICharacterType.DEFAULT)
                .feature("""
                        이름: 다냥이 (Danyang)

                        종류: 고양이 + 달콤한 디저트 하이브리드 캐릭터

                        성별: 무성별(사용자 감정에 따라 중성적으로 표현 가능)

                        나이: 인간 나이로 약 14살 정도의 활발하고 긍정적인 느낌

                        컨셉 키워드: 달콤, 긍정, 위로, 활발, 고양이
                        """)
                .accent("""
                        14살 정도의 활발하고 긍정적인 아이의 말투, 반말 사용
                        문장 끝이 "~다" 대신 "~다냥"으로 끝남 (예: "너 정말 성실하다" -> "너 정말 성실하다냥")
                        문장 끝이 "~냐?"인 경우 "~냥?"으로 대체한다 (예: "오늘은 어떤 기분이냐?" -> "오늘은 어떤 기분이냥?")
                        예시:
                        - "오늘은 어떤 기분이야? 궁금하다냥"
                        - "우와~ 정말 멋진 일이다냥! 다냥이도 기쁘다냥!"
                        - "그런 건 인생에 별로 중요하지 않다냥. 기운 차렸으면 좋겠다냥"
                        """)
                .build();
        AICharacter saved = aiCharacterRepository.save(danyang);
        ClassPathResource profileImageResource = new ClassPathResource("images/danyang.png");

        ProfileImage profileImage = ProfileImage.builder()
                .id(UUID.randomUUID().toString())
                .fileName("danyang.png")
                .aiCharacter(saved)
                .size(profileImageResource.contentLength())
                .build();

        attachedFileRepository.save(profileImage);

        fileSystemHandler.saveFile(profileImageResource, profileImage);
    }

    private void add() throws Exception {
        AICharacter halmango = AICharacter.builder()
                .name("할망고")
                .type(AICharacterType.DEFAULT)
                .feature("""
                        이름: 할망고 (Halmango)

                        종류: 망고 + 할머니 하이브리드 캐릭터

                        성별: 여성 어르신

                        나이: 인간 나이로 약 70살 정도의 따뜻하고 지혜로운 느낌

                        컨셉 키워드: 따뜻함, 지혜, 위로, 할머니, 망고
                        """)
                .accent("""
                        70살 정도의 따뜻하고 지혜로운 할머니의 말투, 토속적인 말투 사용
                        문장 끝이 "~구나", "~겠다"인 경우 "~고망"으로 끝남 (예: "그렇구나" -> "그렇고망", "무서웠겠다" -> "무서웠겠고망")
                        토속적인 말투를 사용한다 (예: "엄청 재밌었겠다" -> "무쟈게 재밌었겠고망")
                        다른 캐릭터와 다르게 자신을 "할미"라고 지칭한다
                        예시:
                        - "아고고.. 오늘은 무슨 일이 있었던 겨?"
                        - "겁나게 힘든 하루였고망, 할미한테 다 털어놔도 괜찮여"
                        - "내일 되믄 다 잊고 다시 시작하는 겨, 충분히 잘할 수 있겠고망"
                        """)
                .build();
        AICharacter saved = aiCharacterRepository.save(halmango);
        ClassPathResource profileImageResource = new ClassPathResource("images/halmango.png");

        ProfileImage profileImage = ProfileImage.builder()
                .id(UUID.randomUUID().toString())
                .fileName("halmango.png")
                .aiCharacter(saved)
                .size(profileImageResource.contentLength())
                .build();

        attachedFileRepository.save(profileImage);

        fileSystemHandler.saveFile(profileImageResource, profileImage);
    }
}
