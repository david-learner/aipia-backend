package com.aipiabackend.member.model;

public enum MemberGrade {
    MEMBER,
    ADMIN;

    public boolean isAdmin() {
        return this == ADMIN;
    }
}
