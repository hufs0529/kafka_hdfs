package wanted.wantedpreonboardingbackend.domain.member.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import wanted.wantedpreonboardingbackend.domain.member.entity.Member;
import org.apache.commons.lang3.StringUtils;
import javax.validation.ValidationException;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class MemberRequestDto {

    @NotBlank(message = "이메일을 입력해주세요")
    @Email(message = "유효한 이메일 주소를 입력해주세요")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;

    public MemberRequestDto(String email, String password){
        this.email = email;
        this.password = password;
    }

    public Member toEntity() {

        return Member.builder()
                .email(email)
                .password(password)
                .build();
    }

    public UsernamePasswordAuthenticationToken toAuthentication() {
        return new UsernamePasswordAuthenticationToken(email, password);
    }

    public void validate() {
        if (StringUtils.isBlank(email)) {
            throw new ValidationException("이메일을 입력해주세요");
        }
        if (StringUtils.isBlank(password)) {
            throw new ValidationException("비밀번호를 입력해주세요");
        }
    }

}
