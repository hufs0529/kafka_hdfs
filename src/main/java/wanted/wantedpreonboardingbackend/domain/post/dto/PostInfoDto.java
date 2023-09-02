package wanted.wantedpreonboardingbackend.domain.post.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import wanted.wantedpreonboardingbackend.domain.member.dto.WriterInfoDto;
import wanted.wantedpreonboardingbackend.domain.post.entity.Post;

@Data
@NoArgsConstructor
public class PostInfoDto {

    private Long postId;
    private String title;
    private String content;

    private WriterInfoDto writerInfoDto;

    public PostInfoDto(Post post) {
        this.postId = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.writerInfoDto = new WriterInfoDto(post.getWriter());
    }
}
