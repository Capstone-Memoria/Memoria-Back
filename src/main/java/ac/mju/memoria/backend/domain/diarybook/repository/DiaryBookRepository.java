package ac.mju.memoria.backend.domain.diarybook.repository;

import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DiaryBookRepository extends JpaRepository<DiaryBook, Long> {
}
