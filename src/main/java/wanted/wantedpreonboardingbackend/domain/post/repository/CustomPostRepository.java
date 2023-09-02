package wanted.wantedpreonboardingbackend.domain.post.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import wanted.wantedpreonboardingbackend.domain.post.dto.PostSearchCondition;
import wanted.wantedpreonboardingbackend.domain.post.entity.Post;

public interface CustomPostRepository {
    Page<Post> search(PostSearchCondition postSearchCondition, Pageable pageable);

}
