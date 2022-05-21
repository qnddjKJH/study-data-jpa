package study.datajpa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import study.datajpa.entity.Member;

@Data
@AllArgsConstructor
public class MemberDto {
    private Long id;
    private String username;
    private String teamName;

    public MemberDto(Member m) {
        this.id = m.getId();
        this.username = m.getUsername();
        this.teamName = m.getTeam().getName();
    }
}
