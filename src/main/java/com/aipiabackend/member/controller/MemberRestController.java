package com.aipiabackend.member.controller;

import com.aipiabackend.member.controller.dto.MemberJoinRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@RequestMapping("/api/members")
@RestController
public class MemberRestController {

    /**
     * 회원으로 가입한다
     */
    @PostMapping
    public ResponseEntity<Void> join(@Valid @RequestBody MemberJoinRequest request) {
        UriComponents memberUriComponents = UriComponentsBuilder
            .fromUriString("/api/members/{memberId}")
            .buildAndExpand(1L);

        return ResponseEntity.created(memberUriComponents.toUri()).build();
    }
}
