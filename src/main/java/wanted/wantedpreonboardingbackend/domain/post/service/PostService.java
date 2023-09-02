package wanted.wantedpreonboardingbackend.domain.post.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wanted.wantedpreonboardingbackend.domain.member.entity.Member;
import wanted.wantedpreonboardingbackend.domain.member.repository.MemberRepository;
import wanted.wantedpreonboardingbackend.domain.post.dto.*;
import wanted.wantedpreonboardingbackend.domain.post.entity.Post;
import wanted.wantedpreonboardingbackend.domain.post.repository.CustomPostRepository;
import wanted.wantedpreonboardingbackend.domain.post.repository.PostRepository;
import wanted.wantedpreonboardingbackend.global.exception.PostNotFoundException;
import wanted.wantedpreonboardingbackend.global.exception.UserLoginException;


@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CustomPostRepository customPostRepository;

    public String checkWriter() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserDetails)) {
            throw new UserLoginException("로그인된 사용자가 없습니다.");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        return email;
    }

    public Post save(PostSaveDto postSaveDto) throws Exception {
        Post post = postSaveDto.toEntity();
        Member member = memberRepository.findByEmail(checkWriter());
        post.confirmWriter(member);
        postRepository.save(post);
        return post;
    }

    public PostPagingDto getPostList(Pageable pageable, PostSearchCondition postSearchCondition) {
        return new PostPagingDto(customPostRepository.search(postSearchCondition, pageable));
    }


    public PostInfoDto getPostInfo(Long postId) {
        return new PostInfoDto(postRepository.findWriterById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시물이 존재하지 않습니다.")));
    }

    public PostUpdateDto update(Long postId, PostUpdateDto postUpdateDto) throws Exception {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("게시물이 존재하지 않습니다."));
        Member member = memberRepository.findByEmail(checkWriter());

        if (member.getEmail().equals(post.getWriter().getEmail())) {
            if (postUpdateDto.title().isPresent()) {
                post.setTitle(postUpdateDto.title().get());
            }
            if (postUpdateDto.content().isPresent()) {
                post.setContent(postUpdateDto.content().get());
            }
        }
        return postUpdateDto;
    }

    public String delete(Long postId) throws Exception {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("게시물이 존재하지 않습니다."));
        Member member = memberRepository.findByEmail(checkWriter());

        if (member.getEmail().equals(post.getWriter().getEmail())){
            postRepository.deleteById(postId);
        }
        return postId + " id의 게시물이 지워졌습니다";
    }
}
