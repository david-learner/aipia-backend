package com.aipiabackend.member.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table(name = "member")
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(nullable = false, length = 20)
    private String password;

    @Column(nullable = false, length = 13)
    private String phone;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @Column(name = "withdrawn_at")
    private LocalDateTime withdrawnAt;

    public void withdraw() {
        this.withdrawnAt = LocalDateTime.now();
    }

    public boolean isWithdrawn() {
        return withdrawnAt != null;
    }

    public static Member of(String name, String email, String password, String phone) {
        return new Member(null, name, email, password, phone, LocalDateTime.now(), null);
    }
}