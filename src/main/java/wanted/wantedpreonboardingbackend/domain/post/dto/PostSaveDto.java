package wanted.wantedpreonboardingbackend.domain.post.dto;

import wanted.wantedpreonboardingbackend.domain.post.entity.Post;

public record PostSaveDto(String title, String content) {

    public Post toEntity() {

        return Post.builder().title(title).content(content).build();
    }

}
