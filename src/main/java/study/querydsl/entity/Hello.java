package study.querydsl.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor
public class Hello {
    @Id
    @GeneratedValue
    private Long id;

    @Builder
    public Hello(Long id) {
        this.id = id;
    }
}
