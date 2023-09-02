package wanted.wantedpreonboardingbackend.domain.member.dto;

import lombok.Builder;
import lombok.Getter;
import wanted.wantedpreonboardingbackend.domain.member.entity.Member;

@Getter
public class MemberInfoDto {

    private String email;
    private String password;

    @Builder
    public MemberInfoDto(Member member) {
        this.email = member.getEmail();
        this.password = member.getPassword();
    }
}
