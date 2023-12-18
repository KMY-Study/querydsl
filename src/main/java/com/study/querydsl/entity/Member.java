package com.study.querydsl.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * packageName    : com.study.querydsl.entity
 * fileName       : Member
 * author         : kmy
 * date           : 12/18/23
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 12/18/23        kmy       최초 생성
 */
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "username", "age"}) // 소유한 필드만,, 연관관계 필드를 할 경우 무한루프 발생 가능성 있음,,
public class Member {

    @Id
    @GeneratedValue
    @Column(name="member_id")
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public Member(String username){
        this(username, 0);
    }

    public Member(String username, int age){
        this(username, age , null);
    }

    public Member(String username, int age, Team team){
        this.username = username;
        this.age = age;
        if(team != null){
            changeTeam(team);
        }
    }

    public void changeTeam(Team team){
        this.team = team;
        team.getMembers().add(this);
    }

}
