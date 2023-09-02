package wanted.wantedpreonboardingbackend.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import wanted.wantedpreonboardingbackend.domain.member.dto.MemberRequestDto;
import wanted.wantedpreonboardingbackend.domain.member.service.MemberService;
import wanted.wantedpreonboardingbackend.global.exception.ValidationException;
import wanted.wantedpreonboardingbackend.global.jwt.JwtFilter;
import wanted.wantedpreonboardingbackend.global.jwt.dto.TokenDto;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@Validated
public class MemberController {

    private final MemberService memberService;

    private final Logger logger = LoggerFactory.getLogger(MemberController.class);


    @PostMapping("/signUp")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MemberRequestDto> signUp(@Valid @RequestBody MemberRequestDto memberRequestDto) {
        try {
            memberService.signUp(memberRequestDto);
            return ResponseEntity.ok(memberRequestDto);
        } catch (ValidationException ex) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("validationError", ex.getMessage());
            return ResponseEntity.badRequest().body((MemberRequestDto) errorMap);
        }
    }


    @PostMapping("/login")
    public ResponseEntity<TokenDto> authorize(@Valid @RequestBody MemberRequestDto memberRequestDto) {
        try {
            TokenDto tokenDto = memberService.authenticateAndGenerateToken(memberRequestDto);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + tokenDto.getToken());
            return new ResponseEntity<>(tokenDto, httpHeaders, HttpStatus.OK);
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new TokenDto(null, "인증에 실패하였습니다."));
        }
    }



}
