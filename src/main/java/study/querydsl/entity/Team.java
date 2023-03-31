package study.querydsl.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@ToString( of = {"id", "teamName"} )
public class Team {

    @Id
    @GeneratedValue
    @Column(name = "team_id")
    private Long id;
    private String teamName;

    @OneToMany(mappedBy = "team")
    private List<Member> members;

    @Builder
    public Team(Long id, String teamName) {
        this.id = id;
        this.teamName = teamName;
        this.members = new ArrayList<>();
    }
}
