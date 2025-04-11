package ac.mju.memoria.backend.domain.user.entity;

import ac.mju.memoria.backend.common.auditor.TimeStampedEntity;
import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@SuperBuilder
@Table(name = "users")
public class User extends TimeStampedEntity {
    @Id
    private String email;
    private String nickName;
    private String password;

    @OneToMany(mappedBy = "createdBy", orphanRemoval = true)
    private List<DiaryBook> diaryBooks;

    public void addDiaryBook(DiaryBook diaryBook) {
        this.diaryBooks.add(diaryBook);
        diaryBook.setCreatedBy(this);
    }

    public void removeDiaryBook(DiaryBook diaryBook) {
        this.diaryBooks.remove(diaryBook);
    }
}
