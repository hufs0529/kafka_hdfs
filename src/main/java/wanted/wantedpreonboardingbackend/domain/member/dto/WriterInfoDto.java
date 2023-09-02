package wanted.wantedpreonboardingbackend.domain.member.dto;

import lombok.Builder;
import lombok.Getter;
import wanted.wantedpreonboardingbackend.domain.member.entity.Member;

@Getter
public class WriterInfoDto {

    private String email;

    @Builder
    public WriterInfoDto(Member member) {
        this.email = member.getEmail();
    }
}
