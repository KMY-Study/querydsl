package com.study.querydsl.dto;

import lombok.Data;

/**
 * packageName    : com.study.querydsl.dto
 * fileName       : MemberSearchCondition
 * author         : kmy
 * date           : 5/29/24
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 5/29/24        kmy       최초 생성
 */
@Data
public class MemberSearchCondition {
    // 회원명, 팀명, 나이
    private String username;
    private String teamname;
    private Integer ageGoe;
    private Integer ageLoe;
}
