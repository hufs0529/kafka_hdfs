package wanted.wantedpreonboardingbackend.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;
import wanted.wantedpreonboardingbackend.domain.post.entity.Post;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostPagingDto {

    private List<PostInfoDto> postList;
    private int totalPageCount;
    private int curPageNum;
    private long totalPostCount;
    private int currentPagePostCount;

    public PostPagingDto(Page<Post> searchResult){
        this.totalPageCount = searchResult.getTotalPages();
        this.curPageNum = searchResult.getNumber();
        this.totalPostCount = searchResult.getTotalElements();
        this.currentPagePostCount = searchResult.getNumberOfElements();
        this.postList = searchResult.getContent().stream().map(PostInfoDto::new).toList();
    }
}
