package ac.mju.memoria.backend.domain.diarybook.repository;

import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DiaryBookRepository extends JpaRepository<DiaryBook, Integer> {

    @Query("SELECT db FROM DiaryBook db " +
            "JOIN FETCH db.createdBy " +
            "LEFT JOIN FETCH db.lastModifiedBy " +
            "WHERE db.id = :diaryBookId AND db.createdBy.email = :userEmail")
    Optional<DiaryBook> findByIdAndUserEmail(@Param("diaryBookId") Integer diaryBookId, @Param("userEmail") String userEmail);

    @Modifying
    @Query("DELETE FROM DiaryBook db WHERE db.id = :diaryBookId AND db.createdBy.email = :userEmail")
    int deleteByIdAndUserEmail(@Param("diaryBookId") Integer diaryBookId, @Param("userEmail") String userEmail);

    @Query(value = "SELECT db FROM DiaryBook db " +
            "JOIN FETCH db.createdBy " +
            "LEFT JOIN FETCH db.lastModifiedBy " +
            "WHERE db.createdBy.email = :userEmail",
            countQuery = "SELECT count(db) FROM DiaryBook db WHERE db.createdBy.email = :userEmail")
    Page<DiaryBook> findByUserEmailWithDetails(@Param("userEmail") String userEmail, Pageable pageable);
}
