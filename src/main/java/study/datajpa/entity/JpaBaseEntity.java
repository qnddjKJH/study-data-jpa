package study.datajpa.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

// 진짜 상속관계는 아니고 속성들을 내려 같이 쓰는 그런 애
// 진짜 JPA 상속 관계는 따로 있음 알지? Item -> book, movie, album
@MappedSuperclass
public class JpaBaseEntity {
    // JPA 주요 어노테이션
    // @PrePersist, @PostPersist
    // @PreUpdate, @PostUpdate

    @Column(updatable = false)
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    // Persist 하기 전에 실행 (prev)
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        updatedDate = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedDate = LocalDateTime.now();
    }
}
