package wanted.wantedpreonboardingbackend.domain.post.dto;

import java.util.Optional;

public record PostUpdateDto(
        Optional<String> title,
        Optional<String> content){
}