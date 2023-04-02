package study.querydsl.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class MemberSearchCondition {

    private String userName;
    private String teamName;
    private Integer ageGoe;
    private Integer ageLoe;

    @Builder
    public MemberSearchCondition(String userName, String teamName, Integer ageGoe, Integer ageLoe) {
        this.userName = userName;
        this.teamName = teamName;
        this.ageGoe = ageGoe;
        this.ageLoe = ageLoe;
    }
}
