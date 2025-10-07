package com.aipiabackend.member.controller;

import com.aipiabackend.member.controller.dto.MemberJoinRequest;
import com.aipiabackend.member.model.Member;
import com.aipiabackend.member.service.MemberService;
import com.aipiabackend.member.service.dto.MemberJoinCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
}
