package wanted.wantedpreonboardingbackend.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wanted.wantedpreonboardingbackend.domain.member.dto.MemberRequestDto;
import wanted.wantedpreonboardingbackend.domain.member.entity.Member;
import wanted.wantedpreonboardingbackend.domain.member.repository.MemberRepository;
import wanted.wantedpreonboardingbackend.global.exception.DuplicateEmailException;
import wanted.wantedpreonboardingbackend.global.exception.ValidationException;
import wanted.wantedpreonboardingbackend.global.jwt.TokenProvider;
import wanted.wantedpreonboardingbackend.global.jwt.dto.TokenDto;


@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;

    @Transactional
    public void signUp(MemberRequestDto memberRequestDto) throws ValidationException, DuplicateEmailException {
        Member member = memberRequestDto.toEntity();

        member.encodePassword(passwordEncoder);

        if (memberRepository.existsByEmail(memberRequestDto.getEmail())) {
            throw new DuplicateEmailException("Email already exists");
        }
        memberRepository.save(member);
    }


    public TokenDto authenticateAndGenerateToken(MemberRequestDto memberRequestDto) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(memberRequestDto.getEmail(), memberRequestDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);

        return new TokenDto(jwt,"");
    }
}
