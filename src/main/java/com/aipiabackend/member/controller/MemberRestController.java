package com.aipiabackend.member.controller;

import com.aipiabackend.auth.model.MemberPrincipal;
import com.aipiabackend.member.controller.dto.MemberJoinRequest;
import com.aipiabackend.member.controller.dto.MemberResponse;
import com.aipiabackend.member.model.Member;
import com.aipiabackend.member.service.MemberService;
import com.aipiabackend.member.service.dto.MemberJoinCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
@RequestMapping("/api/members")
@RestController
public class MemberRestController {

    private final MemberService memberService;

    /**
     * 회원으로 가입한다
     */
    @PostMapping
    public ResponseEntity<Void> join(@Valid @RequestBody MemberJoinRequest request) {
        MemberJoinCommand command = new MemberJoinCommand(
            request.name(),
            request.email(),
            request.password(),
            request.phone()
        );

        Member savedMember = memberService.join(command);

        UriComponents memberUriComponents = UriComponentsBuilder
            .fromUriString("/api/members/{memberId}")
            .buildAndExpand(savedMember.getId());

        return ResponseEntity.created(memberUriComponents.toUri()).build();
    }

    /**
     * 회원 본인의 정보를 조회한다
     */
    @GetMapping("/me")
    public ResponseEntity<MemberResponse> retrieveMe(
        @AuthenticationPrincipal MemberPrincipal principal
    ) {
        Member member = memberService.retrieveMe(principal.getMemberId());
        return ResponseEntity.ok(MemberResponse.from(member));
    }

    /**
     * 회원 정보를 조회한다
     */
    @GetMapping("/{memberId}")
    public ResponseEntity<MemberResponse> retrieve(
        @PathVariable Long memberId
    ) {
        Member member = memberService.retrieveMember(memberId);
        return ResponseEntity.ok(MemberResponse.from(member));
    }
}
