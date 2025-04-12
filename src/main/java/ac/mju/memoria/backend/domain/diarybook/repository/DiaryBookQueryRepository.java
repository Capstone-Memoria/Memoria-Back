package ac.mju.memoria.backend.domain.diarybook.repository;

import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import ac.mju.memoria.backend.domain.diarybook.entity.QDiaryBook;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static ac.mju.memoria.backend.domain.diarybook.entity.QDiaryBook.*;

@RequiredArgsConstructor
@Repository
public class DiaryBookQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Optional<DiaryBook> findByIdAndUserEmail(Long diaryBookId, String userEmail) {
        DiaryBook result = queryFactory
                .selectFrom(diaryBook)
                .innerJoin(diaryBook.createdBy).fetchJoin()
                .innerJoin(diaryBook.owner).fetchJoin()
                .leftJoin(diaryBook.lastModifiedBy).fetchJoin()
                .where(
                        diaryBook.id.eq(diaryBookId),
                        diaryBook.owner.email.eq(userEmail)
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }

    public Page<DiaryBook> findByUserEmailWithDetails(String userEmail, Pageable pageable) {
        List<DiaryBook> content = queryFactory
                .selectFrom(diaryBook)
                .innerJoin(diaryBook.createdBy).fetchJoin()
                .innerJoin(diaryBook.owner).fetchJoin()
                .leftJoin(diaryBook.lastModifiedBy).fetchJoin()
                .where(diaryBook.owner.email.eq(userEmail))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifiers(pageable))
                .fetch();

        Long total = queryFactory
                .select(diaryBook.count())
                .from(diaryBook)
                .where(diaryBook.owner.email.eq(userEmail))
                .fetchOne();

        long count = (total == null) ? 0L : total;

        return new PageImpl<>(content, pageable, count);
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Pageable pageable) {
        PathBuilder<DiaryBook> entityPath = new PathBuilder<>(DiaryBook.class, "diaryBook");

        return pageable.getSort().stream()
                .map(order -> new OrderSpecifier(
                        order.isAscending() ? Order.ASC : Order.DESC,
                        entityPath.get(order.getProperty())
                )).toArray(OrderSpecifier[]::new);
    }

}
