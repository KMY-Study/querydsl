package com.study.querydsl.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * packageName    : com.study.querydsl.entity
 * fileName       : Team
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
@ToString(of = {"id", "name"})
public class Team {

    @Id
    @GeneratedValue
    @Column(name = "team_id")
    private long id;
    private String name;

    @OneToMany(mappedBy = "team") // 연관관계의 주인이 아님을 명시
    private List<Member> members = new ArrayList<>();

    public Team(String name){
        this.name = name;
    }

}
