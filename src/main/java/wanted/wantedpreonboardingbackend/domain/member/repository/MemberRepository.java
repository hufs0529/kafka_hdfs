package wanted.wantedpreonboardingbackend.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wanted.wantedpreonboardingbackend.domain.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);
    Member findByEmail(String email);
}
