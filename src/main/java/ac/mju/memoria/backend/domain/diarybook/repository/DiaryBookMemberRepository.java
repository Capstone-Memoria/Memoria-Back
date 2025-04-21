package ac.mju.memoria.backend.domain.diarybook.repository;

import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBookMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryBookMemberRepository extends JpaRepository<DiaryBookMember, Long> {
}
