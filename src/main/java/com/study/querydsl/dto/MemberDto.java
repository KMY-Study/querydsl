package com.study.querydsl.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberDto {

    private String username;
    private int age;

    @QueryProjection//DTO도 Qclass생성,,, <- QueryDSL에 의존적이게 설계된다는 단점,
    public MemberDto(String username, int age){
        this.username = username;
        this.age = age;
    }
}
