package wanted.wantedpreonboardingbackend.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wanted.wantedpreonboardingbackend.domain.member.entity.CustomUserDetails;
import wanted.wantedpreonboardingbackend.domain.member.entity.Member;
import wanted.wantedpreonboardingbackend.domain.member.repository.MemberRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Member> member = Optional.ofNullable(memberRepository.findByEmail(username));
        if(member.isPresent()){
            return new CustomUserDetails(member.get());
        }
        throw new UsernameNotFoundException("사용자를 찾을 수 없습니다");
    }
}
