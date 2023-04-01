package study.querydsl.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDto {

    private String name;
    private int age;

    @Builder
    public UserDto(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
