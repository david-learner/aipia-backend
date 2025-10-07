package com.aipiabackend.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/members")
@RestController
public class MemberRestController {

    /**
     * 회원으로 가입한다
     */
    @PostMapping
    public ResponseEntity<Void> join() {
        return ResponseEntity.ok().build();
    }
}
