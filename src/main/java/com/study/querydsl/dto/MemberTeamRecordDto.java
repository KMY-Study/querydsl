package com.study.querydsl.dto;

import com.querydsl.core.annotations.QueryProjection;

public record MemberTeamRecordDto(
        Long memberId, String username, int age, Long teamId, String teamName
) {
    @QueryProjection
    public MemberTeamRecordDto(Long memberId, String username, int age, Long teamId, String teamName) {
        this.memberId = memberId;
        this.username = username;
        this.age = age;
        this.teamId = teamId;
        this.teamName = teamName;
    }
}
