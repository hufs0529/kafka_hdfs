package wanted.wantedpreonboardingbackend.domain.member.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import wanted.wantedpreonboardingbackend.domain.post.entity.Post;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Setter
@Getter
@Table(name="member")
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    @OneToMany(mappedBy = "writer")
    @JsonManagedReference
    private List<Post> postList = new ArrayList<>();


    @Builder
    public Member(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public void addPost(Post post) {
        postList.add(post);
    }


    public void encodePassword(PasswordEncoder passwordEncoder){
        this.password = passwordEncoder.encode(password);
    }


}
