package ac.mju.memoria.backend.domain.diarybook.repository;

import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBookMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DiaryBookMemberRepository extends JpaRepository<DiaryBookMember, Long> {
    Optional<DiaryBookMember> findByIdAndDiaryBookId(Long memberId, Long diaryBookId);

    List<DiaryBookMember> findAllByDiaryBookId(Long diaryBookId);
}
