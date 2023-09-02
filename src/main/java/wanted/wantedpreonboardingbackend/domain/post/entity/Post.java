package wanted.wantedpreonboardingbackend.domain.post.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import wanted.wantedpreonboardingbackend.domain.member.entity.Member;
import wanted.wantedpreonboardingbackend.domain.member.repository.MemberRepository;
import wanted.wantedpreonboardingbackend.global.entity.BaseTimeEntity;

import javax.persistence.*;

@Table(name = "post")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Post extends BaseTimeEntity {

    static MemberRepository memberRepository;

    @Autowired
    public Post(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "writer_id")
    @JsonBackReference
    private Member writer;

    @Lob
    private String content;

    private String title;

    @Builder
    public Post(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void confirmWriter(Member writer) {
        this.writer = writer;
        writer.addPost(this);
    }
}
